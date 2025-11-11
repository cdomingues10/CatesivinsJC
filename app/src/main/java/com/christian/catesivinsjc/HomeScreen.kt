package com.christian.catesivinsjc

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.christian.catesivinsjc.model.Cata
import com.christian.catesivinsjc.viewmodel.CataViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CatesListScreen(cataViewModel: CataViewModel = viewModel()) {

    // 1. Llançar la càrrega de dades només una vegada quan el Composable es mostra
    LaunchedEffect(Unit) {
        cataViewModel.getCates()
    }

    // 2. Observar el StateFlow del ViewModel.
    // La UI es recomposarà automàticament quan les dades canviïn.
    val catesList by cataViewModel.cates.collectAsState()

    // 3. Mostrar les dades en una llista
    LazyColumn {
        items(catesList) { cata ->
            CataItem(cata)
        }
    }
}

@Composable
fun CataItem(cata: Cata) {
    Column {
        Text(text = "Nom: ${cata.nom}")
        Text(text = "Descripcio: ${cata.descripcio}")
        Text(text = "Ubicacio: ${cata.ubicacio}")
        // Donem format a la data per a una millor llegibilitat
        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cata.data)
        Text(text = "Data: $formattedDate")
    }
}
