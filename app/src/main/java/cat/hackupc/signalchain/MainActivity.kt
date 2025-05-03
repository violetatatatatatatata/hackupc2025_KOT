package cat.hackupc.signalchain

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cat.hackupc.signalchain.model.SharedData
import cat.hackupc.signalchain.sync.BluetoothSyncService
import cat.hackupc.signalchain.repository.FlightRepository
import cat.hackupc.signalchain.repository.PersonRepository
import cat.hackupc.signalchain.repository.AlertRepository

class MainActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val lang = newBase.getSharedPreferences("settings", MODE_PRIVATE).getString("lang", "en") ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnFlights = findViewById<Button>(R.id.btnFlights)
        val btnPeople = findViewById<Button>(R.id.btnPeople)
        val btnAlerts = findViewById<Button>(R.id.btnAlerts)

        btnFlights.text = getString(R.string.btn_flights)
        btnPeople.text = getString(R.string.btn_people)
        btnAlerts.text = getString(R.string.btn_alerts)

        btnFlights.setOnClickListener {
            startActivity(Intent(this, FlightListActivity::class.java))
        }

        btnPeople.setOnClickListener {
            startActivity(Intent(this, PersonListActivity::class.java))
        }

        btnAlerts.setOnClickListener {
            startActivity(Intent(this, AlertListActivity::class.java))
        }

        checkAndStartBluetoothSync()
    }

    private fun checkAndStartBluetoothSync() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val permissions = arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            val missingPermissions = permissions.filter {
                checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
            }

            if (missingPermissions.isNotEmpty()) {
                requestPermissions(missingPermissions.toTypedArray(), 1001)
                return
            }
        }

        startBluetoothSync()
    }

    private fun startBluetoothSync() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Toast.makeText(this, "Bluetooth is not available or not enabled", Toast.LENGTH_SHORT).show()
            return
        }

        val service = BluetoothSyncService(
            context = this,
            bluetoothAdapter = bluetoothAdapter,
            getLocalData = {
                SharedData(
                    FlightRepository.flights,
                    PersonRepository.people,
                    AlertRepository.alerts
                )
            },
            onDataReceived = { received ->
                FlightRepository.merge(received.flights)
                PersonRepository.merge(received.people)
                AlertRepository.merge(received.alerts)

                FlightListActivity.refreshData()
                PersonListActivity.refreshData()
                AlertListActivity.refreshData()
            }
        )
        Log.d("BluetoothDebug", "Bluetooth enabled: ${bluetoothAdapter.isEnabled}")
        service.start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001 && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            startBluetoothSync()
        } else {
            Toast.makeText(this, "Bluetooth permissions required for syncing", Toast.LENGTH_SHORT).show()
        }
    }
}
