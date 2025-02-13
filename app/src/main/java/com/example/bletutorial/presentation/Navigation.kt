package com.example.bletutorial.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation(
    onBluetoothStateChanged: ()-> Unit
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.StartScreen.route) {
        composable(Screen.StartScreen.route){
            StartScreen(navController = navController)
        }
        composable(Screen.DataDisplayScreen.route) {
            DataDisplayScreen(navController, onBluetoothStateChanged)
        }
        composable(Screen.LogDisplayScreen.route){
            LogDisplayScreen(navController = navController)
        }

    }
}

sealed class Screen(val route:String) {
    object StartScreen:Screen("start_screen")
    object DataDisplayScreen:Screen("data_display_screen")

    object LogDisplayScreen:Screen("log_display_screen")
}