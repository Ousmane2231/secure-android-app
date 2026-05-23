package com.security.shield

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var adminComponent: ComponentName
    private lateinit var statusTextView: TextView
    private lateinit var activateAdminButton: Button
    private lateinit var lockDeviceButton: Button
    private lateinit var checkStatusButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponent = ComponentName(this, MyAdminReceiver::class.java)

        initializeUI()
        checkDeviceAdminStatus()
        handleLockMessage(intent)
    }

    private fun initializeUI() {
        statusTextView = findViewById(R.id.status_text)
        activateAdminButton = findViewById(R.id.activate_admin_btn)
        lockDeviceButton = findViewById(R.id.lock_device_btn)
        checkStatusButton = findViewById(R.id.check_status_btn)

        activateAdminButton.setOnClickListener { requestDeviceAdminPermission() }
        lockDeviceButton.setOnClickListener { lockDeviceAction() }
        checkStatusButton.setOnClickListener { checkRemoteStatus() }
    }

    private fun requestDeviceAdminPermission() {
        if (!devicePolicyManager.isAdminActive(adminComponent)) {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
                putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "Activation requise pour les fonctionnalités de sécurité")
            }
            startActivityForResult(intent, REQUEST_ACTIVATE_DEVICE_ADMIN)
        } else {
            Toast.makeText(this, "Device Admin already active", Toast.LENGTH_SHORT).show()
            updateStatus()
        }
    }

    private fun lockDeviceAction() {
        if (isDeviceAdminActive()) {
            val message = "⚠️ DEVICE LOCKED\nContactez l'administrateur système.\nID: ${getDeviceId()}"
            
            val intent = Intent(this, ShieldCheckService::class.java).apply {
                action = ShieldCheckService.ACTION_LOCK_DEVICE
                putExtra(ShieldCheckService.EXTRA_MESSAGE, message)
            }
            startService(intent)
            Toast.makeText(this, "Device locking in progress...", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Please activate Device Admin first", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkRemoteStatus() {
        lifecycleScope.launch {
            try {
                val intent = Intent(this@MainActivity, ShieldCheckService::class.java).apply {
                    action = ShieldCheckService.ACTION_CHECK_STATUS
                }
                startService(intent)
                Toast.makeText(this@MainActivity, "Checking remote status...", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TAG, "Error checking status: ${e.message}", e)
                Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleLockMessage(intent: Intent?) {
        val lockMessage = intent?.getStringExtra("lock_message")
        if (!lockMessage.isNullOrEmpty()) {
            statusTextView.text = lockMessage
            statusTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        }
    }

    private fun checkDeviceAdminStatus() {
        updateStatus()
    }

    private fun updateStatus() {
        val isActive = isDeviceAdminActive()
        val status = if (isActive) "✅ Device Admin Activated" else "❌ Device Admin Not Activated"
        statusTextView.text = status
        statusTextView.setTextColor(
            if (isActive) ContextCompat.getColor(this, android.R.color.holo_green_dark)
            else ContextCompat.getColor(this, android.R.color.holo_red_dark)
        )
        activateAdminButton.isEnabled = !isActive
        lockDeviceButton.isEnabled = isActive
    }

    private fun isDeviceAdminActive(): Boolean {
        return devicePolicyManager.isAdminActive(adminComponent)
    }

    private fun getDeviceId(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Build.ID
        } else {
            @Suppress("DEPRECATION")
            Build.SERIAL
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ACTIVATE_DEVICE_ADMIN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Device Admin activated!", Toast.LENGTH_SHORT).show()
                updateStatus()
                LoggerUtil.log("Device Admin successfully activated")
            } else {
                Toast.makeText(this, "Device Admin activation failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleLockMessage(intent)
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_ACTIVATE_DEVICE_ADMIN = 1001
    }
}
