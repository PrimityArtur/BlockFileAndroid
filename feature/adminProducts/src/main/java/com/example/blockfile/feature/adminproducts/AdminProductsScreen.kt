package com.example.blockfile.feature.adminproducts

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.blockfile.core.model.AdminProductImage
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
    val context = LocalContext.current

    // ---------- PICKER PARA ARCHIVO PRINCIPAL ----------
    val archivoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val pair = readBytesAndNameFromUri(context, uri)
            if (pair != null) {
                val (bytes, filename) = pair
                viewModel.uploadFileForCurrentProduct(bytes, filename)
            }
        }
    }

    // ---------- PICKER PARA IMAGENES ----------
    val imagenPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val pair = readBytesAndNameFromUri(context, uri)
            if (pair != null) {
                val (bytes, filename) = pair
                viewModel.addImageForCurrentProduct(bytes, filename, orden = null)
            }
        }
    }

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
        Box(modifier = Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
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
                        onEditClick = { item -> viewModel.onEditClick(item) },
                    )
                }

                PagerControls(
                    page = state.page,
                    totalPages = state.totalPages,
                    onPageChange = { viewModel.irPagina(it) }
                )
            }

            if (state.showEditDialog) {
                AdminProductEditDialog(
                    isEdit = state.isEdit,
                    nombre = state.formNombre,
                    descripcion = state.formDescripcion,
                    version = state.formVersion,
                    precio = state.formPrecio,
                    autorId = state.formAutorId,
                    categoriaId = state.formCategoriaId,
                    activo = state.formActivo,
                    tieneArchivo = state.formTieneArchivo,
                    imagenes = state.formImagenes,
                    loading = state.loading,
                    onNombreChange = viewModel::onFormNombreChange,
                    onDescripcionChange = viewModel::onFormDescripcionChange,
                    onVersionChange = viewModel::onFormVersionChange,
                    onPrecioChange = viewModel::onFormPrecioChange,
                    onAutorIdChange = viewModel::onFormAutorIdChange,
                    onCategoriaIdChange = viewModel::onFormCategoriaIdChange,
                    onActivoToggle = viewModel::onFormActivoToggle,
                    onUploadFileClick = {
                        // abre picker para cualquier archivo
                        archivoPickerLauncher.launch("*/*")
                    },
                    onAddImageClick = {
                        // abre picker solo para imágenes
                        imagenPickerLauncher.launch("image/*")
                    },
                    onMoveImageUp = { img -> viewModel.moveImageUp(img) },
                    onMoveImageDown = { img -> viewModel.moveImageDown(img) },
                    onDeleteImage = { img -> viewModel.deleteImage(img) },
                    onDismiss = viewModel::onDismissDialog,
                    onSubmit = viewModel::onSubmitForm,
                )
            }
        }
    }
}

/* --------------------------------------------------------------------- */
/*  TABLA + FILTROS                                                      */
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

    val weights = listOf(0.6f, 2.0f, 1.4f, 1.4f, 1.2f)

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

        item {
            TableHeader(
                headers = listOf("ID", "Producto", "Autor", "Categoría", "Acciones"),
                weights = weights
            )
            Divider()
        }

        items(items) { item ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
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
            .padding(vertical = 30.dp, horizontal = 16.dp),
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
            text = item.autor.ifBlank { "-" },
            modifier = Modifier
                .weight(weights[2])
                .padding(horizontal = 6.dp),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            softWrap = true,
            maxLines = Int.MAX_VALUE
        )
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
        Box(
            modifier = Modifier
                .weight(weights[4])
                .padding(horizontal = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            OutlinedButton(onClick = onEditClick) {
                Text("+")
            }
        }
    }
}

/* --------------------------------------------------------------------- */
/*  DIALOGO DE EDICIÓN (SCROLL + IMAGENES)                               */
/* --------------------------------------------------------------------- */

@Composable
private fun AdminProductEditDialog(
    isEdit: Boolean,
    nombre: String,
    descripcion: String,
    version: String,
    precio: String,
    autorId: String,
    categoriaId: String,
    activo: Boolean,
    tieneArchivo: Boolean,
    imagenes: List<AdminProductImage>,
    loading: Boolean,
    onNombreChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onVersionChange: (String) -> Unit,
    onPrecioChange: (String) -> Unit,
    onAutorIdChange: (String) -> Unit,
    onCategoriaIdChange: (String) -> Unit,
    onActivoToggle: () -> Unit,
    onUploadFileClick: () -> Unit,
    onAddImageClick: () -> Unit,
    onMoveImageUp: (AdminProductImage) -> Unit,
    onMoveImageDown: (AdminProductImage) -> Unit,
    onDeleteImage: (AdminProductImage) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
) {
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (isEdit) "Editar producto" else "Agregar producto")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(scrollState)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = onNombreChange,
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = onDescripcionChange,
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = version,
                    onValueChange = onVersionChange,
                    label = { Text("Versión") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = precio,
                    onValueChange = onPrecioChange,
                    label = { Text("Precio (ej. 10.50)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = autorId,
                        onValueChange = onAutorIdChange,
                        label = { Text("ID Autor") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = categoriaId,
                        onValueChange = onCategoriaIdChange,
                        label = { Text("ID Categoría") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = activo,
                        onCheckedChange = { onActivoToggle() }
                    )
                    Text("Activo")
                }

                Divider()

                Text(
                    text = "Archivo del producto",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (tieneArchivo) "Archivo actual: Sí" else "Archivo actual: No",
                    style = MaterialTheme.typography.bodySmall
                )
                Button(
                    onClick = onUploadFileClick,
                    enabled = !loading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (tieneArchivo) "Reemplazar archivo" else "Subir archivo")
                }

                Divider()

                Text(
                    text = "Imágenes del producto",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                if (imagenes.isEmpty()) {
                    Text(
                        text = "Sin imágenes",
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        imagenes.sortedBy { it.orden }.forEach { img ->
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    AsyncImage(
                                        model = img.url,
                                        contentDescription = "Imagen ${img.id}",
                                        modifier = Modifier.size(64.dp)
                                    )

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "ID: ${img.id}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = "Orden: ${img.orden}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        TextButton(
                                            onClick = { onMoveImageUp(img) },
                                            enabled = !loading
                                        ) { Text("↑") }
                                        TextButton(
                                            onClick = { onMoveImageDown(img) },
                                            enabled = !loading
                                        ) { Text("↓") }
                                        TextButton(
                                            onClick = { onDeleteImage(img) },
                                            enabled = !loading
                                        ) { Text("X") }
                                    }
                                }
                            }
                        }
                    }
                }

                OutlinedButton(
                    onClick = onAddImageClick,
                    enabled = !loading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Agregar imagen")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onSubmit,
                enabled = !loading
            ) {
                Text("Guardar")
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

/* --------------------------------------------------------------------- */
/*  PAGINADOR                                                            */
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

/* --------------------------------------------------------------------- */
/*  HELPERS PARA LEER BYTES + NOMBRE DE URI                              */
/* --------------------------------------------------------------------- */

private fun readBytesAndNameFromUri(context: Context, uri: Uri): Pair<ByteArray, String>? {
    val resolver = context.contentResolver

    val name = resolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (cursor.moveToFirst() && nameIndex >= 0) {
            cursor.getString(nameIndex)
        } else null
    } ?: "archivo.bin"

    val bytes = resolver.openInputStream(uri)?.use { input ->
        input.readBytes()
    } ?: return null

    return bytes to name
}
