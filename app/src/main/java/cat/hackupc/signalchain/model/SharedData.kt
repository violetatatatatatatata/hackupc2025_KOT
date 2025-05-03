package cat.hackupc.signalchain.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class SharedData(
    val flights: List<Flight>,
    val people: List<Person>,
    val alerts: List<Alert>
) {
    fun toJson(): String = Gson().toJson(this)

    companion object {
        fun fromJson(json: String): SharedData {
            val type = object : TypeToken<SharedData>() {}.type
            return Gson().fromJson(json, type)
        }
    }
}
