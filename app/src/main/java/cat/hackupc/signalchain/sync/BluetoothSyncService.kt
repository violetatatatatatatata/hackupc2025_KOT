package cat.hackupc.signalchain.sync

import android.Manifest
import android.bluetooth.*
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import cat.hackupc.signalchain.model.SharedData
import java.io.*
import java.util.*
import kotlin.concurrent.thread

class BluetoothSyncService(
    private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter,
    private val getLocalData: () -> SharedData,
    private val onDataReceived: (SharedData) -> Unit
) {
    private val uuid: UUID = UUID.fromString("4d7c2f42-4f67-11ee-be56-0242ac120002")
    private val serviceName = "SignalChainSync"

    private var acceptThread: Thread? = null
    private var connectThread: Thread? = null

    fun start() {
        listenForConnections()
        searchPeriodically()
    }

    private fun listenForConnections() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("BluetoothSync", "Missing BLUETOOTH_CONNECT permission")
            return
        }

        acceptThread = thread {
            try {
                val serverSocket: BluetoothServerSocket =
                    bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(serviceName, uuid)
                Log.d("BluetoothSync", "Listening for incoming connections...")

                while (true) {
                    val socket: BluetoothSocket = serverSocket.accept()
                    Log.d("BluetoothSync", "Accepted incoming connection")
                    handleSocket(socket)
                }
            } catch (e: IOException) {
                Log.e("BluetoothSync", "Server error: ${e.message}")
            }
        }
    }

    private fun searchPeriodically() {
        connectThread = thread {
            while (true) {
                try {
                    searchForPeers()
                } catch (e: Exception) {
                    Log.e("BluetoothSync", "Error while searching: ${e.message}")
                }
                Thread.sleep(30000) // Reintenta cada 30 segundos
            }
        }
    }

    private fun searchForPeers() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("BluetoothSync", "Missing BLUETOOTH_CONNECT permission")
            return
        }

        val pairedDevices = bluetoothAdapter.bondedDevices

        for (device in pairedDevices) {
            try {
                Log.d("BluetoothSync", "Trying paired device: ${device.name}")
                val method = device.javaClass.getMethod(
                    "createInsecureRfcommSocketToServiceRecord",
                    UUID::class.java
                )
                val socket = method.invoke(device, uuid) as BluetoothSocket

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
                ) {
                    bluetoothAdapter.cancelDiscovery()
                }

                socket.connect()
                Log.d("BluetoothSync", "Connected to paired device: ${device.name}")
                handleSocket(socket)
                return
            } catch (e: Exception) {
                Log.w("BluetoothSync", "Failed to connect to paired ${device.name}: ${e.message}")
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("BluetoothSync", "Missing BLUETOOTH_SCAN permission")
            return
        }

        bluetoothAdapter.startDiscovery()
        Log.d("BluetoothSync", "Started discovery for nearby devices")

        val discoveredDevices = mutableSetOf<BluetoothDevice>()

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent?.action == BluetoothDevice.ACTION_FOUND) {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                    device?.let {
                        if (!discoveredDevices.contains(it) && it.name != null) {
                            discoveredDevices.add(it)
                            try {
                                Log.d("BluetoothSync", "Trying discovered device: ${it.name}")
                                val method = it.javaClass.getMethod(
                                    "createInsecureRfcommSocketToServiceRecord",
                                    UUID::class.java
                                )
                                val socket = method.invoke(it, uuid) as BluetoothSocket

                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    bluetoothAdapter.cancelDiscovery()
                                }

                                socket.connect()
                                Log.d("BluetoothSync", "Connected to discovered device: ${it.name}")
                                handleSocket(socket)
                                context.unregisterReceiver(this)
                            } catch (e: Exception) {
                                Log.w("BluetoothSync", "Failed to connect to discovered ${it.name}: ${e.message}")
                            }
                        }
                    }
                }
            }
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(receiver, filter)
    }

    private fun handleSocket(socket: BluetoothSocket) {
        thread {
            try {
                val input = BufferedReader(InputStreamReader(socket.inputStream))
                val output = BufferedWriter(OutputStreamWriter(socket.outputStream))

                val jsonToSend = getLocalData().toJson()
                output.write(jsonToSend + "\n")
                output.flush()

                val incomingJson = input.readLine()
                val receivedData = SharedData.fromJson(incomingJson)
                Log.d("BluetoothSync", "Data received from peer. Merging...")
                onDataReceived(receivedData)

                socket.close()
            } catch (e: Exception) {
                Log.e("BluetoothSync", "Socket error: ${e.message}")
            }
        }
    }
}
