package com.example.blockfile.feature.admincategories

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blockfile.core.domain.admincategories.GetAdminCategoriesPageUseCase
import com.example.blockfile.core.model.AdminCategoryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminCategoriesUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val items: List<AdminCategoryItem> = emptyList(),

    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",

    val page: Int = 1,
    val totalPages: Int = 1,
)

@HiltViewModel
class AdminCategoriesViewModel @Inject constructor(
    private val getAdminCategoriesPageUseCase: GetAdminCategoriesPageUseCase,
) : ViewModel() {

    var uiState by mutableStateOf(AdminCategoriesUiState())
        private set

    init {
        // Carga inicial como en AdminProducts
        buscarPrimeraPagina()
    }

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

    // Por ahora estos no hacen nada (solo existen para el onClick de los botones)
    fun onAddClick() {
        // TODO: implementar modal de agregar categoría
    }

    fun onEditClick(item: AdminCategoryItem) {
        // TODO: implementar modal de edición de categoría
    }
}
