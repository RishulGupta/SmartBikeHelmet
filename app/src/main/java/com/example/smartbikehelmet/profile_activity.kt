package com.example.smartbikehelmet

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smartbikehelmet.databinding.ActivityProfileBinding

class profile_activity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the binding and set the content view using binding.root
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the logout button click listener
        binding.logout.setOnClickListener {
            startActivity(Intent(this@profile_activity, LoginAndSignUpActivity::class.java))
            finish() // Optionally, close the ProfileActivity after logout
        }
    }
}