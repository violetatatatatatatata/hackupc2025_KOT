package cat.hackupc.signalchain

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cat.hackupc.signalchain.model.Alert
import java.text.SimpleDateFormat
import java.util.*

class AddAlertActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val lang = newBase.getSharedPreferences("settings", MODE_PRIVATE)
            .getString("lang", "en") ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_alert)

        val editTitle = findViewById<EditText>(R.id.editAlertTitle)
        val editMessage = findViewById<EditText>(R.id.editAlertMessage)
        val btnSave = findViewById<Button>(R.id.btnSaveAlert)
        val btnCancel = findViewById<Button>(R.id.btnCancelAlert)

        btnCancel.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            val title = editTitle.text.toString().trim()
            val message = editMessage.text.toString().trim()

            if (title.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_fill_alert), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

            val alert = Alert(title, message, timestamp)
            val intent = Intent().apply {
                putExtra("alert", alert)
            }

            setResult(RESULT_OK, intent)
            finish()
        }
    }
}
