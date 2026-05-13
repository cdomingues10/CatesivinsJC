package com.christian.catesivinsjc

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.christian.catesivinsjc.viewmodel.UsuariViewModel

@Composable
fun PerfilScreen(
    navController: NavController? = null, 
    viewModel: UsuariViewModel = viewModel()
) {
    val usuari by viewModel.usuari.collectAsState()
    val isLoggedOut by viewModel.isLoggedOut.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadCurrentUser()
    }

    LaunchedEffect(isLoggedOut) {
        if (isLoggedOut && navController != null) {
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
            Toast.makeText(context, context.getString(R.string.logout_message), Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.profile_title),
            style = MaterialTheme.typography.headlineMedium,
            color = colorResource(id = R.color.copa),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (usuari == null) {
            CircularProgressIndicator(color = colorResource(id = R.color.copa))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.carregant_perfil), 
                style = MaterialTheme.typography.bodySmall
            )
        } else {
            if (!usuari!!.fotoUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = usuari!!.fotoUrl,
                    contentDescription = stringResource(R.string.desc_avatar),
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = stringResource(R.string.desc_avatar),
                    modifier = Modifier.size(120.dp),
                    tint = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            ProfileItem(
                label = stringResource(R.string.profile_name), 
                value = usuari?.nom?.ifEmpty { stringResource(R.string.no_name_defined) } ?: stringResource(R.string.sense_nom)
            )
            ProfileItem(
                label = stringResource(R.string.profile_email), 
                value = usuari?.email ?: stringResource(R.string.sense_email)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.logout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {
                Text(stringResource(R.string.logout_button))
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
        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
    }
}
