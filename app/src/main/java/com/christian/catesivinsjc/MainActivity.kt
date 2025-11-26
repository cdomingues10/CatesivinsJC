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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
    val startDestination = if (FirebaseAuth.getInstance().currentUser != null) "home" else "signup"

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { 
            // Ocultem la barra de navegació a la pantalla de registre
            if (currentRoute != "signup") {
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
            composable("home") { HomeScreen() }
            composable("mapa") { MapScreen() }
            
            // AQUI ESTÀ EL CANVI: Connectem la ruta "perfil" amb la nova pantalla
            composable("perfil") { PerfilScreen(navController) }
        }
    }
}
