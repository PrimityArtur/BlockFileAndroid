package com.example.blockfile.feature.adminusers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.blockfile.core.model.AdminUserItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(
    viewModel: AdminUsersViewModel,
    onGoPerfil: () -> Unit,
    onGoInventario: () -> Unit,
    onGoCategorias: () -> Unit,
    onGoUsuarios: () -> Unit,
) {
    val state = viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel administrador") },
                actions = {
                    TextButton(onClick = onGoPerfil) { Text("Perfil") }
                    TextButton(onClick = onGoInventario) { Text("Inventario") }
                    TextButton(onClick = onGoCategorias) { Text("Categorías") }
                    TextButton(onClick = { /* ya estamos en usuarios */ }, enabled = false) {
                        Text("Usuarios")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {

                Text(
                    text = "Gestión de usuarios",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    AdminUsersTableWithFiltersSection(
                        loading = state.loading,
                        error = state.error,
                        items = state.items,
                        idValue = state.id,
                        nombreValue = state.nombre,
                        saldoValue = state.saldo,
                        onIdChange = viewModel::onIdChange,
                        onNombreChange = viewModel::onNombreChange,
                        onSaldoChange = viewModel::onSaldoChange,
                        onBuscar = { viewModel.buscarPrimeraPagina() },
                        onEditClick = { item -> viewModel.onEditClick(item) },
                    )
                }

                PagerControls(
                    page = state.page,
                    totalPages = state.totalPages,
                    onPageChange = { viewModel.irPagina(it) }
                )
            }

            // Modal edición
            if (state.showEditDialog) {
                AdminUserEditDialog(
                    nombre = state.formNombre,
                    correo = state.formCorreo,
                    fecha = state.formFecha,
                    id = state.formId?.toString() ?: "",
                    saldo = state.formSaldo,
                    loading = state.loading,
                    onSaldoChange = viewModel::onFormSaldoChange,
                    onDismiss = viewModel::onDismissEditDialog,
                    onSubmit = viewModel::onSubmitForm,
                    onDelete = viewModel::onDeleteClickFromDialog,
                )
            }

            // Confirmación borrado
            if (state.showDeleteDialog) {
                ConfirmDeleteUserDialog(
                    nombreUsuario = state.formNombre,
                    loading = state.loading,
                    onDismiss = viewModel::onDismissDeleteDialog,
                    onConfirm = viewModel::onConfirmDelete,
                )
            }
        }
    }
}

/* ----------------------------- TABLA + FILTROS ----------------------------- */

@Composable
private fun AdminUsersTableWithFiltersSection(
    loading: Boolean,
    error: String?,
    items: List<AdminUserItem>,
    idValue: String,
    nombreValue: String,
    saldoValue: String,
    onIdChange: (String) -> Unit,
    onNombreChange: (String) -> Unit,
    onSaldoChange: (String) -> Unit,
    onBuscar: () -> Unit,
    onEditClick: (AdminUserItem) -> Unit,
) {
    if (loading) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }

    error?.let {
        Text(
            it,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(8.dp)
        )
    }

    val weights = listOf(0.6f, 2.0f, 1.2f, 1.2f) // ID / Nombre / Saldo / Acciones

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Filtros",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = idValue,
                            onValueChange = onIdChange,
                            label = { Text("ID") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = nombreValue,
                            onValueChange = onNombreChange,
                            label = { Text("Nombre") },
                            modifier = Modifier.weight(2f),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = saldoValue,
                        onValueChange = onSaldoChange,
                        label = { Text("Saldo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = onBuscar,
                            enabled = !loading
                        ) {
                            Text("Buscar")
                        }
                    }
                }
            }
        }

        item {
            TableHeader(
                headers = listOf("ID", "Nombre", "Saldo", "Acciones"),
                weights = weights
            )
            Divider()
        }

        items(items) { item ->
            Surface(modifier = Modifier.fillMaxWidth()) {
                AdminUserTableRow(
                    item = item,
                    weights = weights,
                    onEditClick = { onEditClick(item) }
                )
            }
            Divider()
        }

        if (items.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sin resultados")
                }
            }
        }
    }
}

@Composable
private fun TableHeader(
    headers: List<String>,
    weights: List<Float>,
) {
    Surface(
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            headers.forEachIndexed { index, title ->
                Text(
                    text = title,
                    modifier = Modifier
                        .weight(weights[index])
                        .padding(horizontal = 6.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun AdminUserTableRow(
    item: AdminUserItem,
    weights: List<Float>,
    onEditClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.id.toString(),
            modifier = Modifier
                .weight(weights[0])
                .padding(horizontal = 6.dp),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = item.nombre,
            modifier = Modifier
                .weight(weights[1])
                .padding(horizontal = 6.dp),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            softWrap = true,
            maxLines = Int.MAX_VALUE
        )
        Text(
            text = item.saldo,
            modifier = Modifier
                .weight(weights[2])
                .padding(horizontal = 6.dp),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Box(
            modifier = Modifier
                .weight(weights[3])
                .padding(horizontal = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            OutlinedButton(onClick = onEditClick) {
                Text("+")
            }
        }
    }
}

/* --------------------------- MODAL EDITAR --------------------------- */

@Composable
private fun AdminUserEditDialog(
    nombre: String,
    correo: String,
    fecha: String,
    id: String,
    saldo: String,
    loading: Boolean,
    onSaldoChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
    onDelete: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Editar usuario")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Campos no modificables
                Text(
                    text = "Campos no modificables",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Fecha de creación: $fecha")
                    Text("Nombre de usuario: $nombre")
                    Text("Correo: ${if (correo.isNotBlank()) correo else "—"}")
                    Text("ID: $id")
                }

                Divider()

                Text(
                    text = "Campos editables",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = saldo,
                    onValueChange = onSaldoChange,
                    label = { Text("Saldo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TextButton(
                    onClick = onDelete,
                    enabled = !loading
                ) {
                    Text("Borrar")
                }
                TextButton(
                    onClick = onSubmit,
                    enabled = !loading
                ) {
                    Text("Aceptar")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !loading
            ) {
                Text("Cerrar")
            }
        }
    )
}

/* ---------------------- CONFIRMAR ELIMINAR ---------------------- */

@Composable
private fun ConfirmDeleteUserDialog(
    nombreUsuario: String,
    loading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eliminar usuario") },
        text = {
            Text("¿Eliminar al usuario \"$nombreUsuario\"?")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !loading
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !loading
            ) {
                Text("Cancelar")
            }
        }
    )
}

/* ----------------------------- PAGINADOR ----------------------------- */

@Composable
private fun PagerControls(
    page: Int,
    totalPages: Int,
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
            enabled = page > 1,
        ) { Text("« Primero") }

        TextButton(
            onClick = { onPageChange(page - 1) },
            enabled = page > 1,
        ) { Text("‹ Ant") }

        Text("Página $page de $totalPages")

        TextButton(
            onClick = { onPageChange(page + 1) },
            enabled = page < totalPages,
        ) { Text("Sig ›") }

        TextButton(
            onClick = { onPageChange(totalPages) },
            enabled = page < totalPages,
        ) { Text("Último »") }
    }
}
