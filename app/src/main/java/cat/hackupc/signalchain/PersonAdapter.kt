package cat.hackupc.signalchain

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cat.hackupc.signalchain.model.Person

class PersonAdapter(private val allPeople: List<Person>) : RecyclerView.Adapter<PersonAdapter.PersonViewHolder>() {

    private var filteredPeople: List<Person> = allPeople.toList()

    fun filter(query: String) {
        filteredPeople = if (query.isEmpty()) {
            allPeople
        } else {
            allPeople.filter {
                val fullName = "${it.firstName} ${it.lastName}"
                fullName.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    class PersonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvPersonName)
        val location: TextView = view.findViewById(R.id.tvPersonLocation)
        val lastUpdate: TextView = view.findViewById(R.id.tvPersonLastUpdate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_person, parent, false)
        return PersonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val person = filteredPeople[position]
        holder.name.text = "${person.firstName} ${person.lastName}"
        holder.location.text = person.location
        holder.lastUpdate.text = person.lastUpdate
    }

    override fun getItemCount(): Int = filteredPeople.size
}
