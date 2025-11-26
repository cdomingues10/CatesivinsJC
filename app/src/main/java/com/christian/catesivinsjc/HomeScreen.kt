package com.christian.catesivinsjc

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.christian.catesivinsjc.model.Cata
import com.christian.catesivinsjc.viewmodel.CataViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HomeScreen(cataViewModel: CataViewModel = viewModel()) {

    LaunchedEffect(Unit) {
        cataViewModel.getCates()
    }

    val catesList by cataViewModel.cates.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Llistat de Cates",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            items(catesList) { cata ->
                CataItem(cata)
            }
        }
    }
}

@Composable
fun CataItem(cata: Cata) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Títol de la Cata
            Text(
                text = cata.nom, 
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Descripció
            Text(
                text = cata.descripcio, 
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // INFORMACIÓ DE LA BODEGA (Nom i Adreça)
            Text(
                text = "📍 Lloc: ${cata.nomBodega}", 
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (cata.direccioBodega.isNotEmpty()) {
                Text(
                    text = "🏠 Adreça: ${cata.direccioBodega}", 
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Data
            val formattedDate = try {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cata.data)
            } catch (e: Exception) {
                "Data desconeguda"
            }
            Text(
                text = "📅 Data: $formattedDate", 
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
