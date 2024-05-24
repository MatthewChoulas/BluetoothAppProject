package com.example.bletutorial.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun StartScreen(
    navController: NavController
) {

    val MyButtonColors =
        ButtonDefaults.buttonColors(Color.Blue, Color.White, Color.Red, Color.White)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(Color.Blue, CircleShape)
                .clickable {
                    navController.navigate(Screen.DataDisplayScreen.route) {
                            popUpTo(Screen.StartScreen.route) {
                                inclusive = true
                            }
                           }
                }
                    ,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Scan",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }


    }

    Button(onClick = {
        navController.navigate(Screen.LogDisplayScreen.route)
    }, colors = MyButtonColors, shape= RectangleShape, modifier = Modifier.height(50.dp).fillMaxWidth(1.0f)) {
        Text("View Log", fontSize = 20.sp)
    }
}