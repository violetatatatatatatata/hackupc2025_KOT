package cat.hackupc.signalchain.repository

import cat.hackupc.signalchain.model.Flight

object FlightRepository {
    val flights = mutableListOf<Flight>()

    fun merge(newFlights: List<Flight>) {
        newFlights.forEach { incoming ->
            if (flights.none { it.flightNumber == incoming.flightNumber }) {
                flights.add(incoming)
            }
        }
    }
}
