package com.onelogica.esathi

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationSettingsClient: SettingsClient

    // ✅ Notification permission
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                Toast.makeText(this, "Notification permission required!", Toast.LENGTH_SHORT).show()
            } else {
                startMyService()
            }
        }

    // ✅ Location permission
    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                checkLocationEnabled() // After permission granted → check GPS
            } else {
                Toast.makeText(this, "Location permission required!", Toast.LENGTH_SHORT).show()
            }
        }

    // ✅ Launcher for GPS enable dialog
    private val resolutionForResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // ✅ GPS enabled
                startMyService()
            } else {
                Toast.makeText(this, "GPS is required for this app", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationSettingsClient = LocationServices.getSettingsClient(this)

        // ✅ Ask Notification Permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            startMyService()
        }

        // ✅ Ask to ignore battery optimizations
        requestBatteryOptimizationIgnore()

        // ✅ Ask Location Permission
        requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        // ✅ Buttons
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

    // 🔋 Ignore battery optimization
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

    // 🚀 Start foreground service
    private fun startMyService() {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    // 📍 Check if Location (GPS) is ON
    private fun checkLocationEnabled() {
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 1000L
        ).build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val task = locationSettingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            // ✅ GPS already enabled → start service
            startMyService()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSender = exception.resolution.intentSender
                    resolutionForResult.launch(
                        IntentSenderRequest.Builder(intentSender).build()
                    )
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }

    // 🔄 Re-check location every time activity resumes
    override fun onResume() {
        super.onResume()
        checkLocationEnabled()
    }
}
