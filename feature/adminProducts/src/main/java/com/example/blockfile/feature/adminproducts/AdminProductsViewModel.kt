package com.example.blockfile.feature.adminproducts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blockfile.core.domain.adminproducts.GetAdminProductsPageUseCase
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
)

@HiltViewModel
class AdminProductsViewModel @Inject constructor(
    private val getAdminProductsPage: GetAdminProductsPageUseCase,
) : ViewModel() {

    var uiState by mutableStateOf(AdminProductsUiState())
        private set

    init {
        loadPage(1)
    }

    fun onIdChange(newId: String) {
        // Solo dígitos para el filtro ID
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
}
