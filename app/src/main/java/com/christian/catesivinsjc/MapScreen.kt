package com.christian.catesivinsjc

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.christian.catesivinsjc.viewmodel.CataViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapScreen(
    // Necessitem el navController
    navController: androidx.navigation.NavController,
    viewModel: CataViewModel = viewModel()
) {

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    LaunchedEffect(Unit) {
        viewModel.getCates()
    }
    val cates by viewModel.cates.collectAsState()

    // València per defecte
    val defaultLocation = LatLng(38.92008939541156, -0.11935877616720579)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 9f)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            try {
                @SuppressLint("MissingPermission")
                val locationResult = fusedLocationClient.lastLocation
                locationResult.addOnSuccessListener { location ->
                    if (location != null) {
                        cameraPositionState.move(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(location.latitude, location.longitude), 12f
                            )
                        )
                    }
                }
            } catch (e: Exception) { e.printStackTrace() }
        } else {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    // --- INTERFÍCIE PRINCIPAL ---
    Column(modifier = Modifier.fillMaxSize()) {

        // --- MAPA (75%) ---
        Box(modifier = Modifier.weight(0.75f)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                uiSettings = MapUiSettings(myLocationButtonEnabled = true)
            ) {
                // AQUI PINTEM ELS MARCADORS
                cates.forEach { cata ->
                    if (cata.coordenades != null) {
                        val pos = LatLng(cata.coordenades!!.latitude, cata.coordenades!!.longitude)

                        Marker(
                            state = MarkerState(position = pos),
                            title = cata.nom,
                            snippet = cata.nomBodega,
                            // Quan cliquen la finestreta d'informació, anem al detall?
                            // Nota: Marker no té onClick directe fàcil per navegar,
                            // normalment es fa amb onInfoWindowClick.
                            onInfoWindowClick = {
                                navController.navigate("cata_detall/${cata.cataId}")
                            }
                        )
                    }
                }
            }

            if (cates.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = colorResource(id = R.color.copa)
                )
            }
        }

        // --- LLISTA (25%) ---
        Surface(
            modifier = Modifier.weight(0.25f),
            shadowElevation = 16.dp,
            color = Color(0xFFF9F9F9),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column {
                // Barreta decorativa
                Box(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), contentAlignment = Alignment.Center) {
                    Box(modifier = Modifier.width(40.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(Color.LightGray))
                }

                Text(
                    text = "Properes Cates",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.copa),
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
                )

                LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                    items(cates) { cata ->
                        MiniCataItem(cata) {
                            // Clic a la llista -> Mou la càmera a la cata
                            if (cata.coordenades != null) {
                                val pos = LatLng(cata.coordenades!!.latitude, cata.coordenades!!.longitude)
                                cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(pos, 15f))
                            }
                            // O si prefereixes navegar:
                            // navController.navigate("cata_detall/${cata.cataId}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MiniCataItem(cata: com.christian.catesivinsjc.model.Cata, onClick: () -> Unit) {
    androidx.compose.material3.Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color.White),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = cata.nom, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = cata.nomBodega, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}