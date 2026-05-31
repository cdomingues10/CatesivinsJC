package com.christian.catesivinsjc

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.ui.text.style.TextOverflow
import com.christian.catesivinsjc.model.ValoracionsCata
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CataDetailScreen(
    cataId: String,
    navController: NavController,
    viewModel: CataDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    
    // Carreguem les dades quan canvia el cataId
    LaunchedEffect(cataId) {
        viewModel.loadCataDetails(cataId)
    }
    val cata by viewModel.cata.collectAsState()
    val bodega by viewModel.bodega.collectAsState()
    val vins by viewModel.vins.collectAsState()
    val valoracions by viewModel.valoracions.collectAsState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val laMeuaValoracio = valoracions.find { it.userId == currentUserId }
    var puntuacio by remember(laMeuaValoracio) { mutableIntStateOf(laMeuaValoracio?.puntuacioGeneral ?: 0) }
    var comentari by remember(laMeuaValoracio) { mutableStateOf(laMeuaValoracio?.comentariGeneral ?: "") }
    val mainColor = colorResource(id = R.color.copa)

    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = { 
                    Text(
                        text = bodega?.Nom ?: cata?.nom ?: stringResource(R.string.detalls_cata),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = stringResource(R.string.desc_back), 
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
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
        ) {
            
            bodega?.let { currentBodega ->
                if (currentBodega.Imatgebod.isNotEmpty()) {
                    AsyncImage(
                        model = currentBodega.Imatgebod,
                        contentDescription = stringResource(R.string.desc_imatge_bodega, currentBodega.Nom),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                cata?.let { currentCata ->
                    Text(
                        text = stringResource(R.string.titol_experiencia),
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
                            // Dades de la cata: Data i Descripció
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarToday, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                val dateFormatted = try {
                                    SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(currentCata.data)
                                } catch (e: Exception) { stringResource(R.string.data_desconeguda) }
                                Text(text = dateFormatted, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = currentCata.descripcio, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                            
                            // Llista del que inclou l'experiència
                            if (currentCata.inclou.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = stringResource(R.string.titol_que_inclou),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.DarkGray,
                                    fontWeight = FontWeight.SemiBold
                                )

                                currentCata.inclou.forEach { item ->
                                    Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(top = 4.dp)) {
                                        Icon(
                                            imageVector = Icons.Default.WineBar,
                                            contentDescription = null,
                                            tint = mainColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = item,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.DarkGray
                                        )
                                    }
                                }
                            }

                            // BOTÓ DE RESERVA (Ràpid i senzill)
                            Button(
                                onClick = { 
                                    viewModel.reservarCata()
                                    Toast.makeText(context, context.getString(R.string.reserva_ok), Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 24.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = mainColor),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.ConfirmationNumber, null)
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(R.string.boto_reservar))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                bodega?.let { currentBodega ->
                    Text(
                        text = stringResource(R.string.titol_ubicacio),
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
                                Icon(Icons.Default.Storefront, null, tint = mainColor, modifier = Modifier.size(24.dp))
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
                    text = stringResource(R.string.titol_seleccio_vins),
                    style = MaterialTheme.typography.headlineSmall,
                    color = mainColor,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Mostrar la selecció de vins si n'hi ha
                if (vins.isNotEmpty()) {
                    vins.forEach { vi ->
                        ViCard(vi = vi, mainColor = mainColor)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Secció per a que l'usuari puga valorar (si no ho ha fet ja)
                if(laMeuaValoracio == null) {
                    Text(
                        text = stringResource(R.string.opinio_usuari_pregunta),
                        style = MaterialTheme.typography.titleMedium,
                        color = mainColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = mainColor.copy(alpha = 0.05f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            RatingCopes(puntuacio = puntuacio) { novaNota -> puntuacio = novaNota }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = comentari,
                                onValueChange = { comentari = it },
                                placeholder = {
                                    Text(
                                        stringResource(R.string.placeholder_comentari),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3,
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = mainColor)
                            )
                            Button(
                                onClick = { viewModel.guardarOActualitzarValoracio(puntuacio, comentari) },
                                enabled = puntuacio > 0,
                                modifier = Modifier.align(Alignment.End).padding(top = 12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = mainColor)
                            ) {
                                Text(stringResource(R.string.boto_publicar))
                            }
                        }
                    }
                }

                Text(
                    text = stringResource(R.string.titol_opinions),
                    style = MaterialTheme.typography.titleMedium,
                    color = mainColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Llistat d'opinions d'altres tastadors
                if (valoracions.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        items(valoracions) { valo ->
                            ValoracioItem(valoracio = valo, mainColor = mainColor)
                        }
                    }
                } else {
                    Text(
                        text = stringResource(R.string.sense_opinions),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ViCard(vi: Vi, mainColor: Color) {
    // Targeta visual per a cada vi
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
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icona i dades del vi (Nom, Tipus, Any)
            Box(
                modifier = Modifier.size(90.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(45.dp).offset(y = 40.dp)) {
                    drawOval(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.15f), Color.Transparent),
                            center = center,
                            radius = size.width / 2
                        )
                    )
                }
                if (vi.Imatgeurl.isNotEmpty()) {
                    AsyncImage(
                        model = vi.Imatgeurl,
                        contentDescription = stringResource(R.string.desc_imatge_vi, vi.Nom),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Icon(Icons.Default.LocalBar, null, tint = Color.LightGray, modifier = Modifier.size(45.dp))
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(vi.Nom, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, color = Color(0xFF2C2C2C))
                Text("${vi.Tipus} • ${vi.Any}", style = MaterialTheme.typography.bodyMedium, color = mainColor, fontWeight = FontWeight.Medium)
                if (vi.Descripcio.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(vi.Descripcio, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 2)
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

@Composable
fun ValoracioItem(valoracio: ValoracionsCata, mainColor: Color) {
    Card(
        modifier = Modifier.width(320.dp).padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccountCircle, null, tint = mainColor.copy(alpha = 0.5f), modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(valoracio.nomUsuari, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                Row {
                    repeat(5) { index ->
                        Icon(Icons.Default.WineBar, null, tint = if (index < valoracio.puntuacioGeneral) colorResource(id = R.color.copa) else Color.LightGray, modifier = Modifier.size(16.dp))
                    }
                }
            }
            if (valoracio.comentariGeneral.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(valoracio.comentariGeneral, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray, maxLines = 3)
            }
        }
    }
}

@Composable
fun RatingCopes(puntuacio: Int, onPuntuacioChange: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        repeat(5) { index ->
            val activa = index < puntuacio
            IconButton(onClick = { onPuntuacioChange(index + 1) }) {
                Icon(Icons.Default.WineBar, null, tint = if (activa) colorResource(id = R.color.copa) else Color.LightGray, modifier = Modifier.size(32.dp))
            }
        }
    }
}
