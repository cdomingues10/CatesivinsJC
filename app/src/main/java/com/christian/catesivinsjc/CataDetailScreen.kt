package com.christian.catesivinsjc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.christian.catesivinsjc.model.Vi
import com.christian.catesivinsjc.viewmodel.CataDetailViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CataDetailScreen(
    cataId: String,
    navController: NavController,
    viewModel: CataDetailViewModel = viewModel()
) {
    // Carreguem les dades
    LaunchedEffect(cataId) {
        viewModel.loadCataDetails(cataId)
    }
    val cata by viewModel.cata.collectAsState()
    val bodega by viewModel.bodega.collectAsState()
    val vins by viewModel.vins.collectAsState()

    // Color principal (si no existeix bordeaux, fem servir un fallback)
    val mainColor = colorResource(id = R.color.copa)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = cata?.nom ?: stringResource(R.string.detalls_cata),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = mainColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .background(Color(0xFFFAFAFA)) // Fons gris molt suau
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
        ) {
            
            // --- SECCIÓ 1: INFORMACIÓ GENERAL ---
            if (cata != null) {
                Text(
                    text = "Informació de la Cata",
                    style = MaterialTheme.typography.titleMedium,
                    color = mainColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Data
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            val dateFormatted = try {
                                SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(cata!!.data)
                            } catch (e: Exception) { "Data desconeguda" }
                            Text(text = dateFormatted, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Descripció
                        Text(
                            text = cata!!.descripcio,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF555555),
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- SECCIÓ 2: LA BODEGA ---
            if (bodega != null) {
                Text(
                    text = "Lloc de l'event",
                    style = MaterialTheme.typography.titleMedium,
                    color = mainColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WineBar, null, tint = mainColor, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = bodega!!.Nom, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))

                        InfoRow(icon = Icons.Default.LocationOn, text = bodega!!.Ubicacio)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // FIX: Convertim el Long a String
                        InfoRow(icon = Icons.Default.Phone, text = bodega!!.Telefon.toString())
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- SECCIÓ 3: ELS VINS ---
            if (vins.isNotEmpty()) {
                Text(
                    text = "🍷 Vins a tastar",
                    style = MaterialTheme.typography.headlineSmall,
                    color = mainColor,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                vins.forEach { vi ->
                    ViCard(vi = vi, mainColor = mainColor)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else {
                Text(
                    text = "Carregant vins o sense vins assignats...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    fontStyle = FontStyle.Italic
                )
            }
            
            // Espai extra al final per l'scroll
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF444444))
    }
}

@Composable
fun ViCard(vi: Vi, mainColor: Color) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            
            // Encapçalament del Vi
            Row(
                modifier = Modifier
                    .background(mainColor.copy(alpha = 0.08f))
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White,
                    modifier = Modifier.size(48.dp),
                    shadowElevation = 2.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.WineBar,
                            contentDescription = "Vi",
                            tint = mainColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = vi.Nom,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF222222)
                    )
                    Text(
                        text = "${vi.Tipus} • ${vi.Any}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = mainColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Contingut del Vi
            Column(modifier = Modifier.padding(16.dp)) {
                
                // Varietat (Etiqueta) - ACTUALITZAT PER A LLISTA DE STRINGS
                if (vi.Varietat.isNotEmpty()) {
                    
                    // Unim la llista amb comes i espai
                    val varietatNeta = vi.Varietat.joinToString(", ")

                    Surface(
                        color = Color(0xFFEEEEEE),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = "🍇 $varietatNeta",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                // Descripció
                Text(
                    text = "Nota de tast:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = if (vi.Descripcio.isNotEmpty()) vi.Descripcio else "Sense descripció disponible.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666),
                    lineHeight = 20.sp,
                    fontStyle = if (vi.Descripcio.isEmpty()) FontStyle.Italic else FontStyle.Normal
                )
            }
        }
    }
}
