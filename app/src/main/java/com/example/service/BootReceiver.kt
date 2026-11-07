package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

private const val TAG = "ShivAiBootReceiver"

/**
 * BroadcastReceiver to automatically resume 24/7 background fitness tracking
 * immediately when the Android mobile device completes boot or when app updates.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        val action = intent.action
        Log.i(TAG, "BootReceiver received action: $action")

        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            Log.i(TAG, "Restarting 24/7 StepTrackingService after system reboot")
            StepTrackingService.start(context)
        }
    }
}
