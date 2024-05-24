package com.example.bletutorial.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun LogDisplayScreen(
    navController: NavController
) {

    val MyButtonColors =
    ButtonDefaults.buttonColors(Color.Blue, Color.White, Color.Red, Color.White)
    Column(

    ) {
        Button(colors=MyButtonColors,onClick = {}, modifier = Modifier.height(50.dp).fillMaxWidth(1.0f).background(Color.Blue)) {
            Text("Sign In Log", fontSize = 20.sp)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .shadow(8.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "May 3, 2024",
                    fontSize = 24.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp)
                )
                Column(
                    modifier = Modifier.weight(2f)
                ) {
                    Text(text = "Student ID: 123456")
                    Text(text = "Class: AP Economics")
                    Text(text = "Beacon ID: BlueCharm_953078")
                }
            }
        }




        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .shadow(8.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "May 4, 2024",
                    fontSize = 24.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp)
                )
                Column(
                    modifier = Modifier.weight(2f)
                ) {
                    Text(text = "Student ID: 123456")
                    Text(text = "Class: AP Lang")
                    Text(text = "Beacon ID: BlueCharm_953078")
                }
            }
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .shadow(8.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "May 5, 2024",
                    fontSize = 24.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp)
                )
                Column(
                    modifier = Modifier.weight(2f)
                ) {
                    Text(text = "Student ID: 123456")
                    Text(text = "Class: Mobile Apps")
                    Text(text = "Beacon ID: BlueCharm_953078")
                }
            }
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .shadow(8.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "May 6th, 2024",
                    fontSize = 24.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp)
                )
                Column(
                    modifier = Modifier.weight(2f)
                ) {
                    Text(text = "Student ID: 134084")
                    Text(text = "Class: Mobile Apps")
                    Text(text = "Beacon ID: BlueCharm_953078")
                }
            }
        }
    }


}