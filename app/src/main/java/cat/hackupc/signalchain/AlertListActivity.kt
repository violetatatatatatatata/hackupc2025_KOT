package cat.hackupc.signalchain

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cat.hackupc.signalchain.model.Alert
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.widget.Toolbar

class AlertListActivity : AppCompatActivity() {

    private val ADD_ALERT_REQUEST_CODE = 103
    private val alerts = mutableListOf<Alert>()
    private lateinit var adapter: AlertAdapter

    override fun attachBaseContext(newBase: Context) {
        val lang = newBase.getSharedPreferences("settings", MODE_PRIVATE)
            .getString("lang", "en") ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        // Datos de prueba
        alerts.addAll(
            listOf(
                Alert("Power Outage", "Main terminal lights are off.", "10:30"),
                Alert("Flight Delay", "Flight UX789 delayed due to weather.", "11:15"),
                Alert("Lost Item", "Backpack found near security gate.", "11:20")
            )
        )

        val recyclerView = findViewById<RecyclerView>(R.id.alertRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AlertAdapter(alerts)
        recyclerView.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabAddAlert).setOnClickListener {
            val intent = Intent(this, AddAlertActivity::class.java)
            startActivityForResult(intent, ADD_ALERT_REQUEST_CODE)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_ALERT_REQUEST_CODE && resultCode == RESULT_OK) {
            val alert = data?.getSerializableExtra("alert") as? Alert
            alert?.let {
                alerts.add(0, it)
                adapter.notifyItemInserted(0)
            }
        }
    }
}
