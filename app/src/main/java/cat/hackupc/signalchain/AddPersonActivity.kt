package cat.hackupc.signalchain

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cat.hackupc.signalchain.model.Person
import java.text.SimpleDateFormat
import java.util.*

class AddPersonActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val lang = newBase.getSharedPreferences("settings", MODE_PRIVATE)
            .getString("lang", "en") ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_person)

        val editFirstName = findViewById<EditText>(R.id.editFirstName)
        val editLastName = findViewById<EditText>(R.id.editLastName)
        val editLocation = findViewById<EditText>(R.id.editLocation)

        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val btnSave = findViewById<Button>(R.id.btnSave)

        btnCancel.setOnClickListener {
            finish() // vuelve sin guardar
        }

        btnSave.setOnClickListener {
            val firstName = editFirstName.text.toString().trim()
            val lastName = editLastName.text.toString().trim()
            val location = editLocation.text.toString().trim()

            if (firstName.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_fill_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

            val newPerson = Person(firstName, lastName, location, currentTime)

            val resultIntent = Intent().apply {
                putExtra("person", newPerson)
            }

            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}
