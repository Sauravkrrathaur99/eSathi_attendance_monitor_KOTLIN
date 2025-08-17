//package com.onelogica.esathi
//
//import android.Manifest
//import android.content.Intent
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.os.PowerManager
//import android.provider.Settings
//import android.widget.Button
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//
//class MainActivity : AppCompatActivity() {
//
//    // ✅ Register for notification permission (Android 13+)
//    private val requestNotificationPermission =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
//            if (!granted) {
//                Toast.makeText(this, "Notification permission required!", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        // ✅ Ask Notification Permission (Android 13+)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
//        }
//
//        // ✅ Ask user to ignore battery optimizations
//        requestBatteryOptimizationIgnore()
//
//        // ✅ Buttons
//        val startBtn = findViewById<Button>(R.id.startServiceBtn)
//        val stopBtn = findViewById<Button>(R.id.stopServiceBtn)
//
//        // Start Foreground Service safely
//        startBtn.setOnClickListener {
//            val serviceIntent = Intent(this, ForegroundService::class.java)
//            ContextCompat.startForegroundService(this, serviceIntent) // ✅ FIX
//            Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show()
//        }
//
//        // Stop Foreground Service
//        stopBtn.setOnClickListener {
//            val serviceIntent = Intent(this, ForegroundService::class.java)
//            stopService(serviceIntent)
//            Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun requestBatteryOptimizationIgnore() {
//        val pm = getSystemService(POWER_SERVICE) as PowerManager
//        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
//            try {
//                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
//                intent.data = Uri.parse("package:$packageName")
//                startActivity(intent)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//}


//package com.onelogica.esathi
//
//import android.Manifest
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.os.PowerManager
//import android.provider.Settings
//import android.widget.Button
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//
//class MainActivity : AppCompatActivity() {
//
//    // ✅ Register for notification permission (Android 13+)
//    private val requestNotificationPermission =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
//            if (!granted) {
//                Toast.makeText(this, "Notification permission required!", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        // ✅ Ask Notification Permission (Android 13+)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
//        }
//
//        // ✅ Ask user to ignore battery optimizations
//        requestBatteryOptimizationIgnore()
//
//        // ✅ Buttons
//        val startBtn = findViewById<Button>(R.id.startServiceBtn)
//        val stopBtn = findViewById<Button>(R.id.stopServiceBtn)
//
//        // Start Foreground Service safely
//        startBtn.setOnClickListener {
//            val serviceIntent = Intent(this, ForegroundService::class.java)
//            ContextCompat.startForegroundService(this, serviceIntent) // ✅ FIX
//            Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show()
//        }
//
//        // Stop Foreground Service
//        stopBtn.setOnClickListener {
//            val serviceIntent = Intent(this, ForegroundService::class.java)
//            stopService(serviceIntent)
//            Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    // 🔋 Only the snippet you asked for
//    private fun requestBatteryOptimizationIgnore() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
//            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
//                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
//                intent.data = Uri.parse("package:$packageName")
//                startActivity(intent)
//            }
//        }
//    }
//}


package com.onelogica.esathi

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    // ✅ Register for notification permission (Android 13+)
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                Toast.makeText(this, "Notification permission required!", Toast.LENGTH_SHORT).show()
            } else {
                // After permission is granted → start service automatically
                startMyService()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ✅ Ask Notification Permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            startMyService() // For Android < 13 → directly start service
        }

        // ✅ Ask user to ignore battery optimizations
        requestBatteryOptimizationIgnore()

        // ✅ Buttons (optional for testing)
        val startBtn = findViewById<Button>(R.id.startServiceBtn)
        val stopBtn = findViewById<Button>(R.id.stopServiceBtn)

        startBtn.setOnClickListener {
            startMyService()
            Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show()
        }

        stopBtn.setOnClickListener {
            val serviceIntent = Intent(this, ForegroundService::class.java)
            stopService(serviceIntent)
            Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show()
        }
    }

    // 🔋 Ask to disable battery optimization
    private fun requestBatteryOptimizationIgnore() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                try {
                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // 🚀 Start foreground service automatically
    private fun startMyService() {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }
}
