package com.example.blockfile.feature.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.blockfile.core.ui.theme.TextMuted
import com.example.blockfile.core.ui.theme.Warning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: CatalogViewModel,
    onGoHome: () -> Unit,
    onGoRanking: () -> Unit,
    onGoPerfil: () -> Unit,
    onLogout: () -> Unit,
    onProductClick: (Long) -> Unit,
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
                    items(state.items) { product ->
                        CatalogItemCard(
                            producto = product,
                            onClick = { onProductClick(product.id) }
                        )
                    }
                }
            }
        }

        // ===== PAGINADOR ABAJO =====
        PagerControls(
            page = state.page,
            totalPages = state.totalPages,
            loading = state.loading,
            onPageChange = { viewModel.irPagina(it) }
        )
    }
}
@Composable
fun CatalogItemCard(
    producto: ProductoCatalogo,
    onClick: () -> Unit,
) {
    val imageUrl = producto.imagenId?.let {
        "https://blockfile.up.railway.app/apimovil/catalogo/imagen/$it/"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ===== IMAGEN ARRIBA (figure) =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Imagen de ${producto.nombre}",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sin imagen",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ===== BODY=====
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // ---- Fila 1: título + rating  ----
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = producto.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(2f)
                    )

                    RatingStarsCompact(
                        rating = producto.calificacionPromedio ?: 0.0
                    )
                }

                // ---- Fila 2: Por: autor (catalog-card__desc) ----
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Por: ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = producto.autor.ifBlank { "-" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // ---- Fila 3: Precio + compras (catalog-card__row meta) ----
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = buildString {
                            append("Precio: ")
                            append(
                                if (producto.precio != null)
                                    "S/ ${"%.2f".format(producto.precio)}"
                                else
                                    "-"
                            )
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,

//                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "${producto.compras} compras",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,

//                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun RatingStarsCompact(rating: Double) {
    val safeRating = rating.coerceIn(0.0, 5.0)
    val full = safeRating.toInt()
    val hasHalf = (safeRating - full) >= 0.5 && full < 5
    val empty = 5 - full - if (hasHalf) 1 else 0

    val stars = buildString {
        append("★".repeat(full))
        if (hasHalf) append("★")
        append("☆".repeat(empty))
    }

    Text(
        text = stars,
        style = MaterialTheme.typography.bodySmall,
        color = Warning
    )
}

@Composable
private fun PagerControls(
    page: Int,
    totalPages: Int,
    loading: Boolean,
    onPageChange: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(
            onClick = { onPageChange(1) },
            enabled = page > 1 && !loading,
        ) {
            Text("« Primero")
        }

        TextButton(
            onClick = { onPageChange(page - 1) },
            enabled = page > 1 && !loading,
        ) {
            Text("‹ Ant")
        }

        Text("Página $page de $totalPages", color = MaterialTheme.colorScheme.onSurface)

        TextButton(
            onClick = { onPageChange(page + 1) },
            enabled = page < totalPages && !loading,
        ) {
            Text("Sig ›")
        }

        TextButton(
            onClick = { onPageChange(totalPages) },
            enabled = page < totalPages && !loading,
        ) {
            Text("Último »")
        }
    }
}
