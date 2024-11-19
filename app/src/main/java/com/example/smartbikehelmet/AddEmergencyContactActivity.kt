package com.example.smartbikehelmet

import Contact
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener

class AddEmergencyContactActivity : AppCompatActivity() {
    private lateinit var contactAdapter: SMSadapter
    private var contactList = mutableListOf<Contact>()
    private var contactKeysList = mutableListOf<String>()
    private lateinit var database: DatabaseReference

    private val SMS_PERMISSION_CODE = 101
    private val LOCATION_PERMISSION_CODE = 102

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_emergency_contact)

        database = FirebaseDatabase.getInstance().reference.child("EmergencyContacts")

        val etContactName = findViewById<EditText>(R.id.etContactName)
        val etContactNumber = findViewById<EditText>(R.id.etContactNumber)
        val btnAddContact = findViewById<Button>(R.id.btnAddContact)
        val btnSendSOS = findViewById<Button>(R.id.btnSendSOS)
        val rvContacts = findViewById<RecyclerView>(R.id.rvContacts)

        rvContacts.layoutManager = LinearLayoutManager(this)
        contactAdapter = SMSadapter(contactList, contactKeysList) { key, position ->
            onContactDeleted(key, position) // Change position type to Int
        }
        rvContacts.adapter = contactAdapter

        loadContactsFromFirebase()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        btnAddContact.setOnClickListener {
            val name = etContactName.text.toString()
            val phoneNumber = etContactNumber.text.toString()

            if (name.isNotEmpty() && phoneNumber.isNotEmpty()) {
                val newContact = Contact(name, phoneNumber, "")
                saveContactToFirebase(newContact)
                etContactName.text.clear()
                etContactNumber.text.clear()
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        btnSendSOS.setOnClickListener {
            if (checkSmsPermission()) {
                if (checkLocationPermission()) {
                    sendSOSToAllContacts()
                } else {
                    requestLocationPermission()
                }
            } else {
                requestSmsPermission()
            }
        }
    }

    private fun loadContactsFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                contactList.clear()
                contactKeysList.clear()
                for (contactSnapshot in snapshot.children) {
                    val contact = contactSnapshot.getValue(Contact::class.java)
                    if (contact != null) {
                        contactList.add(contact)
                        contactKeysList.add(contactSnapshot.key.toString())
                    }
                }
                contactAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AddEmergencyContactActivity, "Failed to load contacts", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveContactToFirebase(contact: Contact) {
        val key = database.push().key
        if (key != null) {
            database.child(key).setValue(contact)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Contact saved to Firebase", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to save contact", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    fun sendSOSToAllContactsAndAmbulance() {
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    val sosMessage = "SOS! I need help. Location: https://maps.google.com/?q=${location.latitude},${location.longitude}"
                    val smsManager = SmsManager.getDefault()

                    // Send SOS to emergency contacts
                    for (contact in contactList) {
                        smsManager.sendTextMessage(contact.phone, null, sosMessage, null, null)
                    }

                    // Send SOS to ambulance contact
                    val ambulanceNumber = "9212548716"
                    smsManager.sendTextMessage(ambulanceNumber, null, sosMessage, null, null)

                    Toast.makeText(this, "SOS sent to all contacts and ambulance", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show()
        }
    }
    private fun sendSOSToAllContacts() {
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation.addOnSuccessListener(this, OnSuccessListener<Location> { location ->
                if (location != null) {
                    val sosMessage = "SOS! I need help. Location: https://maps.google.com/?q=${location.latitude},${location.longitude}"
                    val smsManager = SmsManager.getDefault()

                    for (contact in contactList) {
                        smsManager.sendTextMessage(contact.phone, null, sosMessage, null, null)
                    }

                    Toast.makeText(this, "SOS sent to all contacts", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onContactDeleted(key: String, position: Int?) {
        // Remove from Firebase using the key
        if (position != null) {
            database.child(key).removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        contactAdapter.removeContactAt(position) // Call the adapter's remove method
                        Toast.makeText(this, "Contact deleted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to delete contact", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun checkSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestSmsPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), SMS_PERMISSION_CODE)
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            SMS_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "SMS Permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "SMS Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            LOCATION_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Location Permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Location Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}