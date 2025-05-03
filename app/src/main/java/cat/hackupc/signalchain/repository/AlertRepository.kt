package cat.hackupc.signalchain.repository

import cat.hackupc.signalchain.model.Alert

object AlertRepository {
    val alerts = mutableListOf<Alert>()

    fun merge(newAlerts: List<Alert>) {
        newAlerts.forEach { incoming ->
            val exists = alerts.any {
                it.title == incoming.title && it.timestamp == incoming.timestamp
            }
            if (!exists) {
                alerts.add(incoming)
            }
        }
    }
}
