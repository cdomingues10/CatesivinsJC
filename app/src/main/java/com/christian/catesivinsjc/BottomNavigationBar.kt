package com.christian.catesivinsjc

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        val items = listOf("Home", "Mapa", "Perfil")
        val icons = listOf(Icons.Default.Home, Icons.Default.Place, Icons.Default.Person)
        val routes = listOf("home", "mapa", "perfil")

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEachIndexed { index, item ->
            val isSelected = currentRoute == routes[index]
            
            NavigationBarItem(
                // Icona més gran utilitzant Modifier.size
                icon = { 
                    Icon(
                        imageVector = icons[index], 
                        contentDescription = item,
                        modifier = Modifier.size(32.dp) // Augmentem la mida a 32dp
                    ) 
                },
                label = { Text(item) },
                selected = isSelected,
                colors = NavigationBarItemDefaults.colors(
                    // Només canviem el color dels elements SELECCIONATS pel nou color Bordeus
                    selectedIconColor = colorResource(id = R.color.bordeaux),
                    selectedTextColor = colorResource(id = R.color.bordeaux),
                    // També posem l'indicador (la pastilla de fons) del mateix color però transparent
                    indicatorColor = colorResource(id = R.color.bordeaux).copy(alpha = 0.1f)
                ),
                onClick = {
                    navController.navigate(routes[index]) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
