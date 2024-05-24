package com.example.bletutorial.presentation

import android.bluetooth.BluetoothAdapter
import android.content.Context.MODE_PRIVATE
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuDefaults.outlinedTextFieldColors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.bletutorial.data.ConnectionState
import com.example.bletutorial.presentation.permissions.PermissionUtils
import com.example.bletutorial.presentation.permissions.SystemBroadcastReceiver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import kotlin.math.truncate


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterialApi::class)
@Composable
fun DataDisplayScreen(
    navController: NavController,
    onBluetoothStateChanged: () -> Unit,
    viewModel: RSSIDataViewModel = hiltViewModel()
) {
        SystemBroadcastReceiver(systemAction = BluetoothAdapter.ACTION_STATE_CHANGED) { bluetoothState ->
            val action = bluetoothState?.action ?: return@SystemBroadcastReceiver
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED){
                onBluetoothStateChanged()
            }
        }


    val MyButtonColors  = buttonColors(Color.Blue, Color.White, Color.Red, Color.White)
    val SubmitButtonColors  = buttonColors(Color(0, 171, 102), Color.White, Color.Red, Color.White)
    val MyTextFieldColors = outlinedTextFieldColors(textColor = Color.Black, focusedBorderColor = Color.Blue, unfocusedBorderColor = Color.Blue, cursorColor = Color.Black )
    
    val permissionState = rememberMultiplePermissionsState(permissions = PermissionUtils.permissions)
    val lifecycleOwner = LocalLifecycleOwner.current
    val bleConnectionState = viewModel.connectionState

    var distanceCheck = false

    val N = 4.0
    val MEASURED_POWER = -59
    var text by remember { mutableStateOf("")}



    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver{
                _, event ->
                if(event == Lifecycle.Event.ON_START) {
                    permissionState.launchMultiplePermissionRequest()
                    if(permissionState.allPermissionsGranted && bleConnectionState == ConnectionState.DisConnected) {
                        viewModel.reconnect()
                    }
                }
                if(event == Lifecycle.Event.ON_STOP) {
                    if(bleConnectionState == ConnectionState.Connected) {
                        viewModel.disconnect()
                    }
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)


            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        })

    LaunchedEffect(key1 = permissionState.allPermissionsGranted) {
        if(permissionState.allPermissionsGranted) {
            if(bleConnectionState == ConnectionState.Uninitialized) {
                viewModel.initializeConnection()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Log.d("myTag", bleConnectionState.toString());
        Column(
            modifier = Modifier
                .fillMaxHeight(0.6f)
                .fillMaxWidth(0.75f)
                .aspectRatio(1f)
                .border(
                    BorderStroke(
                        5.dp, Color.Blue
                    ),
                    RoundedCornerShape(10.dp)
                ),
            verticalArrangement =  Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (bleConnectionState == ConnectionState.CurrentlyInitializing) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    if(viewModel.initializingMessages != null) {
                        Text(
                            text = viewModel.initializingMessages!!
                        )
                    }
                }
            } else if(!permissionState.allPermissionsGranted) {
                Text (
                    text = "Go to the app settings and allow the missing permissions.",
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(10.dp),
                    textAlign = TextAlign.Center
                )
            } else if (viewModel.errorMessage != null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = viewModel.errorMessage!!
                    )
                    Button(onClick = {
                        if(permissionState.allPermissionsGranted) {
                            viewModel.initializeConnection()
                        }
                    }) {
                        Text(
                            "Try again"
                        )
                    }
                }
            } else if (bleConnectionState == ConnectionState.Connected) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {

                        Text(
                            modifier = Modifier.padding(5.dp),
                            text = "RSSI: ${viewModel.rssiValue}",
                            style = MaterialTheme.typography.h6
                        )

                        val distance = truncate(Math.pow(10.0,((MEASURED_POWER - viewModel.rssiValue)/(10*N))) * 100) / 100
                        if (viewModel.rssiValue > -70.0) {
                            distanceCheck = true
                        } else {
                            distanceCheck = false
                        }

                        Text (
                            modifier = Modifier.padding(5.dp),
                            text = "Distance: ${distance}",
                            style = MaterialTheme.typography.h6
                        )


                    Button(onClick = {
                        viewModel.updateValues()
                    }, modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth(0.9f), colors = MyButtonColors) {
                        Text("Reload")
                    }

                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OutlinedTextField(
                            value = text,
                            colors = MyTextFieldColors,
                            onValueChange = {text = it},
                            label = { Text("ID:", color = Color.Blue, fontSize = 20.sp)},
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .height(65.dp),
                            textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.Normal, fontSize = 20.sp),
                        )
                        Button(onClick = {saveConnectionData()}, colors = SubmitButtonColors, modifier = Modifier.height(56.dp), enabled= distanceCheck) {
                            Text("Submit")
                        }
                    }



                }
            } else if (bleConnectionState == ConnectionState.DisConnected) {
                Button(onClick = {
                    viewModel.initializeConnection()
                }, colors = MyButtonColors) {
                    Text("Rescan")
                }
            }
        }
    }

    Button(onClick = {
        navController.navigate(Screen.LogDisplayScreen.route)
    }, colors = MyButtonColors, shape= RectangleShape, modifier = Modifier.height(50.dp).fillMaxWidth(1.0f)) {
        Text("View Log", fontSize = 20.sp)
    }

}

fun saveConnectionData(){}

