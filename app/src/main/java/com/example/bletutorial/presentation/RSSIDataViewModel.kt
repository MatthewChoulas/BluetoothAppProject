package com.example.bletutorial.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bletutorial.data.ConnectionState
import com.example.bletutorial.data.RSSIReceiveManager
import com.example.bletutorial.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel

class RSSIDataViewModel @Inject constructor(
    private val rssiReceiveManager: RSSIReceiveManager
) :ViewModel() {
    var initializingMessages by mutableStateOf<String?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var rssiValue by mutableStateOf(0)
        private set

    var connectionState by mutableStateOf<ConnectionState>(ConnectionState.Uninitialized)

    private fun subscribeToChanges(){
        viewModelScope.launch {
            rssiReceiveManager.data.collect{result ->
                when (result) {
                    is Resource.Success -> {
                        connectionState = result.data.connectionState
                        rssiValue = result.data.rssi
                    }

                    is Resource.Loading -> {
                        initializingMessages = result.message
                        connectionState = ConnectionState.CurrentlyInitializing
                    }

                    is Resource.Error -> {
                        errorMessage = result.errorMessage
                        connectionState =  ConnectionState.Uninitialized
                    }
                }
            }
        }
    }

    fun updateValues() {
        rssiReceiveManager.updateRSSI()
    }
    fun disconnect() {
        rssiReceiveManager.disconnect()
    }

    fun reconnect() {
        rssiReceiveManager.reconnect()
    }

    fun initializeConnection(){
        errorMessage = null
        subscribeToChanges()
        rssiReceiveManager.startReceiving()
    }

    override fun onCleared() {
        super.onCleared()
        rssiReceiveManager.closeConnection()
    }

}