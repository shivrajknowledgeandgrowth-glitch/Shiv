package com.example.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sqrt

private const val TAG = "RealStepTracker"

enum class SensorTrackingMode(val displayName: String) {
    STEP_DETECTOR("Hardware Step Detector"),
    STEP_COUNTER("Hardware Step Counter"),
    ACCELEROMETER_PEDOMETER("Accelerometer Pedometer"),
    UNAVAILABLE("No Motion Sensor Available")
}

data class SensorStatusInfo(
    val mode: SensorTrackingMode,
    val sensorName: String,
    val isActive: Boolean,
    val isRealHardware: Boolean
)

/**
 * Real device hardware step tracker for Android.
 * Leverages device hardware TYPE_STEP_DETECTOR or TYPE_STEP_COUNTER,
 * and falls back to precision Accelerometer 3D-vector peak detection when
 * dedicated step counter hardware is unavailable (e.g. emulators or certain devices).
 */
class RealStepTracker(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private var stepDetectorSensor: Sensor? = null
    private var stepCounterSensor: Sensor? = null
    private var accelerometerSensor: Sensor? = null

    private var activeSensor: Sensor? = null
    private var trackingMode = SensorTrackingMode.UNAVAILABLE

    // Baseline calculation for TYPE_STEP_COUNTER (which reports total steps since reboot)
    private var initialCounterBaseline = -1f
    private var previousCounterValue = -1f

    // Accelerometer peak detection algorithm variables
    private var lastAccelerationMagnitude = 9.8f
    private var isAboveThreshold = false
    private var lastStepTimestampMs = 0L

    private val _sensorStatus = MutableStateFlow(
        SensorStatusInfo(
            mode = SensorTrackingMode.UNAVAILABLE,
            sensorName = "Initializing...",
            isActive = false,
            isRealHardware = false
        )
    )
    val sensorStatus: StateFlow<SensorStatusInfo> = _sensorStatus.asStateFlow()

    private var stepCallback: ((stepDelta: Int) -> Unit)? = null

    init {
        stepDetectorSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        stepCounterSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        accelerometerSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        selectOptimalSensor()
    }

    private fun selectOptimalSensor() {
        when {
            stepDetectorSensor != null -> {
                activeSensor = stepDetectorSensor
                trackingMode = SensorTrackingMode.STEP_DETECTOR
            }
            stepCounterSensor != null -> {
                activeSensor = stepCounterSensor
                trackingMode = SensorTrackingMode.STEP_COUNTER
            }
            accelerometerSensor != null -> {
                activeSensor = accelerometerSensor
                trackingMode = SensorTrackingMode.ACCELEROMETER_PEDOMETER
            }
            else -> {
                activeSensor = null
                trackingMode = SensorTrackingMode.UNAVAILABLE
            }
        }

        updateStatus(isActive = false)
    }

    private fun updateStatus(isActive: Boolean) {
        val name = activeSensor?.name ?: "No Motion Sensor Detected"
        _sensorStatus.value = SensorStatusInfo(
            mode = trackingMode,
            sensorName = name,
            isActive = isActive,
            isRealHardware = trackingMode != SensorTrackingMode.UNAVAILABLE
        )
    }

    fun startTracking(onStepDetected: (stepDelta: Int) -> Unit) {
        this.stepCallback = onStepDetected

        if (sensorManager == null || activeSensor == null) {
            Log.w(TAG, "No motion sensors available to track real steps.")
            updateStatus(isActive = false)
            return
        }

        // Register listener with normal/game sensor delay for responsive step detection
        val samplingPeriodUs = when (trackingMode) {
            SensorTrackingMode.ACCELEROMETER_PEDOMETER -> SensorManager.SENSOR_DELAY_GAME
            else -> SensorManager.SENSOR_DELAY_UI
        }

        val registered = sensorManager.registerListener(this, activeSensor, samplingPeriodUs)
        if (registered) {
            Log.i(TAG, "Real Step Tracking registered using: ${activeSensor?.name} ($trackingMode)")
            updateStatus(isActive = true)
        } else {
            Log.e(TAG, "Failed to register sensor listener for ${activeSensor?.name}")
            // Fall back to accelerometer if hardware step detector failed to bind
            if (activeSensor != accelerometerSensor && accelerometerSensor != null) {
                activeSensor = accelerometerSensor
                trackingMode = SensorTrackingMode.ACCELEROMETER_PEDOMETER
                val accelRegistered = sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME)
                updateStatus(isActive = accelRegistered)
            } else {
                updateStatus(isActive = false)
            }
        }
    }

    fun stopTracking() {
        try {
            sensorManager?.unregisterListener(this)
        } catch (e: Exception) {
            Log.e(TAG, "Error unregistering sensor listener", e)
        } finally {
            updateStatus(isActive = false)
            stepCallback = null
            initialCounterBaseline = -1f
            previousCounterValue = -1f
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_STEP_DETECTOR -> {
                // Event value is 1.0 each time a step is physically taken
                val detected = event.values.firstOrNull() ?: 0f
                if (detected > 0f) {
                    stepCallback?.invoke(1)
                }
            }

            Sensor.TYPE_STEP_COUNTER -> {
                // Event value is total cumulative steps since phone booted
                val currentTotal = event.values.firstOrNull() ?: 0f
                if (currentTotal > 0f) {
                    if (initialCounterBaseline < 0f) {
                        initialCounterBaseline = currentTotal
                        previousCounterValue = currentTotal
                    } else {
                        val delta = (currentTotal - previousCounterValue).toInt()
                        if (delta > 0 && delta < 500) { // Safety ceiling to discard sudden boot counter anomalies
                            stepCallback?.invoke(delta)
                            previousCounterValue = currentTotal
                        } else if (delta < 0) {
                            // Device was rebooted in the background
                            initialCounterBaseline = currentTotal
                            previousCounterValue = currentTotal
                        }
                    }
                }
            }

            Sensor.TYPE_ACCELEROMETER -> {
                // Peak detection algorithm across 3D gravity vector
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val magnitude = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

                val now = System.currentTimeMillis()
                // Thresholds: normal gravity is ~9.8 m/s^2.
                // Walking impact causes a dynamic spike above 11.2 m/s^2 followed by a trough below 9.3 m/s^2.
                // Min time between steps is 280ms (at most ~3.5 steps/sec during running) to prevent noise.
                if (!isAboveThreshold && magnitude > 11.2f) {
                    if (now - lastStepTimestampMs > 280) {
                        isAboveThreshold = true
                        lastStepTimestampMs = now
                        stepCallback?.invoke(1)
                    }
                } else if (isAboveThreshold && magnitude < 9.3f) {
                    isAboveThreshold = false
                }

                lastAccelerationMagnitude = magnitude
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Sensor accuracy changes (e.g. SENSOR_STATUS_ACCURACY_HIGH)
    }
}
