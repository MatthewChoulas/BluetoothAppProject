package com.example.bletutorial

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.bletutorial.presentation.Navigation
import com.example.bletutorial.ui.theme.BLETutorialTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

internal const val ACTION_PERMISSIONS_GRANTED = "BluetoothPermission.permissions_granted"
internal const val ACTION_PERMISSIONS_DENIED = "BluetoothPermission.permissions_denied"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var  bluetoothAdapter: BluetoothAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BLETutorialTheme {
               Navigation(
                   {showBluetoothDialog()}
               )
            }
        }
    }


    override fun onStart() {
        super.onStart()

        when {
            checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED -> {
                sendBroadcast(Intent(ACTION_PERMISSIONS_GRANTED))
                showBluetoothDialog()
            }
            else -> {
                requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 1)
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sendBroadcast(Intent(ACTION_PERMISSIONS_GRANTED))
        } else {
            sendBroadcast(Intent(ACTION_PERMISSIONS_DENIED))
        }
    }

    private var isBluetoothDialogAlreadyShown = false
    private fun showBluetoothDialog(){
        if(!bluetoothAdapter.isEnabled) {
            if (!isBluetoothDialogAlreadyShown) {
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startBluetoothIntentForResult.launch(enableBluetoothIntent)
                isBluetoothDialogAlreadyShown = true
            }
        }
    }

    private val startBluetoothIntentForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
            isBluetoothDialogAlreadyShown = false
            if(result.resultCode != Activity.RESULT_OK) {
                showBluetoothDialog()
        }
        }
}