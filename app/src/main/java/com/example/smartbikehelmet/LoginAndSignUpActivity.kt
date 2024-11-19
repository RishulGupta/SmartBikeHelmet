package com.example.smartbikehelmet

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smartbikehelmet.databinding.ActivityLoginAndSignUpBinding

class LoginAndSignUpActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityLoginAndSignUpBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            LoginButton.setOnClickListener{
                startActivity(Intent(this@LoginAndSignUpActivity, LoginActivity::class.java))
            }
            RegisterButton.setOnClickListener{
                startActivity(Intent(this@LoginAndSignUpActivity, RegisterActivity::class.java))
            }

        }
    }
}