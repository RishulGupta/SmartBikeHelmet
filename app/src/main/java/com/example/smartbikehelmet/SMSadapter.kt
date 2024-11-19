package com.example.smartbikehelmet

import Contact
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SMSadapter(
    private val contactList: MutableList<Contact>,
    private val contactKeysList: MutableList<String>,
    private val deleteCallback: (String, Int?) -> Unit // Change from Any? to Int?
) : RecyclerView.Adapter<SMSadapter.ContactViewHolder>() {

    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contactName: TextView = view.findViewById(R.id.tvContactName)
        val contactNumber: TextView = view.findViewById(R.id.tvContactNumber)
        val deleteButton: Button = view.findViewById(R.id.btnDeleteContact)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sms_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        if (position >= contactList.size || position >= contactKeysList.size) {
            return
        }

        val contact = contactList[position]
        holder.contactName.text = contact.name
        holder.contactNumber.text = contact.phone

        holder.deleteButton.setOnClickListener {
            val contactKey = contactKeysList[position]
            deleteCallback(contactKey, position) // Pass position to deleteCallback
        }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    fun removeContactAt(position: Int?) {
        if (position != null && position < contactList.size && position < contactKeysList.size) {
            contactList.removeAt(position)
            contactKeysList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, contactList.size)
        }
    }
}