package com.security.shield

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MyAdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Log.d(TAG, "Device Admin activated successfully")
        LoggerUtil.initialize(context)
        LoggerUtil.log("Device Admin enabled")
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Log.d(TAG, "Device Admin disabled")
        LoggerUtil.log("Device Admin disabled")
    }

    override fun onPasswordChanged(context: Context, intent: Intent) {
        super.onPasswordChanged(context, intent)
        Log.d(TAG, "Device password changed")
    }

    override fun onLockTaskModeEntering(context: Context, intent: Intent, pkg: String) {
        super.onLockTaskModeEntering(context, intent, pkg)
        Log.d(TAG, "Entering lock task mode for: $pkg")
    }

    override fun onLockTaskModeExiting(context: Context, intent: Intent) {
        super.onLockTaskModeExiting(context, intent)
        Log.d(TAG, "Exiting lock task mode")
    }

    companion object {
        private const val TAG = "MyAdminReceiver"
    }
}
