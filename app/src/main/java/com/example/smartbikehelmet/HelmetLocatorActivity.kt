package com.example.smartbikehelmet

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class HelmetLocatorActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private var buzzerCommand: Float = 0.0f
    private lateinit var locateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helmet_locator)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference.child("sensor")

        // Initialize button
        locateButton = findViewById(R.id.button3)

        // Set initial text on button
        locateButton.text = "Press to Activate Buzzer"

        // Set up button click listener
        locateButton.setOnClickListener {
            toggleBuzzerState()
        }
    }

    private fun toggleBuzzerState() {
        if (buzzerCommand == 0.0f) {
            // Update the Firebase database with the new state under "sensor/buzzerState"
            database.child("buzzerCommand").setValue(1.0f)
            // Change button text
            locateButton.text = "Press to Deactivate Buzzer"
            Toast.makeText(this, "Buzzer state set to 1.0f", Toast.LENGTH_SHORT).show()
            buzzerCommand = 1.0f
        } else {
            // Update the Firebase database with the new state under "sensor/buzzerState"
            database.child("buzzerCommand").setValue(0.0f)
            // Change button text back
            locateButton.text = "Press to Activate Buzzer"
            Toast.makeText(this, "Buzzer state set to 0.0f", Toast.LENGTH_SHORT).show()
            buzzerCommand = 0.0f
        }
    }
}
