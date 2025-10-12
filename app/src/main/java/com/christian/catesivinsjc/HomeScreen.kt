package com.christian.catesivinsjc

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.christian.catesivinsjc.model.Bodega
import com.christian.catesivinsjc.viewmodel.BodegaViewModel

@Composable
//Aço es lexemple de funcio per a saber si funciona
/*fun HomeScreen() {
    Text("Això és el Home")
    }*/
fun BodegasListScreen(bodegaViewModel: BodegaViewModel = viewModel()) {

    // 1. Llançar la càrrega de dades només una vegada quan el Composable es mostra
    LaunchedEffect(Unit) {
        bodegaViewModel.getBodegues()
    }

    // 2. Observar el StateFlow del ViewModel.
    // La UI es recomposarà automàticament quan les dades canviïn.
    val bodegasList by bodegaViewModel.bodegues.collectAsState()

    // 3. Mostrar les dades en una llista
    LazyColumn {
        items(bodegasList) { bodega ->
            BodegaItem(bodega)
        }
    }
}

@Composable
fun BodegaItem(bodega: Bodega) {
    Column {
        Text(text = "Nom: ${bodega.nom}")
        Text(text = "Localitat: ${bodega.localitat}")
    }
}