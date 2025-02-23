package com.example.smartbikehelmet

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartbikehelmet.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            submit.setOnClickListener {
                val email = editTextTextEmailAddress.text.toString().trim()
                val password = editTextTextPassword.text.toString().trim()

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Email and Password cannot be empty",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showDialog()
                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                task.exception?.localizedMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }

    private fun showDialog() {
        val dialog = Dialog(this@RegisterActivity)
        dialog.setContentView(R.layout.done_dialog)

        val lp = WindowManager.LayoutParams()
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            lp.blurBehindRadius = 16
            lp.flags = WindowManager.LayoutParams.FLAG_BLUR_BEHIND
        }

        dialog.window?.attributes = lp
        dialog.setCancelable(false)
        dialog.show()

        dialog.findViewById<Button>(R.id.done_btn).setOnClickListener {
            val intent = Intent(this@RegisterActivity, AddEmergencyContactActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}
