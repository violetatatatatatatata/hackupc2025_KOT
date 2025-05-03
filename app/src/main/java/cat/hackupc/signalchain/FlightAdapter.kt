package cat.hackupc.signalchain

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cat.hackupc.signalchain.model.Flight

class FlightAdapter(private val allFlights: List<Flight>) : RecyclerView.Adapter<FlightAdapter.FlightViewHolder>() {

    private var filteredFlights: List<Flight> = allFlights.toList()

    fun filter(query: String) {
        filteredFlights = if (query.isEmpty()) {
            allFlights
        } else {
            allFlights.filter {
                it.flightNumber.contains(query, ignoreCase = true) ||
                        it.destination.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    class FlightViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val flightNumber: TextView = view.findViewById(R.id.tvFlightNumber)
        val destination: TextView = view.findViewById(R.id.tvDestination)
        val gate: TextView = view.findViewById(R.id.tvGate)
        val time: TextView = view.findViewById(R.id.tvTime)
        val status: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_flight, parent, false)
        return FlightViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
        val flight = filteredFlights[position]
        val context = holder.itemView.context
        holder.flightNumber.text = flight.flightNumber
        holder.destination.text = flight.destination
        holder.gate.text = "${context.getString(R.string.label_gate)}: ${flight.gate}"
        holder.time.text = "${context.getString(R.string.label_boarding)}: ${flight.boardingTime}"
        holder.status.text = "${context.getString(R.string.label_status)}: ${context.getString(flight.statusResId)}"
    }

    override fun getItemCount(): Int = filteredFlights.size
}
