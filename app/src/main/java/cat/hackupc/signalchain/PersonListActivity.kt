package cat.hackupc.signalchain

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cat.hackupc.signalchain.model.Person
import cat.hackupc.signalchain.repository.PersonRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.widget.Toolbar

class PersonListActivity : AppCompatActivity() {

    private lateinit var adapter: PersonAdapter
    private lateinit var searchView: SearchView

    override fun attachBaseContext(newBase: Context) {
        val lang = newBase.getSharedPreferences("settings", MODE_PRIVATE)
            .getString("lang", "en") ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.personRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PersonAdapter(PersonRepository.people)
        recyclerView.adapter = adapter

        searchView = findViewById(R.id.personSearchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText ?: "")
                return true
            }
        })

        findViewById<FloatingActionButton>(R.id.fabAddPerson).setOnClickListener {
            val intent = Intent(this, AddPersonActivity::class.java)
            startActivityForResult(intent, 101)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101 && resultCode == RESULT_OK) {
            val person = data?.getSerializableExtra("person") as? Person
            person?.let {
                PersonRepository.people.add(it)
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
        private lateinit var instance: PersonListActivity

        fun refreshData() {
            if (::instance.isInitialized) {
                instance.adapter.filter(instance.searchView.query.toString())
            }
        }
    }
}
