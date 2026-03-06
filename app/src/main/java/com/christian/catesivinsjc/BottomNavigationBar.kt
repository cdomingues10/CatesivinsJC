package com.christian.catesivinsjc

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalBar
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
        val items = listOf("Cates", "Mapa", "Perfil")
        
        // ARA SÍ: Fem servir Icons.Default.LocalBar (la copa) gràcies a la llibreria nova
        val icons = listOf(Icons.Default.LocalBar, Icons.Default.Place, Icons.Default.Person)
        
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
                        modifier = Modifier.size(32.dp) 
                    ) 
                },
                label = { Text(item) },
                selected = isSelected,
                colors = NavigationBarItemDefaults.colors(
                    // Només canviem el color dels elements SELECCIONATS pel nou color Copa
                    selectedIconColor = colorResource(id = R.color.copa),
                    selectedTextColor = colorResource(id = R.color.copa),
                    // També posem l'indicador (la pastilla de fons) del mateix color però transparent
                    indicatorColor = colorResource(id = R.color.copa).copy(alpha = 0.1f)
                ),
                onClick = {
                    // Si l'usuari clica "Cates" (home) i ja estem a "home" o en un "cata_detall"
                    // forcem que torni a la ruta base de la secció.
                    val route = routes[index]
                    navController.navigate(routes[index]) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = false
                        }
                        launchSingleTop = true }
                }
            )
        }
    }
}
