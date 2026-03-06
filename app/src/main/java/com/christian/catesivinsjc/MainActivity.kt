package com.christian.catesivinsjc

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.christian.catesivinsjc.ui.theme.CatesivinsJCTheme
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        enableEdgeToEdge()
        setContent {
            CatesivinsJCTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Comprovem si l'usuari ja està loguejat per decidir on començar
    // Si hi ha usuari -> Home. Si no -> Sign Up
    val startDestination = if (FirebaseAuth.getInstance().currentUser != null) "home" else "login"

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { 
            // Ocultem la barra de navegació a la pantalla de registre
            if (currentRoute != "signup" && currentRoute != "login") {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination, 
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("signup") { SignUpScreen(navController) }
            composable("login") { LoginScreen(navController) }
            composable("home") { HomeScreen(navController) }
            composable("mapa") { MapScreen(navController) }
            composable("perfil") { PerfilScreen(navController) }
            composable(
                route = "cata_detall/{cataId}",
                arguments = listOf(navArgument("cataId") { type = NavType.StringType })
            ) { backStackEntry ->
                // Recuperem l'ID que hem passat per la ruta
                val cataId = backStackEntry.arguments?.getString("cataId") ?: ""
                CataDetailScreen(cataId = cataId, navController = navController)
            }
        }
    }
}
