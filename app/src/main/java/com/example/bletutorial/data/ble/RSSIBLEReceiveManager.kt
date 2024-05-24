package com.example.bletutorial.data.ble

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.bletutorial.data.ConnectionState
import com.example.bletutorial.data.RSSIReceiveManager
import com.example.bletutorial.data.RSSIResult
import com.example.bletutorial.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class RSSIBLEReceiveManager @Inject constructor (
    private val bluetoothAdapter: BluetoothAdapter,
    private val context: Context
) : RSSIReceiveManager {

    private val DEVICE_NAME = "BlueCharm_953078"

    override val data: MutableSharedFlow<Resource<RSSIResult>> = MutableSharedFlow()

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    private var gatt:  BluetoothGatt? = null

    private var isScanning = false

    private var coroutineScope = CoroutineScope(Dispatchers.Default)

    private val scanCallBack = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (result?.device?.name == DEVICE_NAME) {
                    coroutineScope.launch {
                        data.emit(Resource.Loading(message = "Connecting to device..."))
                    }
                    if (isScanning) {
                        result.device.connectGatt(context, false, gattCallback)
                        isScanning = false
                        bleScanner.stopScan(this)
                    }
                }
            }
        }
    }

    override fun updateRSSI() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        gatt?.readRemoteRssi()
    }




    override fun reconnect() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        gatt?.connect()
    }

    override fun disconnect() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        gatt?.disconnect()
    }

    override fun startReceiving() {
        coroutineScope.launch {
            data.emit(Resource.Loading(message = "Scanning Ble devices..."))
        }
        isScanning = true
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        bleScanner.startScan(null,scanSettings, scanCallBack)
    }

    private var currentConnectionAttempt = 1
    private var MAXIMUM_CONNECTION_ATTEMPTS = 5

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    coroutineScope.launch {
                        data.emit(Resource.Loading(message = "getting RSSI"))
                    }
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    gatt.readRemoteRssi()
                    this@RSSIBLEReceiveManager.gatt = gatt
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    coroutineScope.launch {
                        data.emit(Resource.Success(data = RSSIResult(0, ConnectionState.DisConnected)))
                    }
                    gatt.close()
                }
            } else {
                 gatt.close()
                currentConnectionAttempt+= 1
                coroutineScope.launch {
                    data.emit(Resource.Loading(
                        message = "Attempting to connect $currentConnectionAttempt/$MAXIMUM_CONNECTION_ATTEMPTS"))
                }
                if(currentConnectionAttempt < MAXIMUM_CONNECTION_ATTEMPTS) {
                    startReceiving()
                } else {
                    coroutineScope.launch {
                        data.emit(Resource.Error(errorMessage = "Could not connect to ble device"))
                    }
                }
            }
        }

        override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                coroutineScope.launch {
                    data.emit(Resource.Success(RSSIResult(rssi, ConnectionState.Connected)))
                }
            } else {
                coroutineScope.launch {
                    data.emit(Resource.Error(errorMessage = "Could not get RSSI"))
                }
            }
        }


        override fun onServicesDiscovered(gatt: BluetoothGatt, status:Int) {
            with(gatt) {
                printGattTable()
                coroutineScope.launch {
                    data.emit(Resource.Loading(message = "Adjusting MTU space .."))
                }
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                   return
                }
                gatt.requestMtu(517)

            }
        }
    }


    override fun closeConnection() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        bleScanner.stopScan(scanCallBack)
        gatt?.close()
    }

}