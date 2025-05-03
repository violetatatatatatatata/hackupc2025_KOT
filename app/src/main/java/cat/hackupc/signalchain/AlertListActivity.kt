package cat.hackupc.signalchain

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cat.hackupc.signalchain.model.Alert
import cat.hackupc.signalchain.repository.AlertRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.widget.Toolbar

class AlertListActivity : AppCompatActivity() {

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
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.alertRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AlertAdapter(AlertRepository.alerts)
        recyclerView.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabAddAlert).setOnClickListener {
            val intent = Intent(this, AddAlertActivity::class.java)
            startActivityForResult(intent, 103)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 103 && resultCode == RESULT_OK) {
            val alert = data?.getSerializableExtra("alert") as? Alert
            alert?.let {
                AlertRepository.alerts.add(0, it)
                adapter.notifyItemInserted(0)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        instance = this
        adapter.notifyDataSetChanged()
    }

    companion object {
        private lateinit var instance: AlertListActivity

        fun refreshData() {
            if (::instance.isInitialized) {
                instance.adapter.notifyDataSetChanged()
            }
        }
    }
}
