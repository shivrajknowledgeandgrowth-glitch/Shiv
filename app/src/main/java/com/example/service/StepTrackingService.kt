package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.ShivAiDatabase
import com.example.data.local.StepData
import com.example.data.repository.ShivAiRepository
import com.example.sensor.RealStepTracker
import com.example.sensor.SensorStatusInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "StepTrackingService"
private const val NOTIFICATION_ID = 1001
private const val CHANNEL_ID = "shivai_24h_fitness_channel"

/**
 * 24-Hour Continuous Background Step Tracking Foreground Service.
 * Tracks physical user steps even when the app is completely closed or screen is off.
 * Automatically archives data at midnight, persists updates into Room Database,
 * and restarts automatically via BootReceiver when the device reboots.
 */
class StepTrackingService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var repository: ShivAiRepository? = null
    private var stepTracker: RealStepTracker? = null

    private var activeDateString: String = ""
    private var lastSavedSteps: Int = 0
    private var lastSavedTimestamp: Long = 0L

    companion object {
        private val _liveSteps = MutableStateFlow(0)
        val liveSteps: StateFlow<Int> = _liveSteps.asStateFlow()

        private val _liveCalories = MutableStateFlow(0)
        val liveCalories: StateFlow<Int> = _liveCalories.asStateFlow()

        private val _isServiceRunning = MutableStateFlow(false)
        val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()

        private val _sensorStatus = MutableStateFlow<SensorStatusInfo?>(null)
        val sensorStatus: StateFlow<SensorStatusInfo?> = _sensorStatus.asStateFlow()

        fun start(context: Context) {
            try {
                val intent = Intent(context, StepTrackingService::class.java)
                ContextCompat.startForegroundService(context, intent)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start StepTrackingService", e)
            }
        }

        fun stop(context: Context) {
            try {
                val intent = Intent(context, StepTrackingService::class.java)
                context.stopService(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to stop StepTrackingService", e)
            }
        }

        fun updateLiveStepsManually(newSteps: Int) {
            _liveSteps.value = newSteps
            _liveCalories.value = (newSteps * 0.04).toInt()
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "StepTrackingService onCreate - 24/7 background tracking initializing")
        _isServiceRunning.value = true

        val database = ShivAiDatabase.getDatabase(applicationContext)
        repository = ShivAiRepository(database)
        stepTracker = RealStepTracker(applicationContext)

        activeDateString = getTodayDateString()

        createNotificationChannel()
        startAsForeground()

        // Load today's persisted steps from Room DB
        serviceScope.launch {
            val savedToday = repository?.getStepDataByDateOnce(activeDateString)
            if (savedToday != null) {
                _liveSteps.value = savedToday.steps
                _liveCalories.value = savedToday.calories
                lastSavedSteps = savedToday.steps
            }
            updateNotification(_liveSteps.value, _liveCalories.value)
        }

        // Collect and forward sensor status
        serviceScope.launch {
            stepTracker?.sensorStatus?.collect { status ->
                _sensorStatus.value = status
            }
        }

        // Start hardware sensor tracking
        stepTracker?.startTracking { delta ->
            handleStepDetected(delta)
        }
    }

    private fun handleStepDetected(delta: Int) {
        val todayStr = getTodayDateString()

        // 1. Check for midnight date change
        if (todayStr != activeDateString) {
            Log.i(TAG, "Midnight date rollover detected: $activeDateString -> $todayStr")
            // Save final day count for yesterday
            persistToDatabase(activeDateString, _liveSteps.value, _liveCalories.value, force = true)
            // Reset daily counters for new day
            activeDateString = todayStr
            _liveSteps.value = delta
            _liveCalories.value = (delta * 0.04).toInt()
            lastSavedSteps = 0
        } else {
            val updatedSteps = _liveSteps.value + delta
            val updatedCalories = (updatedSteps * 0.04).toInt()
            _liveSteps.value = updatedSteps
            _liveCalories.value = updatedCalories
        }

        val currentSteps = _liveSteps.value
        val currentCalories = _liveCalories.value

        // 2. Debounced save to Room DB (every 10 steps or every 10 seconds)
        val now = System.currentTimeMillis()
        if (currentSteps - lastSavedSteps >= 10 || (now - lastSavedTimestamp > 10_000L && currentSteps != lastSavedSteps)) {
            persistToDatabase(activeDateString, currentSteps, currentCalories, force = false)
            updateNotification(currentSteps, currentCalories)
        }
    }

    private fun persistToDatabase(dateStr: String, steps: Int, calories: Int, force: Boolean) {
        serviceScope.launch {
            try {
                val existing = repository?.getStepDataByDateOnce(dateStr)
                val distanceKm = (steps * 0.00075f)
                val stepData = StepData(
                    id = existing?.id ?: 0,
                    dateString = dateStr,
                    steps = steps,
                    calories = calories,
                    distanceKm = (distanceKm * 10).toInt() / 10f,
                    dailyGoal = existing?.dailyGoal ?: 6000,
                    timestamp = System.currentTimeMillis()
                )
                repository?.saveStepData(stepData)
                lastSavedSteps = steps
                lastSavedTimestamp = System.currentTimeMillis()
                Log.d(TAG, "Persisted 24h step data to Room: $steps steps for $dateStr")
            } catch (e: Exception) {
                Log.e(TAG, "Error persisting 24h step data", e)
            }
        }
    }

    private fun startAsForeground() {
        val notification = buildNotification(_liveSteps.value, _liveCalories.value)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun updateNotification(steps: Int, calories: Int) {
        val notification = buildNotification(steps, calories)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        manager?.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(steps: Int, calories: Int): Notification {
        val distanceKm = (steps * 0.00075f)
        val contentText = "आजची पावले: $steps | $calories kcal | ${String.format(Locale.getDefault(), "%.1f", distanceKm)} km"

        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("ShivAI २४ तास फिटनेस ट्रॅकर चालू आहे 🏃")
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$contentText\nदिवसभर पावले अखंड ट्रॅक होत आहेत."))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "ShivAI 24/7 फिटनेस ट्रॅकर",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "अखंड २४ तास पावले आणि फिटनेस माहिती मोजण्यासाठी"
                setShowBadge(false)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Return START_STICKY to guarantee the system restarts this 24h tracking service if killed
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        Log.i(TAG, "StepTrackingService onDestroy - saving final steps")
        _isServiceRunning.value = false
        stepTracker?.stopTracking()
        persistToDatabase(activeDateString, _liveSteps.value, _liveCalories.value, force = true)
        serviceScope.cancel()
        super.onDestroy()
    }
}
