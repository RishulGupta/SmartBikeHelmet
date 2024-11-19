package com.example.smartbikehelmet.ui.home

import Contact
import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.smartbikehelmet.MainActivity
import com.example.smartbikehelmet.R
import com.example.smartbikehelmet.databinding.FragmentHomeBinding
import com.example.smartbikehelmet.profile_activity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var contactList = mutableListOf<Contact>()
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
private var womenHelplineNumber="9212548716"
    private var progress = 0
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval: Long = 20 // Faster update interval for quicker progress
    private var isButtonPressed = false
    private val progressRunnable = object : Runnable {
        override fun run() {
            if (progress < 100) {
                progress += 2 // Increase progress faster
                binding.progressBar.progress = progress
                if(progress==0)
                {
                    binding.holdButton.text="Press and hold"
                }
                else
                {
                    binding.holdButton.text="Sending SOS.."
                }
                handler.postDelayed(this, updateInterval)
            } else {
                Toast.makeText(requireContext(), "Progress completed!", Toast.LENGTH_SHORT).show()
                sendSOSMessage()
            }
        }
    }

    private fun sendSOSMessage2() {
        if (checkPermissions()) {
            sendSOSToAllContactsAndWomenHelpline()
        } else {
            requestPermissions()
        }
    }
    private fun sendSOSMessage() {
        (activity as? MainActivity)?.sendSOS()
        Toast.makeText(requireContext(), "Progress completed! SOS Sent.", Toast.LENGTH_SHORT).show()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        drawerLayout = binding.drawerLayout
        toggle = ActionBarDrawerToggle(
            requireActivity(),
            drawerLayout,
            R.string.open,
            R.string.close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        toggle.setToolbarNavigationClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                requireActivity().onBackPressed()
            }
        }

        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(requireContext(), "Home clicked", Toast.LENGTH_SHORT).show()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_settings -> {
                    Toast.makeText(requireContext(), "Settings clicked", Toast.LENGTH_SHORT).show()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_about -> {
                    startActivity(Intent(requireActivity(), profile_activity::class.java))
                    Toast.makeText(requireContext(), "About clicked", Toast.LENGTH_SHORT).show()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }


binding.womenbutton.setOnClickListener {
    showWomenSosDialog()
}
        // Dummy notification button


        binding.holdButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isButtonPressed = true
                    progress = 0
                    binding.progressBar.progress = progress
                    binding.holdButton.text = "Sending SOS.."
                    handler.post(progressRunnable)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isButtonPressed = false
                    binding.holdButton.text = "Press and hold"
                    handler.removeCallbacks(progressRunnable)
                    progress = 0
                    binding.progressBar.progress = progress
                    Toast.makeText(requireContext(), "Hold button longer to complete", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
    }
    private fun showWomenSosDialog() {
        AlertDialog.Builder(requireContext())
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
private fun checkPermissions(): Boolean {
    return ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.SEND_SMS
    ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
}

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION),
            101
        )
    }

    private fun sendSOSToAllContactsAndWomenHelpline() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
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

                Toast.makeText(requireContext(), "SOS sent to all contacts and women helpline", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Unable to get location", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Log.e("SOS", "Failed to get location", it)
            Toast.makeText(requireContext(), "Failed to retrieve location", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        handler.removeCallbacks(progressRunnable)
    }
}
