package com.example.bletutorial.data

import com.example.bletutorial.util.Resource
import kotlinx.coroutines.flow.MutableSharedFlow

interface RSSIReceiveManager {
    val data: MutableSharedFlow<Resource<RSSIResult>>

    fun reconnect()

    fun disconnect()

    fun startReceiving()

    fun closeConnection()

    fun updateRSSI()
}