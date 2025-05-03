package cat.hackupc.signalchain.model

data class Person(
    val firstName: String,
    val lastName: String,
    val location: String,
    val lastUpdate: String // Ej: "Hace 5 minutos"
)
