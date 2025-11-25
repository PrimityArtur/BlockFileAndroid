package com.example.blockfile.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.blockfile.core.model.CompraPerfil
import com.example.blockfile.core.ui.theme.TextMuted
import com.example.blockfile.core.ui.theme.Warning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onGoHome: () -> Unit,
    onGoRankings: () -> Unit,
    onLogout: () -> Unit,
    onProductClick: (Long) -> Unit,
) {
    val state = viewModel.uiState
    val compras = viewModel.comprasState

    LaunchedEffect(Unit) {
        viewModel.loadInitial()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // ===== HEADER ARRIBA (igual estilo que CatalogScreen) =====
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
                TextButton(onClick = onGoRankings) {
                    Text("Ranking")
                }
                TextButton(onClick = { /* ya estás en perfil */ }) {
                    Text("Perfil")
                }
                TextButton(onClick = onLogout) {
                    Text("Cerrar sesión")
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                // "Perfil del Cliente" (h1 en la web)
                Text(
                    text = "Perfil del Cliente",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (state.loading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                // Bloque de errores tipo card como en la web
                state.error?.let { err ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Errores:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = err,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // Formulario de perfil (similar al formPerfil de la web)
                PerfilFormCard(
                    state = state,
                    onNombreChange = viewModel::onNombreChange,
                    onCorreoChange = viewModel::onCorreoChange,
                    onContrasenaChange = viewModel::onContrasenaChange,
                    onGuardar = viewModel::guardarCambios,
                )

                Divider()

                // Sección "Tus compras" + tarjetas + paginador
                ComprasSection(
                    comprasState = compras,
                    onPageChange = viewModel::loadCompras,
                    onProductClick = onProductClick,
                )
            }
        }
    }
}

/**
 * Card que combina:
 * - nombre_usuario, correo, contraseña (editables)
 * - saldo, num_compras (solo lectura)
 * - botones Editar / Guardar / Cancelar
 * Reproduce la lógica del HTML: modo lectura <-> modo edición.
 */
@Composable
private fun PerfilFormCard(
    state: ProfileUiState,
    onNombreChange: (String) -> Unit,
    onCorreoChange: (String) -> Unit,
    onContrasenaChange: (String) -> Unit,
    onGuardar: () -> Unit,
) {
    val perfil = state.perfil ?: return

    var isEditing by remember { mutableStateOf(false) }

    // Valores "originales" para poder restaurar al cancelar
    var originalNombre by remember { mutableStateOf(perfil.nombreUsuario) }
    var originalCorreo by remember { mutableStateOf(perfil.correo) }
    var originalContrasena by remember { mutableStateOf("") }

    // Cuando se carga/actualiza el perfil o se guarda con éxito,
    // sincronizamos originales y salimos de edición (igual que el js de la web).
    LaunchedEffect(perfil.idUsuario, state.saveSuccess) {
        originalNombre = perfil.nombreUsuario
        originalCorreo = perfil.correo
        originalContrasena = ""  // no mostramos la contraseña real
        if (state.saveSuccess) {
            isEditing = false
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            OutlinedTextField(
                value = state.nombre,
                onValueChange = { if (isEditing) onNombreChange(it) },
                label = { Text("Nombre de usuario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditing && !state.saving,
            )

            OutlinedTextField(
                value = state.correo,
                onValueChange = { if (isEditing) onCorreoChange(it) },
                label = { Text("Correo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditing && !state.saving,
            )

            OutlinedTextField(
                value = state.contrasena,
                onValueChange = { if (isEditing) onContrasenaChange(it) },
                label = { Text("Contraseña nueva (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditing && !state.saving,
            )

            OutlinedTextField(
                value = "S/ ${perfil.saldo}",
                onValueChange = {},
                label = { Text("Saldo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            )

            OutlinedTextField(
                value = perfil.numCompras.toString(),
                onValueChange = {},
                label = { Text("N.º de compras") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            )

            state.saveError?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
            if (state.saveSuccess) {
                Text(
                    "Perfil actualizado correctamente.",
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (!isEditing) {
                    Button(
                        onClick = {
                            isEditing = true
                            originalNombre = state.nombre
                            originalCorreo = state.correo
                            originalContrasena = state.contrasena
                        },
                        enabled = !state.saving
                    ) {
                        Text("Editar")
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { onGuardar() },
                            enabled = !state.saving
                        ) {
                            if (state.saving) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .padding(end = 4.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                            Text("Guardar")
                        }

                        OutlinedButton(
                            onClick = {
                                onNombreChange(originalNombre)
                                onCorreoChange(originalCorreo)
                                onContrasenaChange(originalContrasena)
                                isEditing = false
                            },
                            enabled = !state.saving
                        ) {
                            Text("Cancelar")
                        }
                    }
                }
            }
        }
    }
}

/* ============================================================
 *     SECCIÓN "TUS COMPRAS" – TARJETAS TIPO CATÁLOGO
 * ============================================================ */

@Composable
private fun ComprasSection(
    comprasState: ProfileComprasState,
    onPageChange: (Int) -> Unit,
    onProductClick: (Long) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            "Tus compras",
            modifier = Modifier.padding(bottom = 4.dp),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (comprasState.loading && comprasState.items.isEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            comprasState.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            // Ya NO usamos LazyColumn aquí, dejamos que scrollee el LazyColumn exterior
            comprasState.items.forEach { item ->
                CompraItemCard(
                    item = item,
                    onClick = { onProductClick(item.id) }
                )
            }
        }

        // Paginador (inspirado en el pager de la web)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Página ${comprasState.page} de ${comprasState.totalPages}")

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { onPageChange(comprasState.page - 1) },
                    enabled = comprasState.page > 1 && !comprasState.loading
                ) { Text("Anterior") }

                OutlinedButton(
                    onClick = { onPageChange(comprasState.page + 1) },
                    enabled = comprasState.page < comprasState.totalPages && !comprasState.loading
                ) { Text("Siguiente") }
            }
        }
    }
}

@Composable
fun CompraItemCard(
    item: CompraPerfil,
    onClick: () -> Unit,
) {
    val imageUrl = item.imagenId?.let {
        "https://blockfile.up.railway.app/apimovil/catalogo/imagen/$it/"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
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

            // ===== IMAGEN (figure) =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Imagen de ${item.nombre}",
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

            // ===== BODY (catalog-card__body) =====
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Título + rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(2f)
                    )

                    RatingStarsCompact(
                        rating = item.calificacionPromedio ?: 0.0
                    )
                }

                // "Por: autor"
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Por: ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = item.autor.ifBlank { "-" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Precio + compras
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = buildString {
                            append("Precio: ")
                            append(
                                if (item.precio != null)
                                    "S/ ${"%.2f".format(item.precio)}"
                                else
                                    "-"
                            )
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )

                    Text(
                        text = "${item.compras} compras",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
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
