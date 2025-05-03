package cat.hackupc.signalchain

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.SearchView
import android.widget.TextView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cat.hackupc.signalchain.model.Flight
import androidx.appcompat.widget.Toolbar


class FlightListActivity : AppCompatActivity() {

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


        val recyclerView = findViewById<RecyclerView>(R.id.flightRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val flights = listOf(
            Flight("IB123", "Barcelona", "A1", "10:45", R.string.status_on_time),
            Flight("VY456", "Madrid", "B2", "11:30", R.string.status_delayed),
            Flight("UX789", "Valencia", "C3", "12:15", R.string.status_boarding)
        )

        val adapter = FlightAdapter(flights)
        recyclerView.adapter = adapter

        val searchView = findViewById<SearchView>(R.id.flightSearchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText ?: "")
                return true
            }
        })

        // 👇 Aplicamos color al texto y hint del SearchView tras cargar la vista
        searchView.post {
            val searchEditText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            searchEditText?.apply {
                setTextColor(Color.WHITE)
                setHintTextColor(Color.WHITE)
                setBackgroundColor(Color.TRANSPARENT)
                highlightColor = Color.WHITE
                setCursorVisible(true)
            }
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // Cierra la actividad actual y vuelve al MainActivity
        return true
    }

}
