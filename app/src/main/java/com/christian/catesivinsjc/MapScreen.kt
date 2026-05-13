package com.christian.catesivinsjc

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
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
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
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

    // Estat per guardar la posició de l'usuari
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    
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

    // Funció per calcular la distància
    fun calcularDistancia(cataLat: Double, cataLon: Double): String {
        val usuari = userLocation ?: return ""
        val results = FloatArray(1)
        Location.distanceBetween(
            usuari.latitude, usuari.longitude,
            cataLat, cataLon,
            results
        )
        val metres = results[0]
        return if (metres < 1000) {
            context.getString(R.string.unitat_metres, metres.toInt())
        } else {
            String.format(Locale.getDefault(), context.getString(R.string.unitat_kilomtres), metres / 1000)
        }
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            try {
                @SuppressLint("MissingPermission")
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val currentPos = LatLng(location.latitude, location.longitude)
                        userLocation = currentPos
                        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(currentPos, 12f))
                    }
                }
            } catch (e: Exception) { e.printStackTrace() }
        } else {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true
        )
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 220.dp,
        sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        sheetContainerColor = Color.White,
        sheetShadowElevation = 16.dp,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
            ) {
                // Títol
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Text(
                        text = stringResource(R.string.mapa_titol_properes),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2C2C2C)
                    )
                    Text(
                        text = stringResource(R.string.mapa_subtitol_properes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, top = 0.dp, end = 20.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cates) { cata ->
                        val distanciaStr = if (cata.coordenades != null) {
                            calcularDistancia(cata.coordenades!!.latitude, cata.coordenades!!.longitude)
                        } else ""

                        MiniCataItem(cata, distanciaStr) {
                            if (cata.coordenades != null) {
                                val pos = LatLng(cata.coordenades!!.latitude, cata.coordenades!!.longitude)
                                cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(pos, 15f))
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                uiSettings = MapUiSettings(myLocationButtonEnabled = true)
            ) {
                cates.forEach { cata ->
                    if (cata.coordenades != null) {
                        val pos = LatLng(cata.coordenades!!.latitude, cata.coordenades!!.longitude)
                        Marker(
                            state = MarkerState(position = pos),
                            title = cata.nom,
                            snippet = cata.nomBodega,
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
    }
}

@Composable
fun MiniCataItem(
    cata: com.christian.catesivinsjc.model.Cata, 
    distancia: String, 
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(colorResource(id = R.color.copa).copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.LocationOn, 
                    null, 
                    tint = colorResource(id = R.color.copa),
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(text = cata.nom, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = cata.nomBodega, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            if (distancia.isNotEmpty()) {
                Text(
                    text = distancia,
                    style = MaterialTheme.typography.labelLarge,
                    color = colorResource(id = R.color.copa),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
