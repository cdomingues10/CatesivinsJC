package com.christian.catesivinsjc

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.christian.catesivinsjc.model.Cata
import com.christian.catesivinsjc.viewmodel.CataViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HomeScreen(navController: androidx.navigation.NavController, cataViewModel: CataViewModel = viewModel()) {

    // Carreguem la llista de cates en entrar
    LaunchedEffect(Unit) {
        cataViewModel.getCates()
    }

    val catesList by cataViewModel.cates.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {
        // --- CAPÇALERA MODERNA ---
        Surface(
            color = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocalBar,
                    contentDescription = stringResource(R.string.desc_copa),
                    tint = colorResource(id = R.color.copa),
                    modifier = Modifier.size(36.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.copa)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            items(catesList) { cata ->
                CataItem(cata, onClick = {
                    navController.navigate("cata_detall/${cata.cataId}")
                })
            }
        }
    }
}

@Composable
fun CataItem(cata: Cata, onClick: () -> Unit) {
    // Card per a cada cata de la llista
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column {
            // IMATGE DE LA BODEGA
            if (cata.imatgeBodega.isNotEmpty()) {
                AsyncImage(
                    model = cata.imatgeBodega,
                    contentDescription = stringResource(R.string.desc_imatge_bodega, cata.nomBodega),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Info principal: Nom i icona
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = cata.nom, 
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF333333)
                        )
                    }
                    
                    Icon(
                        imageVector = Icons.Default.LocalBar,
                        contentDescription = stringResource(R.string.desc_copa),
                        tint = colorResource(id = R.color.copa).copy(alpha = 0.2f),
                        modifier = Modifier.size(40.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Ubicació de la bodega
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = stringResource(R.string.desc_lloc),
                        tint = colorResource(id = R.color.copa),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = cata.nomBodega, 
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.copa)
                    )
                }

                if (cata.direccioBodega.isNotEmpty()) {
                    Text(
                        text = cata.direccioBodega, 
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color(0xFFF0F0F0))
                Spacer(modifier = Modifier.height(12.dp))
                
                // Descripció curta de l'experiència
                Text(
                    text = cata.descripcio, 
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF555555),
                    lineHeight = 20.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                // Data de la cata (formatejada)
                val formattedDate = try {
                    SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(cata.data)
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                } catch (e: Exception) {
                    stringResource(R.string.data_per_confirmar)
                }
                
                Surface(
                    color = colorResource(id = R.color.copa).copy(alpha = 0.05f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = colorResource(id = R.color.copa),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colorResource(id = R.color.copa)
                        )
                    }
                }
            }
        }
    }
}
