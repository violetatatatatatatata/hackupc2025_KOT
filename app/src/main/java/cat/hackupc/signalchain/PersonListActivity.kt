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
import cat.hackupc.signalchain.model.Person
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.widget.Toolbar

class PersonListActivity : AppCompatActivity() {

    private val ADD_PERSON_REQUEST_CODE = 101
    private val allPeople = mutableListOf<Person>()
    private lateinit var adapter: PersonAdapter

    override fun attachBaseContext(newBase: Context) {
        val lang = newBase.getSharedPreferences("settings", MODE_PRIVATE)
            .getString("lang", "en") ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_list)

        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        // Datos dummy iniciales
        allPeople.addAll(
            listOf(
                Person("Alice", "Smith", "Near Gate A2", "11:05"),
                Person("Carlos", "Ramírez", "Next to McDonald's", "11:00"),
                Person("Emma", "Johnson", "Restroom by Gate B", "10:45")
            )
        )

        val recyclerView = findViewById<RecyclerView>(R.id.personRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PersonAdapter(allPeople)
        recyclerView.adapter = adapter

        // Buscador
        val searchView = findViewById<SearchView>(R.id.personSearchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText ?: "")
                return true
            }
        })

        // Colores del SearchView
        searchView.post {
            val searchText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            searchText?.setTextColor(Color.WHITE)
            searchText?.setHintTextColor(Color.GRAY)
        }

        // FAB - Añadir persona
        findViewById<FloatingActionButton>(R.id.fabAddPerson).setOnClickListener {
            val intent = Intent(this, AddPersonActivity::class.java)
            startActivityForResult(intent, ADD_PERSON_REQUEST_CODE)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_PERSON_REQUEST_CODE && resultCode == RESULT_OK) {
            val person = data?.getSerializableExtra("person") as? Person
            person?.let {
                allPeople.add(it)
                adapter.filter("") // muestra todo de nuevo
            }
        }
    }
}
