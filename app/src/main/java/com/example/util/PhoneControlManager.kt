package com.example.util

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "PhoneControlManager"

enum class SettingsShortcut(val titleMarathi: String) {
    MAIN("मुख्य सेटिंग्ज"),
    WIFI("वाय-फाय सेटिंग्ज"),
    BLUETOOTH("ब्लूटूथ सेटिंग्ज"),
    SOUND("आवाज सेटिंग्ज"),
    DISPLAY("स्क्रीन सेटिंग्ज")
}

/**
 * Handles real Android system controls and device hardware:
 * - Torch / Flashlight (CameraManager)
 * - Volume adjustment / Mute (AudioManager)
 * - Phone Dialer / Calls
 * - WhatsApp messaging
 * - SMS / Messaging
 * - Alarm & Timer
 * - Camera
 * - System Settings shortcuts
 * - Google Maps & Navigation
 * - YouTube
 */
class PhoneControlManager(private val context: Context) {

    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

    private val _isTorchOn = MutableStateFlow(false)
    val isTorchOn: StateFlow<Boolean> = _isTorchOn.asStateFlow()

    init {
        registerTorchCallback()
    }

    private fun registerTorchCallback() {
        try {
            cameraManager?.registerTorchCallback(object : CameraManager.TorchCallback() {
                override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                    super.onTorchModeChanged(cameraId, enabled)
                    _isTorchOn.value = enabled
                }
            }, null)
        } catch (e: Exception) {
            Log.e(TAG, "Error registering torch callback", e)
        }
    }

    // --- 1. TORCH / FLASHLIGHT CONTROL ---
    fun toggleTorch(turnOn: Boolean? = null): Boolean {
        return try {
            val rearCameraId = cameraManager?.cameraIdList?.firstOrNull { id ->
                val characteristics = cameraManager.getCameraCharacteristics(id)
                val hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                val isRear = characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
                hasFlash && isRear
            } ?: cameraManager?.cameraIdList?.firstOrNull()

            if (rearCameraId == null) {
                Toast.makeText(context, "फ्लॅशलाईट हार्डवेअर उपलब्ध नाही", Toast.LENGTH_SHORT).show()
                return false
            }

            val targetState = turnOn ?: !_isTorchOn.value
            cameraManager?.setTorchMode(rearCameraId, targetState)
            _isTorchOn.value = targetState
            val msg = if (targetState) "टॉर्च चालू केला 🔦" else "टॉर्च बंद केला"
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            true
        } catch (e: CameraAccessException) {
            Log.e(TAG, "CameraAccessException toggling torch", e)
            Toast.makeText(context, "कॅमेरा वापर चालू असल्याने टॉर्च चालू करता आला नाही", Toast.LENGTH_SHORT).show()
            false
        } catch (e: Exception) {
            Log.e(TAG, "Failed to toggle torch", e)
            Toast.makeText(context, "टॉर्च एरर: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    // --- 2. VOLUME CONTROLS ---
    fun raiseVolume(): String {
        return try {
            audioManager?.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_RAISE,
                AudioManager.FLAG_SHOW_UI
            )
            val current = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 0
            val max = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 15
            val percent = ((current.toFloat() / max.coerceAtLeast(1)) * 100).toInt()
            "आवाज वाढवला ($percent%) 🔊"
        } catch (e: Exception) {
            Log.e(TAG, "Error raising volume", e)
            "आवाज बदलण्यात अडचण आली"
        }
    }

    fun lowerVolume(): String {
        return try {
            audioManager?.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_LOWER,
                AudioManager.FLAG_SHOW_UI
            )
            val current = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 0
            val max = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 15
            val percent = ((current.toFloat() / max.coerceAtLeast(1)) * 100).toInt()
            "आवाज कमी केला ($percent%) 🔉"
        } catch (e: Exception) {
            Log.e(TAG, "Error lowering volume", e)
            "आवाज बदलण्यात अडचण आली"
        }
    }

    fun muteVolume(): String {
        return try {
            audioManager?.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_MUTE,
                AudioManager.FLAG_SHOW_UI
            )
            "आवाज शांत (Mute) केला 🔇"
        } catch (e: Exception) {
            Log.e(TAG, "Error muting volume", e)
            "म्युट करण्यात अडचण आली"
        }
    }

    fun maxVolume(): String {
        return try {
            val max = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 15
            audioManager?.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                max,
                AudioManager.FLAG_SHOW_UI
            )
            "आवाज १००% फुल केला 🔊"
        } catch (e: Exception) {
            Log.e(TAG, "Error setting max volume", e)
            "आवाज बदलण्यात अडचण आली"
        }
    }

    // --- 3. PHONE CALL / DIALER ---
    fun openDialer(phoneNumber: String? = null): Boolean {
        return try {
            val intent = if (phoneNumber.isNullOrBlank()) {
                Intent(Intent.ACTION_DIAL).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            } else {
                val clean = phoneNumber.filter { it.isDigit() || it == '+' }
                Intent(Intent.ACTION_DIAL, Uri.parse("tel:$clean")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error opening dialer", e)
            Toast.makeText(context, "डायलर उघडता आला नाही", Toast.LENGTH_SHORT).show()
            false
        }
    }

    // --- 4. WHATSAPP ---
    fun openWhatsApp(phoneNumber: String? = null, prefillText: String? = null): Boolean {
        return try {
            val intent = if (!phoneNumber.isNullOrBlank()) {
                val cleanNumber = phoneNumber.filter { it.isDigit() }
                val encodedText = Uri.encode(prefillText ?: "")
                Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$cleanNumber&text=$encodedText")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    setPackage("com.whatsapp")
                }
            } else {
                val launchIntent = context.packageManager.getLaunchIntentForPackage("com.whatsapp")
                launchIntent?.apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                } ?: Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error opening WhatsApp", e)
            Toast.makeText(context, "व्हॉट्सअ‍ॅप उघडता आले नाही", Toast.LENGTH_SHORT).show()
            false
        }
    }

    // --- 5. SMS / MESSAGING ---
    fun openSms(phoneNumber: String? = null, messageBody: String? = null): Boolean {
        return try {
            val uri = if (!phoneNumber.isNullOrBlank()) {
                Uri.parse("smsto:${phoneNumber.filter { it.isDigit() || it == '+' }}")
            } else {
                Uri.parse("sms:")
            }
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                if (!messageBody.isNullOrBlank()) {
                    putExtra("sms_body", messageBody)
                }
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error opening SMS", e)
            Toast.makeText(context, "मेसेजिंग उघडता आले नाही", Toast.LENGTH_SHORT).show()
            false
        }
    }

    // --- 6. ALARM & TIMER ---
    fun openAlarmClock(): Boolean {
        return try {
            val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error opening alarms", e)
            try {
                val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                true
            } catch (e2: Exception) {
                Toast.makeText(context, "अलार्म क्लॉक उघडता आले नाही", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }

    fun setTimer(seconds: Int = 300, label: String = "ShivAI टाइमर"): Boolean {
        return try {
            val intent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(AlarmClock.EXTRA_LENGTH, seconds)
                putExtra(AlarmClock.EXTRA_MESSAGE, label)
                putExtra(AlarmClock.EXTRA_SKIP_UI, false)
            }
            context.startActivity(intent)
            Toast.makeText(context, "$label सेट केला ($seconds सेकंद)", Toast.LENGTH_SHORT).show()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error setting timer", e)
            openAlarmClock()
            false
        }
    }

    // --- 7. CAMERA ---
    fun openCamera(): Boolean {
        return try {
            val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error opening camera", e)
            try {
                val fallbackIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(fallbackIntent)
                true
            } catch (e2: Exception) {
                Toast.makeText(context, "कॅमेरा उघडता आला नाही", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }

    // --- 8. SYSTEM SETTINGS ---
    fun openSettings(shortcut: SettingsShortcut = SettingsShortcut.MAIN): Boolean {
        val action = when (shortcut) {
            SettingsShortcut.MAIN -> Settings.ACTION_SETTINGS
            SettingsShortcut.WIFI -> Settings.ACTION_WIFI_SETTINGS
            SettingsShortcut.BLUETOOTH -> Settings.ACTION_BLUETOOTH_SETTINGS
            SettingsShortcut.SOUND -> Settings.ACTION_SOUND_SETTINGS
            SettingsShortcut.DISPLAY -> Settings.ACTION_DISPLAY_SETTINGS
        }
        return try {
            val intent = Intent(action).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error opening settings $shortcut", e)
            try {
                context.startActivity(Intent(Settings.ACTION_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
                true
            } catch (e2: Exception) {
                Toast.makeText(context, "सेटिंग्ज उघडता आली नाहीत", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }

    // --- 9. MAPS & NAVIGATION ---
    fun openMaps(query: String? = null): Boolean {
        return try {
            val uri = if (query.isNullOrBlank()) {
                Uri.parse("geo:0,0?q=")
            } else {
                Uri.parse("geo:0,0?q=${Uri.encode(query)}")
            }
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error opening maps", e)
            try {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://maps.google.com/?q=${Uri.encode(query ?: "")}")
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(browserIntent)
                true
            } catch (e2: Exception) {
                Toast.makeText(context, "नकाशा उघडता आला नाही", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }

    // --- 10. YOUTUBE / MUSIC ---
    fun openYouTube(searchQuery: String? = null): Boolean {
        return try {
            val uri = if (searchQuery.isNullOrBlank()) {
                Uri.parse("https://www.youtube.com")
            } else {
                Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(searchQuery)}")
            }
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error opening YouTube", e)
            Toast.makeText(context, "युट्यूब उघडता आले नाही", Toast.LENGTH_SHORT).show()
            false
        }
    }
}
