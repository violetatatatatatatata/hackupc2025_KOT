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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.widget.Toolbar

class FlightListActivity : AppCompatActivity() {

    private val ADD_FLIGHT_REQUEST_CODE = 102
    private val allFlights = mutableListOf<Flight>()
    private lateinit var adapter: FlightAdapter

    override fun attachBaseContext(newBase: Context) {
        val lang = newBase.getSharedPreferences("settings", MODE_PRIVATE).getString("lang", "en") ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flight_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        allFlights.addAll(
            listOf(
                Flight("IB123", "Barcelona", "A1", "10:45", R.string.status_on_time),
                Flight("VY456", "Madrid", "B2", "11:30", R.string.status_delayed),
                Flight("UX789", "Valencia", "C3", "12:15", R.string.status_boarding)
            )
        )

        val recyclerView = findViewById<RecyclerView>(R.id.flightRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FlightAdapter(allFlights)
        recyclerView.adapter = adapter

        val searchView = findViewById<SearchView>(R.id.flightSearchView)
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

        // FAB: Añadir vuelo
        findViewById<FloatingActionButton>(R.id.fabAddFlight).setOnClickListener {
            val intent = Intent(this, AddFlightActivity::class.java)
            startActivityForResult(intent, ADD_FLIGHT_REQUEST_CODE)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_FLIGHT_REQUEST_CODE && resultCode == RESULT_OK) {
            val flight = data?.getSerializableExtra("flight") as? Flight
            flight?.let {
                allFlights.add(it)
                adapter.filter("") // recarga
            }
        }
    }
}
