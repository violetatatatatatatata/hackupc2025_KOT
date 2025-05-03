package cat.hackupc.signalchain.model

data class Alert(
    val title: String,
    val message: String,
    val timestamp: String // Ejemplo: "11:05"
) : java.io.Serializable
