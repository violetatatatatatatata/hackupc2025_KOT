package cat.hackupc.signalchain.model

data class Flight(
    val flightNumber: String,
    val destination: String,
    val gate: String,
    val boardingTime: String,
    val statusResId: Int
) : java.io.Serializable

