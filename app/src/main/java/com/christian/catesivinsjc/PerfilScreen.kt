package com.christian.catesivinsjc

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.christian.catesivinsjc.viewmodel.UsuariViewModel

@Composable
fun PerfilScreen(
    navController: NavController? = null, // Opcional per si encara no està integrat
    viewModel: UsuariViewModel = viewModel()
) {
    val usuari by viewModel.usuari.collectAsState()
    val isLoggedOut by viewModel.isLoggedOut.collectAsState()
    val context = LocalContext.current

    // Carreguem l'usuari actual només començar
    LaunchedEffect(Unit) {
        viewModel.loadCurrentUser()
    }

    // Si fem logout, tornem a la pantalla de registre
    LaunchedEffect(isLoggedOut) {
        if (isLoggedOut && navController != null) {
            navController.navigate("signup") {
                popUpTo("home") { inclusive = true }
            }
            Toast.makeText(context, "Sessió tancada", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "El Meu Perfil",
            style = MaterialTheme.typography.headlineMedium,
            color = colorResource(id = R.color.bordeaux),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (usuari == null) {
            // Mostrem carregant mentre no tinguem dades
            CircularProgressIndicator(color = colorResource(id = R.color.bordeaux))
        } else {
            // Icona gran d'usuari
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Avatar",
                modifier = Modifier.size(120.dp),
                tint = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            // Dades de l'usuari
            ProfileItem(label = "Nom", value = if (usuari!!.nom.isNotEmpty()) usuari!!.nom else "Sense nom definit")
            ProfileItem(label = "Email", value = usuari!!.email)
            ProfileItem(label = "Rol", value = usuari!!.rol)

            Spacer(modifier = Modifier.weight(1f))

            // Botó de Tancar Sessió
            Button(
                onClick = { viewModel.logout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {
                Text("Tancar Sessió")
            }
        }
    }
}

@Composable
fun ProfileItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        androidx.compose.material3.Divider(color = Color.LightGray)
    }
}
