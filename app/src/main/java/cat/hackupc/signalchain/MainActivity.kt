package cat.hackupc.signalchain

import android.content.Context
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent

class MainActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val lang = newBase.getSharedPreferences("settings", MODE_PRIVATE).getString("lang", "en") ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnFlights = findViewById<Button>(R.id.btnFlights)
        val btnPeople = findViewById<Button>(R.id.btnPeople)
        val btnAlerts = findViewById<Button>(R.id.btnAlerts)

        btnFlights.text = getString(R.string.btn_flights)
        btnPeople.text = getString(R.string.btn_people)
        btnAlerts.text = getString(R.string.btn_alerts)

        btnFlights.setOnClickListener {
            val intent = Intent(this, FlightListActivity::class.java)
            startActivity(intent)
        }

        btnPeople.setOnClickListener {
            val intent = Intent(this, PersonListActivity::class.java)
            startActivity(intent)
        }

        btnAlerts.setOnClickListener {
            val intent = Intent(this, AlertListActivity::class.java)
            startActivity(intent)
        }

    }
}
