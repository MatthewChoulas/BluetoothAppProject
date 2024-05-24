package com.example.bletutorial.data

sealed interface ConnectionState {
    object Connected: ConnectionState
    object DisConnected: ConnectionState
    object Uninitialized: ConnectionState
    object CurrentlyInitializing: ConnectionState
}