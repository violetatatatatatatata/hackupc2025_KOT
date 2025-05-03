package cat.hackupc.signalchain

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import cat.hackupc.signalchain.model.Flight

class AddFlightActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val lang = newBase.getSharedPreferences("settings", MODE_PRIVATE)
            .getString("lang", "en") ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_flight)

        val editFlightNumber = findViewById<EditText>(R.id.editFlightNumber)
        val editDestination = findViewById<EditText>(R.id.editDestination)
        val editGate = findViewById<EditText>(R.id.editGate)
        val editBoardingTime = findViewById<EditText>(R.id.editBoardingTime)
        val spinnerStatus = findViewById<Spinner>(R.id.spinnerStatus)

        val btnSave = findViewById<Button>(R.id.btnSaveFlight)
        val btnCancel = findViewById<Button>(R.id.btnCancelFlight)

        // Lista de estados del vuelo traducidos
        val statuses = listOf(
            getString(R.string.status_on_time),
            getString(R.string.status_delayed),
            getString(R.string.status_boarding),
            getString(R.string.status_cancelled)
        )

        // Adaptador personalizado con estilos oscuros
        val adapter = ArrayAdapter<String>(
            this,
            R.layout.spinner_item_dark,
            statuses
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_dark)
        spinnerStatus.adapter = adapter

        btnCancel.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            val number = editFlightNumber.text.toString().trim()
            val destination = editDestination.text.toString().trim()
            val gate = editGate.text.toString().trim()
            val boardingTime = editBoardingTime.text.toString().trim()
            val statusString = spinnerStatus.selectedItem.toString()

            if (number.isEmpty() || destination.isEmpty() || gate.isEmpty() || boardingTime.isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_fill_flight), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Obtener el resource ID del estado seleccionado
            val statusResId = when (statusString) {
                getString(R.string.status_on_time) -> R.string.status_on_time
                getString(R.string.status_delayed) -> R.string.status_delayed
                getString(R.string.status_boarding) -> R.string.status_boarding
                getString(R.string.status_cancelled) -> R.string.status_cancelled
                else -> R.string.status_on_time
            }

            val newFlight = Flight(number, destination, gate, boardingTime, statusResId)

            val resultIntent = Intent().apply {
                putExtra("flight", newFlight)
            }

            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}
