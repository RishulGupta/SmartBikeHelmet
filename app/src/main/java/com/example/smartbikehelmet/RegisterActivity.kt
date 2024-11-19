package com.example.smartbikehelmet

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smartbikehelmet.databinding.ActivityRegisterBinding
import com.example.smartbikehelmet.ui.home.HomeFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class RegisterActivity() : AppCompatActivity() {
    private val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.apply {
            submit.setOnClickListener{
                var email:String=editTextTextEmailAddress.text.toString()
                var password:String=editTextTextPassword.text.toString()
                Firebase.auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener {
                        if(it.isSuccessful)
                        {
                            diaShowDiaLog()

                        }
                        else {
                            Toast.makeText(this@RegisterActivity, it.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
    fun diaShowDiaLog() {

        val dialog = Dialog(this@RegisterActivity)
        dialog.setContentView(R.layout.done_dialog)

        val lp = WindowManager.LayoutParams()
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        if (Build.VERSION_CODES.S <= Build.VERSION.SDK_INT) {
            lp.blurBehindRadius = 16
        }

        lp.flags = WindowManager.LayoutParams.FLAG_BLUR_BEHIND

        dialog.window?.attributes = lp
        dialog.setCancelable(false)
        dialog.show()

        dialog.findViewById<Button>(R.id.done_btn).setOnClickListener {
            val intent = Intent(this@RegisterActivity, AddEmergencyContactActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this@RegisterActivity.startActivity(intent)
            this@RegisterActivity.finish()
        }
    }
}