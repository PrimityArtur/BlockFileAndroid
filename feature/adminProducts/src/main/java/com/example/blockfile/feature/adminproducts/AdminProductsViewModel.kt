package com.example.blockfile.feature.adminproducts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blockfile.core.domain.adminproducts.*
import com.example.blockfile.core.model.AdminProductDetail
import com.example.blockfile.core.model.AdminProductImage
import com.example.blockfile.core.model.AdminProductItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminProductsUiState(
    val id: String = "",
    val nombre: String = "",
    val autor: String = "",
    val categoria: String = "",

    val page: Int = 1,
    val totalPages: Int = 1,

    val loading: Boolean = false,
    val error: String? = null,
    val items: List<AdminProductItem> = emptyList(),

    val showEditDialog: Boolean = false,
    val isEdit: Boolean = false,
    val formId: Long? = null,
    val formNombre: String = "",
    val formDescripcion: String = "",
    val formVersion: String = "",
    val formPrecio: String = "",
    val formAutorId: String = "",
    val formCategoriaId: String = "",
    val formActivo: Boolean = true,

    val formTieneArchivo: Boolean = false,
    val formImagenes: List<AdminProductImage> = emptyList(),
)

@HiltViewModel
class AdminProductsViewModel @Inject constructor(
    private val getAdminProductsPage: GetAdminProductsPageUseCase,
    private val getAdminProductDetail: GetAdminProductDetailUseCase,
    private val saveAdminProduct: SaveAdminProductUseCase,
    private val uploadProductFileUseCase: UploadProductFileUseCase,
    private val addProductImageUseCase: AddProductImageUseCase,
    private val reorderProductImageUseCase: ReorderProductImageUseCase,
    private val deleteProductImageUseCase: DeleteProductImageUseCase,
) : ViewModel() {

    var uiState by mutableStateOf(AdminProductsUiState())
        private set

    init {
        loadPage(1)
    }

    /* ------------ Filtros ------------ */

    fun onIdChange(newId: String) {
        val filtered = newId.filter { it.isDigit() }
        uiState = uiState.copy(id = filtered)
    }

    fun onNombreChange(new: String) {
        uiState = uiState.copy(nombre = new)
    }

    fun onAutorChange(new: String) {
        uiState = uiState.copy(autor = new)
    }

    fun onCategoriaChange(new: String) {
        uiState = uiState.copy(categoria = new)
    }

    fun buscarPrimeraPagina() {
        loadPage(1)
    }

    fun irPagina(page: Int) {
        val safePage = page.coerceAtLeast(1)
        loadPage(safePage)
    }

    private fun loadPage(page: Int) {
        viewModelScope.launch {
            uiState = uiState.copy(loading = true, error = null)
            try {
                val idLong = uiState.id.toLongOrNull()
                val result = getAdminProductsPage(
                    page = page,
                    id = idLong,
                    nombre = uiState.nombre.takeIf { it.isNotBlank() },
                    autor = uiState.autor.takeIf { it.isNotBlank() },
                    categoria = uiState.categoria.takeIf { it.isNotBlank() },
                )

                uiState = uiState.copy(
                    loading = false,
                    page = result.page,
                    totalPages = result.totalPages,
                    items = result.items,
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    loading = false,
                    error = e.message ?: "Error al cargar los productos"
                )
            }
        }
    }

    /* ------------ Formulario ------------ */

    fun onAddClick() {
        uiState = uiState.copy(
            showEditDialog = true,
            isEdit = false,
            formId = null,
            formNombre = "",
            formDescripcion = "",
            formVersion = "",
            formPrecio = "",
            formAutorId = "",
            formCategoriaId = "",
            formActivo = true,
            formTieneArchivo = false,
            formImagenes = emptyList(),
        )
    }

    fun onEditClick(item: AdminProductItem) {
        viewModelScope.launch {
            uiState = uiState.copy(loading = true, error = null)
            try {
                val detail = getAdminProductDetail(item.id)
                uiState = uiState.copy(
                    loading = false,
                    showEditDialog = true,
                    isEdit = true,
                    formId = detail.id,
                    formNombre = detail.nombre,
                    formDescripcion = detail.descripcion,
                    formVersion = detail.version,
                    formPrecio = detail.precio,
                    formAutorId = detail.autorId?.toString() ?: "",
                    formCategoriaId = detail.categoriaId?.toString() ?: "",
                    formActivo = detail.activo,
                    formTieneArchivo = detail.tieneArchivo,
                    formImagenes = detail.imagenes.sortedBy { it.orden },
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    loading = false,
                    error = e.message ?: "Error al cargar detalle del producto"
                )
            }
        }
    }

    fun onDismissDialog() {
        uiState = uiState.copy(showEditDialog = false)
    }

    fun onFormNombreChange(new: String) {
        uiState = uiState.copy(formNombre = new)
    }

    fun onFormDescripcionChange(new: String) {
        uiState = uiState.copy(formDescripcion = new)
    }

    fun onFormVersionChange(new: String) {
        uiState = uiState.copy(formVersion = new)
    }

    fun onFormPrecioChange(new: String) {
        uiState = uiState.copy(formPrecio = new)
    }

    fun onFormAutorIdChange(new: String) {
        uiState = uiState.copy(formAutorId = new.filter { it.isDigit() })
    }

    fun onFormCategoriaIdChange(new: String) {
        uiState = uiState.copy(formCategoriaId = new.filter { it.isDigit() })
    }

    fun onFormActivoToggle() {
        uiState = uiState.copy(formActivo = !uiState.formActivo)
    }

    fun onSubmitForm() {
        val nombre = uiState.formNombre.trim()
        val precioTxt = uiState.formPrecio.trim()

        if (nombre.isBlank()) {
            uiState = uiState.copy(error = "El nombre no puede estar vacío")
            return
        }
        if (precioTxt.isBlank()) {
            uiState = uiState.copy(error = "El precio no puede estar vacío")
            return
        }

        val autorId = uiState.formAutorId.toLongOrNull()
        val categoriaId = uiState.formCategoriaId.toLongOrNull()

        val detail = AdminProductDetail(
            id = uiState.formId,
            nombre = nombre,
            descripcion = uiState.formDescripcion,
            version = uiState.formVersion,
            precio = precioTxt,
            autorId = autorId,
            categoriaId = categoriaId,
            activo = uiState.formActivo,
            tieneArchivo = uiState.formTieneArchivo,
            imagenes = uiState.formImagenes,
        )

        viewModelScope.launch {
            uiState = uiState.copy(loading = true, error = null)
            try {
                saveAdminProduct(detail)
                val currentPage = uiState.page
                uiState = uiState.copy(
                    loading = false,
                    showEditDialog = false,
                )
                loadPage(currentPage)
            } catch (e: Exception) {
                uiState = uiState.copy(
                    loading = false,
                    error = e.message ?: "Error al guardar el producto"
                )
            }
        }
    }

    /* ------------ Archivo / Imágenes ------------ */

    private fun reloadDetailInForm() {
        val id = uiState.formId ?: return
        viewModelScope.launch {
            try {
                val detail = getAdminProductDetail(id)
                uiState = uiState.copy(
                    formTieneArchivo = detail.tieneArchivo,
                    formImagenes = detail.imagenes.sortedBy { it.orden }
                )
            } catch (_: Exception) { }
        }
    }

    fun uploadFileForCurrentProduct(bytes: ByteArray, filename: String) {
        val id = uiState.formId ?: return
        viewModelScope.launch {
            uiState = uiState.copy(loading = true, error = null)
            try {
                uploadProductFileUseCase(id, bytes, filename)
                uiState = uiState.copy(loading = false)
                reloadDetailInForm()
            } catch (e: Exception) {
                uiState = uiState.copy(
                    loading = false,
                    error = e.message ?: "Error al subir archivo"
                )
            }
        }
    }

    fun addImageForCurrentProduct(bytes: ByteArray, filename: String, orden: Int? = null) {
        val id = uiState.formId ?: return
        viewModelScope.launch {
            uiState = uiState.copy(loading = true, error = null)
            try {
                addProductImageUseCase(id, bytes, filename, orden)
                uiState = uiState.copy(loading = false)
                reloadDetailInForm()
            } catch (e: Exception) {
                uiState = uiState.copy(
                    loading = false,
                    error = e.message ?: "Error al agregar imagen"
                )
            }
        }
    }

    fun moveImageUp(image: AdminProductImage) {
        viewModelScope.launch {
            val newOrden = (image.orden - 1).coerceAtLeast(1)
            try {
                reorderProductImageUseCase(image.id, newOrden)
                reloadDetailInForm()
            } catch (e: Exception) {
                uiState = uiState.copy(error = e.message ?: "Error al reordenar imagen")
            }
        }
    }

    fun moveImageDown(image: AdminProductImage) {
        viewModelScope.launch {
            val newOrden = image.orden + 1
            try {
                reorderProductImageUseCase(image.id, newOrden)
                reloadDetailInForm()
            } catch (e: Exception) {
                uiState = uiState.copy(error = e.message ?: "Error al reordenar imagen")
            }
        }
    }

    fun deleteImage(image: AdminProductImage) {
        viewModelScope.launch {
            try {
                deleteProductImageUseCase(image.id)
                reloadDetailInForm()
            } catch (e: Exception) {
                uiState = uiState.copy(error = e.message ?: "Error al eliminar imagen")
            }
        }
    }
}
