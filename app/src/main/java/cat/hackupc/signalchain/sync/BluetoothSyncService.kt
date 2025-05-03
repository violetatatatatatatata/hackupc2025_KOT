package cat.hackupc.signalchain.sync

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
        searchForPeers()
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

                while (true) {
                    val socket: BluetoothSocket = serverSocket.accept()
                    handleSocket(socket)
                    serverSocket.close()
                    break
                }
            } catch (e: IOException) {
                Log.e("BluetoothSync", "Server error: ${e.message}")
            }
        }
    }

    private fun searchForPeers() {
        connectThread = thread {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w("BluetoothSync", "Missing BLUETOOTH_CONNECT permission")
                return@thread
            }

            val pairedDevices = bluetoothAdapter.bondedDevices
            val discoveredDevices = mutableSetOf<BluetoothDevice>()

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
                    handleSocket(socket)
                    return@thread
                } catch (e: Exception) {
                    Log.w("BluetoothSync", "Failed to connect to paired ${device.name}: ${e.message}")
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w("BluetoothSync", "Missing BLUETOOTH_SCAN permission")
                return@thread
            }

            bluetoothAdapter.startDiscovery()

            val receiver = object : BroadcastReceiver() {
                override fun onReceive(ctx: Context?, intent: Intent?) {
                    val action = intent?.action
                    if (BluetoothDevice.ACTION_FOUND == action) {
                        val device: BluetoothDevice? =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                        device?.let {
                            if (!discoveredDevices.contains(it)) {
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
                onDataReceived(receivedData)

                socket.close()
            } catch (e: Exception) {
                Log.e("BluetoothSync", "Socket error: ${e.message}")
            }
        }
    }
}
