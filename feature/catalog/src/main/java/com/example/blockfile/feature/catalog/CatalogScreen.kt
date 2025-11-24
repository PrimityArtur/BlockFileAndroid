package com.example.blockfile.feature.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.blockfile.core.model.ProductoCatalogo
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: CatalogViewModel,
    onGoHome: () -> Unit,
    onGoRanking: () -> Unit,
    onGoPerfil: () -> Unit,
    onLogout: () -> Unit,
) {
    val state = viewModel.uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ===== HEADER ARRIBA TIPO "HeaderCliente.html" =====
        TopAppBar(
            title = {
                Text(
                    text = "BlockFile",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            },
            actions = {
                TextButton(onClick = onGoHome) {
                    Text("Inicio")
                }
                TextButton(onClick = onGoRanking) {
                    Text("Ranking")
                }
                TextButton(onClick = onGoPerfil) {
                    Text("Perfil")
                }
                TextButton(onClick = onLogout) {
                    Text("Cerrar sesión")
                }
            }
        )

        // ===== FILTROS =====
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.nombre,
                    onValueChange = viewModel::onNombreChange,
                    label = { Text("Nombre") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = state.autor,
                    onValueChange = viewModel::onAutorChange,
                    label = { Text("Autor") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = state.categoria,
                onValueChange = viewModel::onCategoriaChange,
                label = { Text("Categoría") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = { viewModel.buscar() },
                enabled = !state.loading,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Buscar")
            }
        }

        if (state.error != null) {
            Text(
                text = state.error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
        }

        // ===== LISTA VERTICAL =====
        Box(modifier = Modifier.weight(1f)) {
            if (state.loading && state.items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.items) { producto ->
                        CatalogItemCard(producto = producto)
                    }
                }
            }
        }

        // ===== PAGINADOR ABAJO =====
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Página ${state.page} de ${state.totalPages}")

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { viewModel.irPagina(state.page - 1) },
                    enabled = state.page > 1 && !state.loading
                ) { Text("Anterior") }

                OutlinedButton(
                    onClick = { viewModel.irPagina(state.page + 1) },
                    enabled = state.page < state.totalPages && !state.loading
                ) { Text("Siguiente") }
            }
        }
    }
}

@Composable
fun CatalogItemCard(producto: ProductoCatalogo) {
    // Construimos la URL de la imagen solo si hay imagenId
    val imageUrl = producto.imagenId?.let {
        "https://blockfile.up.railway.app/apimovil/catalogo/imagen/$it/"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ===== IMAGEN A LA IZQUIERDA =====
            if (imageUrl != null) {
                coil.compose.AsyncImage(
                    model = imageUrl,
                    contentDescription = "Imagen de ${producto.nombre}",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // ===== TEXTO A LA DERECHA =====
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Autor: ${producto.autor}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "Precio: S/ ${"%.2f".format(producto.precio)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "Compras: ${producto.compras}",
                    style = MaterialTheme.typography.bodySmall
                )
                producto.calificacionPromedio?.let {
                    Text(
                        "Calificación: ${"%.1f".format(it)} ★",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}