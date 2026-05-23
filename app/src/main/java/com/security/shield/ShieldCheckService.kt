package com.security.shield

import android.app.Service
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCancellableCoroutine

class ShieldCheckService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default)
    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var adminComponent: ComponentName

    override fun onCreate() {
        super.onCreate()
        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponent = ComponentName(this, MyAdminReceiver::class.java)
        Log.d(TAG, "ShieldCheckService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return when (intent?.action) {
            ACTION_LOCK_DEVICE -> {
                serviceScope.launch {
                    lockDevice(
                        intent.getStringExtra(EXTRA_MESSAGE) ?: "Device Locked"
                    )
                }
                START_STICKY
            }
            ACTION_CHECK_STATUS -> {
                checkRemoteLockStatus()
                START_STICKY
            }
            else -> START_STICKY
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    /**
     * Verrouille l'appareil avec un message dissuasif
     */
    private suspend fun lockDevice(message: String) = suspendCancellableCoroutine { continuation ->
        try {
            if (isDeviceAdminActive()) {
                // Afficher le message via notification ou dialog
                showLockMessage(message)
                
                // Verrouiller l'écran
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    devicePolicyManager.lockNow(DevicePolicyManager.FLAG_EVICT_CREDENTIAL_ENCRYPTION_KEY)
                } else {
                    @Suppress("DEPRECATION")
                    devicePolicyManager.lockNow()
                }
                
                LoggerUtil.log("Device locked with message: $message")
                Log.d(TAG, "Device locked successfully")
                continuation.resume(Unit)
            } else {
                Log.e(TAG, "Device Admin not active")
                LoggerUtil.log("Device Admin not active - cannot lock")
                continuation.resume(Unit)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error locking device: ${e.message}", e)
            LoggerUtil.log("Lock error: ${e.message}")
            continuation.resume(Unit)
        }
    }

    /**
     * Vérifie le statut du verrouillage depuis Supabase
     */
    private fun checkRemoteLockStatus() {
        serviceScope.launch {
            try {
                val supabaseClient = SupabaseClient.getInstance(applicationContext)
                val lockStatus = supabaseClient.checkLockStatus()
                
                if (lockStatus.shouldLock) {
                    lockDevice(lockStatus.message ?: "Device Secured by Shield")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking lock status: ${e.message}", e)
                LoggerUtil.log("Status check error: ${e.message}")
            }
        }
    }

    /**
     * Affiche le message dissuasif
     */
    private fun showLockMessage(message: String) {
        // Note: Ces notifications s'affichent après le verrouillage
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("lock_message", message)
        }
        startActivity(intent)
    }

    /**
     * Vérifie si Device Admin est activé
     */
    private fun isDeviceAdminActive(): Boolean {
        return devicePolicyManager.isAdminActive(adminComponent)
    }

    companion object {
        private const val TAG = "ShieldCheckService"
        const val ACTION_LOCK_DEVICE = "com.security.shield.action.LOCK_DEVICE"
        const val ACTION_CHECK_STATUS = "com.security.shield.action.CHECK_STATUS"
        const val EXTRA_MESSAGE = "lock_message"
    }
}
