package com.example.blockfile.feature.admincategories

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
import com.example.blockfile.core.model.AdminCategoryItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCategoriesScreen(
    viewModel: AdminCategoriesViewModel,
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
                    // Estamos en Categorías, lo dejamos deshabilitado
                    TextButton(onClick = { /* ya estás en Categorías */ }, enabled = false) {
                        Text("Categorías")
                    }
                    TextButton(onClick = onGoUsuarios) { Text("Usuarios") }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {

                // Título + botón Agregar (sin funcionalidad por ahora)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Gestión de categorías",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = { viewModel.onAddClick() },
                        enabled = !state.loading
                    ) {
                        Text("Agregar")
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    AdminCategoriesTableWithFiltersSection(
                        loading = state.loading,
                        error = state.error,
                        items = state.items,
                        idValue = state.id,
                        nombreValue = state.nombre,
                        descripcionValue = state.descripcion,
                        onIdChange = viewModel::onIdChange,
                        onNombreChange = viewModel::onNombreChange,
                        onDescripcionChange = viewModel::onDescripcionChange,
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
        }
    }
}

/* --------------------------------------------------------------------- */
/*  TABLA + FILTROS                                                      */
/* --------------------------------------------------------------------- */

@Composable
private fun AdminCategoriesTableWithFiltersSection(
    loading: Boolean,
    error: String?,
    items: List<AdminCategoryItem>,
    idValue: String,
    nombreValue: String,
    descripcionValue: String,
    onIdChange: (String) -> Unit,
    onNombreChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onBuscar: () -> Unit,
    onEditClick: (AdminCategoryItem) -> Unit,
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

    val weights = listOf(0.6f, 1.6f, 2.0f, 1.0f) // ID / Nombre / Descripción / Acciones

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // Card de filtros (ID, Nombre, Descripción)
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
                        value = descripcionValue,
                        onValueChange = onDescripcionChange,
                        label = { Text("Descripción") },
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

        // Cabecera tabla
        item {
            TableHeader(
                headers = listOf("ID", "Nombre", "Descripción", "Acciones"),
                weights = weights
            )
            Divider()
        }

        // Filas
        items(items) { item ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
            ) {
                AdminCategoryTableRow(
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
private fun AdminCategoryTableRow(
    item: AdminCategoryItem,
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
            text = item.descripcion,
            modifier = Modifier
                .weight(weights[2])
                .padding(horizontal = 6.dp),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            softWrap = true,
            maxLines = Int.MAX_VALUE
        )
        Box(
            modifier = Modifier
                .weight(weights[3])
                .padding(horizontal = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            // Botón "Editar" sin lógica todavía
            OutlinedButton(onClick = onEditClick) {
                Text("Editar")
            }
        }
    }
}

/* --------------------------------------------------------------------- */
/*  PAGINADOR (copiado del de productos para mantener estilo)            */
/* --------------------------------------------------------------------- */

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
