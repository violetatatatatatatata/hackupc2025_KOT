package cat.hackupc.signalchain

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cat.hackupc.signalchain.model.Person
import androidx.appcompat.widget.Toolbar

class PersonListActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val lang = newBase.getSharedPreferences("settings", MODE_PRIVATE).getString("lang", "en") ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }



        val people = listOf(
            Person("Alice", "Smith", "Near Gate A2", "3 minutes ago"),
            Person("Carlos", "Ramírez", "Next to McDonald's", "5 minutes ago"),
            Person("Emma", "Johnson", "Restroom by Gate B", "just now")
        )

        val recyclerView = findViewById<RecyclerView>(R.id.personRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = PersonAdapter(people)
        recyclerView.adapter = adapter

        val searchView = findViewById<SearchView>(R.id.personSearchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText ?: "")
                return true
            }
        })

        // Cambiar color del texto del buscador
        searchView.post {
            val searchText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            searchText?.setTextColor(Color.WHITE)
            searchText?.setHintTextColor(Color.GRAY)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // Cierra la actividad actual y vuelve al MainActivity
        return true
    }

}
