package cat.hackupc.signalchain

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cat.hackupc.signalchain.model.Alert

class AlertAdapter(private val alerts: List<Alert>) : RecyclerView.Adapter<AlertAdapter.AlertViewHolder>() {

    class AlertViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvAlertTitle)
        val message: TextView = view.findViewById(R.id.tvAlertMessage)
        val timestamp: TextView = view.findViewById(R.id.tvAlertTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alert, parent, false)
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert = alerts[position]
        holder.title.text = alert.title
        holder.message.text = alert.message
        holder.timestamp.text = alert.timestamp
    }

    override fun getItemCount(): Int = alerts.size
}
