import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartbikehelmet.R

class ContactsAdapter(
    var contactsList: List<Contact>,
    private val onCallButtonClick: (String) -> Unit // Pass phone number
) : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item_indiv, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactsList[position]
        holder.nameTextView.text = contact.name
        holder.phoneTextView.text = contact.phone
        holder.signTextView.text = contact.initials

        // Call button click listener
        holder.callButton.setOnClickListener {
            onCallButtonClick(contact.phone) // Pass the phone number to the callback
        }
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }

    fun updateContacts(newContacts: List<Contact>) {
        contactsList = newContacts
        notifyDataSetChanged() // Refresh RecyclerView
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.name)
        val phoneTextView: TextView = itemView.findViewById(R.id.phone)
        val signTextView: TextView = itemView.findViewById(R.id.sign)
        val callButton: View = itemView.findViewById(R.id.button) // Your call button
    }
}

data class Contact(
    val name: String,
    val phone: String,
    val initials: String
){

    constructor() : this("", "", "")
}
