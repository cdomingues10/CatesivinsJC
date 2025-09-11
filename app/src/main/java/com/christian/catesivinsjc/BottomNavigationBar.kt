package com.christian.catesivinsjc

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("home") },
            label = { Text("Home") },
            icon = { Icon(Icons.Filled.Home, contentDescription = null) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("mapa") },
            label = { Text("Mapa") },
            icon = { Icon(Icons.Filled.Place, contentDescription = null) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("perfil") },
            label = { Text("Perfil") },
            icon = { Icon(Icons.Filled.Person, contentDescription = null) }
        )
    }
}