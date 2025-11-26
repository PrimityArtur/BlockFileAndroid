package com.example.blockfile.feature.admincategories

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blockfile.core.domain.admincategories.DeleteAdminCategoryUseCase
import com.example.blockfile.core.domain.admincategories.GetAdminCategoriesPageUseCase
import com.example.blockfile.core.domain.admincategories.SaveAdminCategoryUseCase
import com.example.blockfile.core.model.AdminCategoryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminCategoriesUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val items: List<AdminCategoryItem> = emptyList(),

    // Filtros
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",

    val page: Int = 1,
    val totalPages: Int = 1,

    // Modal agregar/editar
    val showEditDialog: Boolean = false,
    val isEdit: Boolean = false,
    val formId: Long? = null,
    val formNombre: String = "",
    val formDescripcion: String = "",

    // Confirmación borrado
    val showDeleteDialog: Boolean = false,
    val deleteTarget: AdminCategoryItem? = null,
)

@HiltViewModel
class AdminCategoriesViewModel @Inject constructor(
    private val getAdminCategoriesPageUseCase: GetAdminCategoriesPageUseCase,
    private val saveAdminCategoryUseCase: SaveAdminCategoryUseCase,
    private val deleteAdminCategoryUseCase: DeleteAdminCategoryUseCase,
) : ViewModel() {

    var uiState by mutableStateOf(AdminCategoriesUiState())
        private set

    init {
        buscarPrimeraPagina()
    }

    // ------------------ Filtros ------------------

    fun onIdChange(value: String) {
        uiState = uiState.copy(id = value)
    }

    fun onNombreChange(value: String) {
        uiState = uiState.copy(nombre = value)
    }

    fun onDescripcionChange(value: String) {
        uiState = uiState.copy(descripcion = value)
    }

    fun buscarPrimeraPagina() {
        irPagina(1)
    }

    fun irPagina(page: Int) {
        viewModelScope.launch {
            uiState = uiState.copy(loading = true, error = null)
            try {
                val idLong = uiState.id.toLongOrNull()
                val nombre = uiState.nombre.takeIf { it.isNotBlank() }
                val descripcion = uiState.descripcion.takeIf { it.isNotBlank() }

                val result = getAdminCategoriesPageUseCase(
                    page = page,
                    id = idLong,
                    nombre = nombre,
                    descripcion = descripcion,
                )

                uiState = uiState.copy(
                    loading = false,
                    items = result.items,
                    page = result.page,
                    totalPages = result.totalPages,
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    loading = false,
                    error = e.message ?: "Error al cargar categorías",
                )
            }
        }
    }

    // ------------------ Modal agregar/editar ------------------

    fun onAddClick() {
        uiState = uiState.copy(
            showEditDialog = true,
            isEdit = false,
            formId = null,
            formNombre = "",
            formDescripcion = "",
            error = null,
        )
    }

    fun onEditClick(item: AdminCategoryItem) {
        uiState = uiState.copy(
            showEditDialog = true,
            isEdit = true,
            formId = item.id,
            formNombre = item.nombre,
            formDescripcion = item.descripcion,
            error = null,
        )
    }

    fun onDismissDialog() {
        uiState = uiState.copy(
            showEditDialog = false,
        )
    }

    fun onFormNombreChange(value: String) {
        uiState = uiState.copy(formNombre = value)
    }

    fun onFormDescripcionChange(value: String) {
        uiState = uiState.copy(formDescripcion = value)
    }

    fun onSubmitForm() {
        val nombre = uiState.formNombre.trim()
        val descripcion = uiState.formDescripcion.trim()

        if (nombre.isBlank()) {
            uiState = uiState.copy(error = "El nombre de la categoría es obligatorio.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(loading = true, error = null)
            try {
                saveAdminCategoryUseCase(
                    id = uiState.formId,
                    nombre = nombre,
                    descripcion = descripcion,
                )
                uiState = uiState.copy(
                    loading = false,
                    showEditDialog = false,
                )
                // Recarga página actual
                irPagina(uiState.page)
            } catch (e: Exception) {
                uiState = uiState.copy(
                    loading = false,
                    error = e.message ?: "Error al guardar la categoría",
                )
            }
        }
    }

    // ------------------ Borrado ------------------

    fun onDeleteClick(item: AdminCategoryItem) {
        uiState = uiState.copy(
            showDeleteDialog = true,
            deleteTarget = item,
            error = null,
        )
    }

    fun onDismissDeleteDialog() {
        uiState = uiState.copy(
            showDeleteDialog = false,
            deleteTarget = null,
        )
    }

    fun onConfirmDelete() {
        val target = uiState.deleteTarget ?: return

        viewModelScope.launch {
            uiState = uiState.copy(loading = true, error = null)
            try {
                deleteAdminCategoryUseCase(target.id)
                uiState = uiState.copy(
                    loading = false,
                    showDeleteDialog = false,
                    deleteTarget = null,
                )
                // Recargar página (si la última quedó vacía, podrías ajustar aquí)
                irPagina(uiState.page)
            } catch (e: Exception) {
                uiState = uiState.copy(
                    loading = false,
                    error = e.message ?: "Error al eliminar la categoría",
                )
            }
        }
    }
}
