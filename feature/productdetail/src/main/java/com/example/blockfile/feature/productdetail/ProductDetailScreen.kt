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
import com.example.blockfile.core.ui.theme.Warning

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
    val downloadState = viewModel.downloadState
    val commentState = viewModel.commentUiState
    val ratingState = viewModel.ratingUiState
    val purchaseState = viewModel.purchaseUiState
    val context = LocalContext.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,   // 🔹 Fondo oscuro global
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        state.detail?.detail?.nombre ?: "Detalle del producto",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,          // barra oscura
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.primary   // links tipo “Inicio, Ranking…”
                ),
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
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
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
                        Button(
                            onClick = { viewModel.load() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            else -> {
                val detail = state.detail!!
                Box(modifier = Modifier.padding(padding)) {

                    ProductDetailContent(
                        detail = detail,
                        downloadState = downloadState,
                        purchaseState = purchaseState,
                        onDownloadTtl = {
                            val intent =
                                Intent(Intent.ACTION_VIEW, Uri.parse(detail.detail.urlTtl))
                            context.startActivity(intent)
                        },
                        onDownloadProducto = {
                            viewModel.downloadProduct()
                        },
                        onBuyClick = { viewModel.buyProduct() },
                        onRateClick = { viewModel.openRatingDialog() },
                        onCommentClick = { viewModel.openCommentDialog() },
                    )

                    if (commentState.showDialog) {
                        CommentDialog(
                            state = commentState,
                            onTextChange = viewModel::onCommentTextChange,
                            onDismiss = {
                                if (!commentState.sending) {
                                    viewModel.dismissCommentDialog()
                                }
                            },
                            onSend = {
                                if (!commentState.sending) {
                                    viewModel.sendComment()
                                }
                            }
                        )
                    }

                    if (ratingState.showDialog) {
                        RatingDialog(
                            state = ratingState,
                            onRatingSelected = viewModel::onRatingSelected,
                            onDismiss = {
                                if (!ratingState.sending) {
                                    viewModel.dismissRatingDialog()
                                }
                            },
                            onSend = {
                                if (!ratingState.sending) {
                                    viewModel.sendRating()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductDetailContent(
    modifier: Modifier = Modifier,
    detail: ProductDetailResult,
    downloadState: ProductDetailDownloadState,
    purchaseState: PurchaseUiState,
    onDownloadTtl: () -> Unit,
    onDownloadProducto: () -> Unit,
    onBuyClick: () -> Unit,
    onRateClick: () -> Unit,
    onCommentClick: () -> Unit,
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
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface    // 🔹 card oscuro como la web
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = p.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

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
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }

        // ===== Card: Precio + saldo + Comprar / Ya comprado =====
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Precio:",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = p.precio?.let { "S/ %.2f".format(it) } ?: "-",
                        color = MaterialTheme.colorScheme.primary    // 🔹 como acento
                    )
                }

                p.saldoCliente?.let { saldo ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Tu saldo:",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "S/ %.2f".format(saldo),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (!p.mostrarAcciones) {
                    Button(
                        onClick = onBuyClick,
                        enabled = !purchaseState.buying,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            if (purchaseState.buying) "Comprando..."
                            else "Comprar"
                        )
                    }
                } else {
                    Text(
                        text = "Ya compraste este producto.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,   // 🔹 mensaje en color acento
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }

                purchaseState.error?.let { err ->
                    Text(
                        text = err,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }

                purchaseState.success?.let { msg ->
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }

        // ===== Card: rating + metadatos + RDF / Descargar =====
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
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

                Divider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

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
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Descargar RDF (.ttl)")
                    }

                }


            }
        }

        // ===== Card de acciones: Calificar / Comentar (solo si ya compró) =====
        if (p.mostrarAcciones) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Acciones",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    OutlinedButton(
                        onClick = onDownloadProducto,
                        enabled = !downloadState.downloading,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            if (downloadState.downloading)
                                "Descargando..."
                            else
                                "Descargar"
                        )
                    }

                    OutlinedButton(
                        onClick = onRateClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Calificar")
                    }
                    OutlinedButton(
                        onClick = onCommentClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Comentar")
                    }

                    downloadState.error?.let { err ->
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }

                    downloadState.lastDownloadedFile?.let { file ->
                        Text(
                            text = "Archivo descargado en:\n${file.absolutePath}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            }
        }

        // ===== Comentarios =====
        Text(
            text = "Comentarios",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
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
private fun CommentDialog(
    state: CommentUiState,
    onTextChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSend: () -> Unit,
) {
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.surface,   // modal oscuro
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Nuevo comentario",
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.text,
                    onValueChange = onTextChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Escribe tu comentario") },
                    minLines = 3,
                    maxLines = 5,
                )
                state.error?.let { err ->
                    Text(
                        text = err,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                state.success?.let { msg ->
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSend, enabled = !state.sending) {
                if (state.sending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text("Enviar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !state.sending) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun RatingDialog(
    state: RatingUiState,
    onRatingSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
    onSend: () -> Unit,
) {
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.surface,
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Calificar producto",
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    (1..5).forEach { star ->
                        val filled = star <= state.selectedRating
                        Text(
                            text = if (filled) "★" else "☆",
                            style = MaterialTheme.typography.headlineMedium,
                            color = if (filled)
                                Warning
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clickable { onRatingSelected(star) }
                        )
                    }
                }

                state.error?.let { err ->
                    Text(
                        text = err,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                state.success?.let { msg ->
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSend, enabled = !state.sending) {
                if (state.sending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text("Calificar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !state.sending) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "$label:",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            value,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
        Text(
            "★".repeat(full) + "☆".repeat(empty),
            color = Warning
        )
    }
}

@Composable
private fun CommentCard(comment: ProductComment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Column(
                modifier = Modifier.padding(0.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        comment.cliente,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    RatingStars(rating = comment.calificacion.toDouble())
                }
                Text(
                    text = comment.fecha ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                comment.descripcion,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
