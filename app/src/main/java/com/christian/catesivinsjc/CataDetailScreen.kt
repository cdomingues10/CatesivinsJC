package com.christian.catesivinsjc

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.christian.catesivinsjc.model.Vi
import com.christian.catesivinsjc.viewmodel.CataDetailViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.graphics.Brush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CataDetailScreen(
    cataId: String,
    navController: NavController,
    viewModel: CataDetailViewModel = viewModel()
) {
    LaunchedEffect(cataId) {
        viewModel.loadCataDetails(cataId)
    }
    val cata by viewModel.cata.collectAsState()
    val bodega by viewModel.bodega.collectAsState()
    val vins by viewModel.vins.collectAsState()

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
            .background(Color(0xFFFDFDFD))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
        ) {
            
            cata?.let { currentCata ->
                Text(
                    text = "L'Experiència",
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
                            Icon(Icons.Default.CalendarToday, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            val dateFormatted = try {
                                SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(currentCata.data)
                            } catch (e: Exception) { "Data desconeguda" }
                            Text(text = dateFormatted, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = currentCata.descripcio, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            bodega?.let { currentBodega ->
                Text(
                    text = "Ubicació",
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
                            Text(text = currentBodega.Nom, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF5F5F5))
                        InfoRow(icon = Icons.Default.LocationOn, text = currentBodega.Ubicacio)
                        Spacer(modifier = Modifier.height(8.dp))
                        InfoRow(icon = Icons.Default.Phone, text = currentBodega.Telefon.toString())
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Selecció de Vins",
                style = MaterialTheme.typography.headlineSmall,
                color = mainColor,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (vins.isNotEmpty()) {
                vins.forEach { vi ->
                    ViCard(vi = vi, mainColor = mainColor)
                }
            } else {
                Text(
                    text = "Encara no hi ha vins assignats a aquesta cata.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ViCard(vi: Vi, mainColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(
                elevation = 8.dp, 
                shape = RoundedCornerShape(16.dp), 
                spotColor = mainColor.copy(alpha = 0.2f)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Contenidor de la imatge amb l'efecte de l'ampolla "flotant"
            Box(
                modifier = Modifier.size(90.dp),
                contentAlignment = Alignment.Center
            ) {
                // Ombra suau sota l'ampolla
                Canvas(modifier = Modifier.size(45.dp).offset(y = 40.dp)) {
                    drawOval(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.15f), // Centre més fosc
                                Color.Transparent               // S'esvaeix cap a fora
                            ),
                            center = center,
                            radius = size.width / 2
                        )
                    )
                }
                
                if (vi.Imatgeurl.isNotEmpty()) {
                    AsyncImage(
                        model = vi.Imatgeurl,
                        contentDescription = "Imatge del vi ${vi.Nom}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.LocalBar,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(45.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Textos detallats
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = vi.Nom,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFF2C2C2C)
                )
                Text(
                    text = "${vi.Tipus} • ${vi.Any}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = mainColor,
                    fontWeight = FontWeight.Medium
                )
                if (vi.Descripcio.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = vi.Descripcio,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF444444))
    }
}
