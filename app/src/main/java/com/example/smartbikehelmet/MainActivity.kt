package com.example.smartbikehelmet

import Contact
import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.smartbikehelmet.databinding.ActivityMainBinding
import com.example.smartbikehelmet.ui.home.HomeFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import java.lang.StrictMath.sqrt
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {
    private var womenHelplineNumber="9212548716"
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private val ambulanceNumber = "9606553907"
    private var contactList = mutableListOf<Contact>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var database: DatabaseReference
    private lateinit var databaseSensor: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize BottomNavigationView
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Initialize DrawerLayout and ActionBarDrawerToggle
        drawerLayout = binding.drawerLayout
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set up drawer navigation listener
        val navigationView: NavigationView = binding.navViewDrawer
        navigationView.setNavigationItemSelectedListener { menuItem ->
            handleDrawerMenuItemClick(menuItem)
        }

        // Set up notification channel
        createNotificationChannel()

        // Initialize location and database
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        database = FirebaseDatabase.getInstance().reference.child("EmergencyContacts")
        databaseSensor = FirebaseDatabase.getInstance().reference.child("sensor")
        loadContactsFromFirebase()
        monitorCrashData()
    }

    private fun handleDrawerMenuItemClick(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_settings -> {
                startActivity(Intent(this, AddEmergencyContactActivity::class.java))
                Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
                drawerLayout.closeDrawer(GravityCompat.START)

                return true
            }

            R.id.nav_about -> {
                startActivity(Intent(this, profile_activity::class.java))
                Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show()
                drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
            //R.id.nav_locator->{
            //    startActivity(Intent(this, HelmetLocatorActivity::class.java))
           //     Toast.makeText(this, "Locator clicked", Toast.LENGTH_SHORT).show()
           //     drawerLayout.closeDrawer(GravityCompat.START)
            //    return true
           // }
            R.id.nav_home->{
              //  startActivity(Intent(this, HomeFragment::class.java))
                Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show()
                drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }

            else -> return false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Crash Notifications"
            val descriptionText = "Notifications for crash checks"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("CRASH_CHANNEL", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun loadContactsFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                contactList.clear()
                for (contactSnapshot in snapshot.children) {
                    val contact = contactSnapshot.getValue(Contact::class.java)
                    if (contact != null) {
                        contactList.add(contact)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to load contacts", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    // New sendSOS function with permission checks
    fun sendSOS() {
        if (checkPermissions()) {
            sendSOSToAllContactsAndAmbulance()
        } else {
            requestPermissions()
        }
    }
    private fun sendSOSMessage2() {
        if (checkPermissions()) {
            sendSOSToAllContactsAndWomenHelpline()
        } else {
            requestPermissions()
        }
    }


    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION),
            101
        )
    }

    private fun sendSOSToAllContactsAndAmbulance() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val sosMessage =
                    "SOS! I need help. Location: https://maps.google.com/?q=${location.latitude},${location.longitude}"
                val smsManager = SmsManager.getDefault()

                // Send SOS to each emergency contact
                for (contact in contactList) {
                    try {
                        Log.d("SOS", "Sending SOS to contact: ${contact.phone}")
                        smsManager.sendTextMessage(contact.phone, null, sosMessage, null, null)
                    } catch (e: Exception) {
                        Log.e("SOS", "Failed to send SMS to contact ${contact.phone}", e)
                    }
                }

                // Send SOS to ambulance
                try {
                    Log.d("SOS", "Sending SOS to ambulance: $ambulanceNumber")
                    smsManager.sendTextMessage(ambulanceNumber, null, sosMessage, null, null)
                } catch (e: Exception) {
                    Log.e("SOS", "Failed to send SMS to ambulance $ambulanceNumber", e)
                }

                Toast.makeText(this, "SOS sent to all contacts and ambulance", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Log.e("SOS", "Failed to get location", it)
            Toast.makeText(this, "Failed to retrieve location", Toast.LENGTH_SHORT).show()
        }
    }
    private fun sendSOSToAllContactsAndWomenHelpline() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val sosMessage =
                    "SOS! I need help women. Location: https://maps.google.com/?q=${location.latitude},${location.longitude}"
                val smsManager = SmsManager.getDefault()

                // Send SOS to each emergency contact
                for (contact in contactList) {
                    try {
                        Log.d("SOS", "Sending SOS to contact: $contact")
                        smsManager.sendTextMessage(contact.phone, null, sosMessage, null, null)
                    } catch (e: Exception) {
                        Log.e("SOS", "Failed to send SMS to contact $contact", e)
                    }
                }

                // Send SOS to women helpline
                try {
                    Log.d("SOS", "Sending SOS to women helpline: $womenHelplineNumber")
                    smsManager.sendTextMessage(womenHelplineNumber, null, sosMessage, null, null)
                } catch (e: Exception) {
                    Log.e("SOS", "Failed to send SMS to women helpline $womenHelplineNumber", e)
                }

                Toast.makeText(this, "SOS sent to all contacts and women helpline", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Log.e("SOS", "Failed to get location", it)
            Toast.makeText(this, "Failed to retrieve location", Toast.LENGTH_SHORT).show()
        }
    }
    private fun showWomenSosDialog() {
        AlertDialog.Builder(this)
            .setTitle("Women Safety SOS")
            .setMessage("Are you sure you want to call the women safety helpline?")
            .setPositiveButton("Yes") { dialog, _ ->
                sendSOSMessage2()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun sendCheckInNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    102
                )
                return
            }
        }

        val builder = NotificationCompat.Builder(this, "CRASH_CHANNEL")
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle("Check-in Required")
            .setContentText("A small crash was detected. Please check in!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(this)) {
            notify(1, builder.build())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            101 -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    sendSOS()  // Retry sending SOS after permission is granted
                } else {
                    Toast.makeText(this, "SOS permission denied", Toast.LENGTH_SHORT).show()
                }
            }

            102 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendCheckInNotification()
                } else {
                    Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun monitorCrashData() {
        // Reference the path in Firebase where accelerometer data and button state are stored
        val accelerometerRef = databaseSensor.child("accelerometer")
        val buttonStateRef = databaseSensor.child("buttonState")
val buttonStateWomen=databaseSensor.child("buttonState2")
        // Monitor accelerometer data changes
        accelerometerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                handleAccelerometerData(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to read accelerometer data", error.toException())
            }
        })

        // Monitor button state changes
        buttonStateRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                handleButtonState(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to read button state", error.toException())
            }
        })
        buttonStateWomen.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                handleWomenButtonState(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to read button state", error.toException())
            }
        })

    }

    private fun handleAccelerometerData(snapshot: DataSnapshot) {
        val accelX = snapshot.child("x").getValue(Float::class.java) ?: 0f
        val accelY = snapshot.child("y").getValue(Float::class.java) ?: 0f
        val accelZ = snapshot.child("z").getValue(Float::class.java) ?: 0f

        // Calculate magnitude of the accelerometer vector
        val magnitude = sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ)

        // Define crash thresholds
        val minorCrashThreshold = 15.5f
        val majorCrashThreshold = 20.5f

        // Check the magnitude against thresholds
        when {
            magnitude > majorCrashThreshold -> {
                // Trigger major crash response
                Toast.makeText(this@MainActivity, "Major crash detected! Magnitude: $magnitude", Toast.LENGTH_SHORT).show()
                //sendSOS() // Uncomment to send SOS if needed
            }
            magnitude > minorCrashThreshold -> {
                // Trigger minor crash response
                Toast.makeText(this@MainActivity, "Minor crash detected. Magnitude: $magnitude", Toast.LENGTH_SHORT).show()
                sendCheckInNotification() // Function for sending check-in notifications
            }
            else -> {
                // No significant crash detected, do nothing
            }
        }
    }

    private fun handleButtonState(snapshot: DataSnapshot) {
        val buttonState = snapshot.getValue(Float::class.java) ?: 0f

        if (buttonState == 1.0f) {
            // Send SOS if button state is 1
            Toast.makeText(this@MainActivity, "Button pressed, sending SOS!", Toast.LENGTH_SHORT).show()
            sendSOS() // Uncomment to send SOS if needed
        }
    }
    private fun handleWomenButtonState(snapshot: DataSnapshot) {
        val buttonState2 = snapshot.getValue(Float::class.java) ?: 0.0f

        if (buttonState2 == 1.0f) {
            // Send SOS if button state is 1
            Toast.makeText(this@MainActivity, "Women safety Button pressed, sending SOS!", Toast.LENGTH_SHORT).show()
            sendSOSMessage2() // Uncomment to send SOS if needed
        }
        else
        {
            //Toast.makeText(this@MainActivity, "Women safety Button not pressed", Toast.LENGTH_SHORT).show()
        }
    }

    // Other methods (e.g., sendSOS, sendCheckInNotification, etc.) remain the same
}
