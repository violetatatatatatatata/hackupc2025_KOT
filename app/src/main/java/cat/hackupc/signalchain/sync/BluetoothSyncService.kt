package cat.hackupc.signalchain.sync

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.util.Log
import cat.hackupc.signalchain.model.SharedData
import java.io.*
import java.util.*
import kotlin.concurrent.thread

class BluetoothSyncService(
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
        acceptThread = thread {
            try {
                val serverSocket: BluetoothServerSocket =
                    bluetoothAdapter.listenUsingRfcommWithServiceRecord(serviceName, uuid)

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
            val pairedDevices: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
            for (device in pairedDevices) {
                try {
                    val socket = device.createRfcommSocketToServiceRecord(uuid)
                    socket.connect()
                    handleSocket(socket)
                    break
                } catch (e: IOException) {
                    Log.w("BluetoothSync", "Failed to connect to ${device.name}")
                }
            }
        }
    }

    private fun handleSocket(socket: BluetoothSocket) {
        thread {
            try {
                val input = BufferedReader(InputStreamReader(socket.inputStream))
                val output = BufferedWriter(OutputStreamWriter(socket.outputStream))

                // Enviar nuestros datos
                val jsonToSend = getLocalData().toJson()
                output.write(jsonToSend + "\n")
                output.flush()

                // Recibir los suyos
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
