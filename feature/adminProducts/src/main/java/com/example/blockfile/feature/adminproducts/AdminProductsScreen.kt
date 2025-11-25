package com.example.blockfile.feature.adminproducts

import androidx.compose.foundation.clickable
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
import com.example.blockfile.core.model.AdminProductItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductsScreen(
    viewModel: AdminProductsViewModel,
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
                    TextButton(onClick = { /* ya estás en Inventario */ }) { Text("Inventario") }
                    TextButton(onClick = onGoCategorias) { Text("Categorías") }
                    TextButton(onClick = onGoUsuarios) { Text("Usuarios") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // ===== TÍTULO + BOTÓN AGREGAR =====
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gestión de productos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = { /* TODO: abrir creación de producto */ },
                    enabled = !state.loading
                ) {
                    Text("Agregar")
                }
            }

            // ===== CONTENIDO SCROLL (FILTROS + TABLA) =====
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AdminProductsTableWithFiltersSection(
                    loading = state.loading,
                    error = state.error,
                    items = state.items,
                    idValue = state.id,
                    nombreValue = state.nombre,
                    autorValue = state.autor,
                    categoriaValue = state.categoria,
                    onIdChange = viewModel::onIdChange,
                    onNombreChange = viewModel::onNombreChange,
                    onAutorChange = viewModel::onAutorChange,
                    onCategoriaChange = viewModel::onCategoriaChange,
                    onBuscar = { viewModel.buscarPrimeraPagina() },
                    onEditClick = { /* luego abrimos pantalla edición */ },
                )
            }

            // ===== PAGINADOR FIJO ABAJO (IGUAL QUE RANKINGS) =====
            PagerControls(
                page = state.page,
                totalPages = state.totalPages,
                onPageChange = { viewModel.irPagina(it) }
            )
        }
    }
}

/* --------------------------------------------------------------------- */
/*  SECCIÓN SCROLLEABLE: FILTROS + TABLA ESTILO RANKINGS                 */
/* --------------------------------------------------------------------- */

@Composable
private fun AdminProductsTableWithFiltersSection(
    loading: Boolean,
    error: String?,
    items: List<AdminProductItem>,
    idValue: String,
    nombreValue: String,
    autorValue: String,
    categoriaValue: String,
    onIdChange: (String) -> Unit,
    onNombreChange: (String) -> Unit,
    onAutorChange: (String) -> Unit,
    onCategoriaChange: (String) -> Unit,
    onBuscar: () -> Unit,
    onEditClick: (AdminProductItem) -> Unit,
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

    // Pesos de cada columna: ID, Nombre, Autor, Categoría, Acciones
    val weights = listOf(0.4f, 2.0f, 1.4f, 1.4f, 1.5f)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // ===== ITEM 1: FILTROS =====
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = autorValue,
                            onValueChange = onAutorChange,
                            label = { Text("Autor") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = categoriaValue,
                            onValueChange = onCategoriaChange,
                            label = { Text("Categoría") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

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

        // ===== ITEM 2: HEADER DE TABLA =====
        item {
            TableHeader(
                headers = listOf("ID", "Producto", "Autor", "Categoría", "Acciones"),
                weights = weights
            )
            Divider()
        }

        // ===== ITEMS: FILAS DE TABLA =====
        items(items) { item ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = false) { /* nada por ahora */ },
            ) {
                AdminProductTableRow(
                    item = item,
                    weights = weights,
                    onEditClick = { onEditClick(item) }
                )
            }
            Divider()
        }
    }
}

/* --------------------------------------------------------------------- */
/*  HEADER Y FILA IGUAL ESTILO RANKINGS                                  */
/* --------------------------------------------------------------------- */

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
private fun AdminProductTableRow(
    item: AdminProductItem,
    weights: List<Float>,
    onEditClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 25.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ID
        Text(
            text = item.id.toString(),
            modifier = Modifier
                .weight(weights[0])
                .padding(horizontal = 6.dp),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        // Nombre
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
        // Autor
        Text(
            text = item.autor.ifBlank { "-" },
            modifier = Modifier
                .weight(weights[2])
                .padding(horizontal = 6.dp),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            softWrap = true,
            maxLines = Int.MAX_VALUE
        )
        // Categoría
        Text(
            text = item.categoria.ifBlank { "-" },
            modifier = Modifier
                .weight(weights[3])
                .padding(horizontal = 6.dp),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            softWrap = true,
            maxLines = Int.MAX_VALUE
        )
        // Acciones
        Box(
            modifier = Modifier
                .weight(weights[4])
                .padding(horizontal = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            OutlinedButton(onClick = onEditClick) {
                Text("Edit")
            }
        }
    }
}

/* --------------------------------------------------------------------- */
/*  PAGINADOR IGUAL QUE EN RANKINGS                                      */
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
