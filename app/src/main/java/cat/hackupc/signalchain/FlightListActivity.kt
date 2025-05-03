package cat.hackupc.signalchain

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cat.hackupc.signalchain.model.Flight
import cat.hackupc.signalchain.repository.FlightRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.widget.Toolbar

class FlightListActivity : AppCompatActivity() {

    private lateinit var adapter: FlightAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView

    override fun attachBaseContext(newBase: Context) {
        val lang = newBase.getSharedPreferences("settings", MODE_PRIVATE)
            .getString("lang", "en") ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flight_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        recyclerView = findViewById(R.id.flightRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FlightAdapter(FlightRepository.flights)
        recyclerView.adapter = adapter

        searchView = findViewById(R.id.flightSearchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText ?: "")
                return true
            }
        })

        searchView.post {
            val searchText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            searchText?.setTextColor(Color.WHITE)
            searchText?.setHintTextColor(Color.GRAY)
        }

        findViewById<FloatingActionButton>(R.id.fabAddFlight).setOnClickListener {
            val intent = Intent(this, AddFlightActivity::class.java)
            startActivityForResult(intent, 102)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 102 && resultCode == RESULT_OK) {
            val flight = data?.getSerializableExtra("flight") as? Flight
            flight?.let {
                FlightRepository.flights.add(it)
                adapter.filter(searchView.query.toString())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        instance = this
        adapter.filter(searchView.query.toString())
    }

    companion object {
        private lateinit var instance: FlightListActivity

        fun refreshData() {
            if (::instance.isInitialized) {
                instance.adapter.filter(instance.searchView.query.toString())
            }
        }
    }
}
