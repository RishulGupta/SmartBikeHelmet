package com.example.smartbikehelmet

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smartbikehelmet.databinding.ActivityLoginBinding
import com.example.smartbikehelmet.ui.home.HomeFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.apply {
            submit.setOnClickListener{
                var email:String=editTextTextEmailAddress.text.toString()
                var password:String=editTextTextPassword.text.toString()
                Firebase.auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        //startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        val intent = Intent(this@LoginActivity,HomeFragment::class.java)

                        this@LoginActivity.startActivity(intent)
                        this@LoginActivity.finish()
                        Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()

                    }
                    else
                    {
                        Toast.makeText(this@LoginActivity, it.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}