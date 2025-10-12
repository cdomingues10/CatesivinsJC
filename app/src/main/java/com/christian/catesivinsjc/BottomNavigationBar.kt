package com.christian.catesivinsjc

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.christian.catesivinsjc.ui.theme.colorVi

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        // Definim els colors per als elements de la barra de navegació
        val itemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = colorVi,           // El teu color per a la icona seleccionada
            unselectedIconColor = Color.Gray,      // Un color neutre per a les no seleccionades
            selectedTextColor = colorVi,           // El teu color per al text seleccionat
            unselectedTextColor = Color.Gray       // Un color neutre per al text no seleccionat
        )

        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") },
            label = { Text(stringResource(R.string.bottom_nav_home)) },
            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
            colors = itemColors
        )
        NavigationBarItem(
            selected = currentRoute == "mapa",
            onClick = { navController.navigate("mapa") },
            label = { Text(stringResource(R.string.bottom_nav_mapa)) },
            icon = { Icon(Icons.Filled.Place, contentDescription = null) },
            colors = itemColors
        )
        NavigationBarItem(
            selected = currentRoute == "perfil",
            onClick = { navController.navigate("perfil") },
            label = { Text(stringResource(R.string.bottom_nav_perfil)) },
            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
            colors = itemColors
        )
    }
}
