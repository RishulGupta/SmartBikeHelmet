package com.example.smartbikehelmet.ui.notifications

import Contact
import ContactsAdapter
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartbikehelmet.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private val contactsList = mutableListOf<Contact>()
    private val CONTACTS_PERMISSION_CODE = 100
    private val CALL_PHONE_PERMISSION_CODE = 101
    private lateinit var adapter: ContactsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Check and request contact permission
        if (checkContactPermission()) {
            loadContacts() // Load contacts if permission is granted
        }

        setupSearch()

        return root
    }

    private fun checkContactPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_CONTACTS), CONTACTS_PERMISSION_CODE)
            false
        }
    }

    private fun loadContacts() {
        val resolver = requireActivity().contentResolver
        val cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)

        // Create a Set to store unique contacts based on both name and number
        val uniqueContacts = mutableSetOf<Pair<String, String>>()

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                val number = it.getString(numberIndex).replace("\\s".toRegex(), "") // Remove spaces for consistency
                val initials = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString("")

                // Create a Pair of (name, number) to ensure uniqueness
                if (uniqueContacts.add(Pair(name, number))) {
                    contactsList.add(Contact(name, number, initials))
                }
            }
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.contactsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ContactsAdapter(contactsList) { phoneNumber ->
            // Handle call request
            makePhoneCall(phoneNumber)
        }
        recyclerView.adapter = adapter
    }

    private fun setupSearch() {
        binding.searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s?.toString()?.trim() ?: ""

                if (searchText.isEmpty()) {
                    adapter.updateContacts(contactsList) // Reset to original list if search is empty
                } else {
                    val filteredList = contactsList.filter {
                        it.name.contains(searchText, ignoreCase = true) ||
                                it.phone.contains(searchText, ignoreCase = true)
                    }
                    adapter.updateContacts(filteredList)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun makePhoneCall(phoneNumber: String) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), CALL_PHONE_PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CONTACTS_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadContacts()
                } else {
                    Toast.makeText(context, "Contacts permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            CALL_PHONE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Call permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Call permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
