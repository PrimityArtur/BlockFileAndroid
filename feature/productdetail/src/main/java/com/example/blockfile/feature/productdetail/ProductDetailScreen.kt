package com.example.blockfile.feature.productdetail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.blockfile.core.data.repository.ProductDetailResult
import com.example.blockfile.core.model.ProductComment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailViewModel,
    onGoHome: () -> Unit,
    onGoRanking: () -> Unit,
    onGoPerfil: () -> Unit,
    onLogout: () -> Unit,
) {
    val state = viewModel.uiState
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.detail?.detail?.nombre ?: "Detalle del producto") },
                actions = {
                    TextButton(onClick = onGoHome) { Text("Inicio") }
                    TextButton(onClick = onGoRanking) { Text("Ranking") }
                    TextButton(onClick = onGoPerfil) { Text("Perfil") }
                    TextButton(onClick = onLogout) { Text("Cerrar sesión") }
                }
            )
        }
    ) { padding ->
        when {
            state.loading -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.error ?: "Error",
                            color = MaterialTheme.colorScheme.error,
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { viewModel.load() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            else -> {
                val detail = state.detail!!
                ProductDetailContent(
                    modifier = Modifier.padding(padding),
                    detail = detail,
                    onDownloadTtl = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(detail.detail.urlTtl))
                        context.startActivity(intent)
                    },
                    onDownloadProducto = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(detail.detail.urlDescargar))
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
private fun ProductDetailContent(
    modifier: Modifier = Modifier,
    detail: ProductDetailResult,
    onDownloadTtl: () -> Unit,
    onDownloadProducto: () -> Unit,
) {
    val p = detail.detail
    val comments = detail.comments

    var selectedImage by remember {
        mutableStateOf(p.imagenUrls.firstOrNull())
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ===== Card principal: imagen + descripción =====
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = p.nombre, style = MaterialTheme.typography.titleLarge)

                if (selectedImage != null) {
                    AsyncImage(
                        model = selectedImage,
                        contentDescription = "Imagen de ${p.nombre}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Text(
                        text = "Sin imágenes disponibles.",
                        modifier = Modifier.padding(vertical = 16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                if (p.imagenUrls.size > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        p.imagenUrls.forEach { url ->
                            Card(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clickable { selectedImage = url },
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                AsyncImage(
                                    model = url,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                )
                            }
                        }
                    }
                }

                Text(
                    text = p.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }

        // ===== Card: Precio + saldo + Comprar / Ya comprado =====
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Precio:", fontWeight = FontWeight.Bold)
                    Text(
                        text = p.precio?.let { "S/ %.2f".format(it) } ?: "-",
                    )
                }

                p.saldoCliente?.let { saldo ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tu saldo:", fontWeight = FontWeight.Bold)
                        Text("S/ %.2f".format(saldo))
                    }
                }

                if (!p.mostrarAcciones) {
                    Button(
                        onClick = { /* TODO: Comprar producto vía API */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                    ) {
                        Text("Comprar")
                    }
                } else {
                    Text(
                        text = "Ya compraste este producto.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }

        // ===== Card: rating + metadatos + RDF / Descargar =====
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    RatingStars(rating = p.calificacionPromedio)
                    Text(
                        text = "${p.compras} compras",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 4.dp))

                InfoRow("Autor", p.autor)
                InfoRow("Versión", p.version)
                InfoRow("Categoría", p.categoria)
                InfoRow("Fecha", p.fechaPublicacion ?: "-")

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDownloadTtl,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Descargar RDF (.ttl)")
                    }

                    if (p.mostrarAcciones) {
                        OutlinedButton(
                            onClick = onDownloadProducto,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Descargar producto")
                        }
                    }
                }
            }
        }

        // ===== Card de acciones: Calificar / Comentar (solo si ya compró) =====
        if (p.mostrarAcciones) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Acciones", fontWeight = FontWeight.Bold)
                    OutlinedButton(
                        onClick = { /* TODO: pantalla Calificar */ },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Calificar")
                    }
                    OutlinedButton(
                        onClick = { /* TODO: pantalla Comentar */ },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Comentar")
                    }
                }
            }
        }

        // ===== Comentarios =====
        Text(
            text = "Comentarios",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp),
        )

        if (comments.isEmpty()) {
            Text(
                text = "No hay comentarios para este producto.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                comments.forEach { c ->
                    CommentCard(c)
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("$label:", fontWeight = FontWeight.Bold)
        Text(value)
    }
}

@Composable
private fun RatingStars(rating: Double) {
    val full = rating.toInt().coerceIn(0, 5)
    val empty = 5 - full
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text("★".repeat(full) + "☆".repeat(empty))
        Text("(${String.format("%.1f", rating)})")
    }
}

@Composable
private fun CommentCard(comment: ProductComment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(comment.cliente, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    RatingStars(rating = comment.calificacion.toDouble())
                }
                Text(
                    text = comment.fecha ?: "",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Text(comment.descripcion)
        }
    }
}

