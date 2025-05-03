package cat.hackupc.signalchain

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LanguageSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_language_selection)

        val tvChoose = findViewById<TextView>(R.id.tvChooseLanguage)
        val btnEnglish = findViewById<Button>(R.id.btnEnglish)
        val btnSpanish = findViewById<Button>(R.id.btnSpanish)

        tvChoose.text = getString(R.string.choose_language)
        btnEnglish.text = getString(R.string.english)
        btnSpanish.text = getString(R.string.spanish)

        btnEnglish.setOnClickListener {
            saveLangAndContinue("en")
        }

        btnSpanish.setOnClickListener {
            saveLangAndContinue("es")
        }
    }

    private fun saveLangAndContinue(lang: String) {
        getSharedPreferences("settings", MODE_PRIVATE)
            .edit().putString("lang", lang).apply()

        val intent = Intent(this, ExplanationActivity::class.java)
        startActivity(intent)
        finish()
    }
}
