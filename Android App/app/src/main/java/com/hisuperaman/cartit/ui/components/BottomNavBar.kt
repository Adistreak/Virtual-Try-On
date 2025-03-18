package com.hisuperaman.cartit.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun BottomNavBar(
    onHomeClick: () -> Unit,
    onUserClick: () -> Unit,
    onCameraClick: () -> Unit,
    currentRoute: String?
) {
//    val items = listOf(
//        Screen.Home,
//        Screen.Cart,
//        Screen.Profile
//    )
//    var selectedItem by remember { mutableStateOf(0) }

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "home") },
            label = { Text("Home") },
            selected = currentRoute=="Home",
            onClick = {
                onHomeClick()
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.CameraAlt, contentDescription = "virtual try on") },
            label = { Text("Virtual Try On") },
            selected = currentRoute=="Camera",
            onClick = {
                onCameraClick()
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "user") },
            label = { Text("User") },
            selected = currentRoute=="User",
            onClick = {
                onUserClick()
            }
        )
    }
}
