package cat.hackupc.signalchain

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cat.hackupc.signalchain.model.Alert
import androidx.appcompat.widget.Toolbar as AppToolbar

class AlertListActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val lang = newBase.getSharedPreferences("settings", MODE_PRIVATE).getString("lang", "en") ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert_list)

        // Configurar Toolbar
        val toolbar = findViewById<AppToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        val alerts = listOf(
            Alert("General Delay", "All flights are delayed 15 minutes due to technical issues.", "11:05"),
            Alert("Security Alert", "Do not leave bags unattended.", "10:50"),
            Alert("Gate Change", "Flight IB123 has moved to gate B3.", "10:42")
        )

        val recyclerView = findViewById<RecyclerView>(R.id.alertRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = AlertAdapter(alerts)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
