package com.example.blockfile.feature.catalog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blockfile.core.data.repository.CatalogPage
import com.example.blockfile.core.domain.catalog.GetCatalogPageUseCase
import com.example.blockfile.core.model.ProductoCatalogo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CatalogUiState(
    val nombre: String = "",
    val autor: String = "",
    val categoria: String = "",
    val page: Int = 1,
    val totalPages: Int = 1,
    val loading: Boolean = false,
    val error: String? = null,
    val items: List<ProductoCatalogo> = emptyList(),
)

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val getCatalogPage: GetCatalogPageUseCase,
) : ViewModel() {

    var uiState by mutableStateOf(CatalogUiState())
        private set

    init {
        loadPage(1)
    }

    fun onNombreChange(value: String) {
        uiState = uiState.copy(nombre = value, error = null)
    }

    fun onAutorChange(value: String) {
        uiState = uiState.copy(autor = value, error = null)
    }

    fun onCategoriaChange(value: String) {
        uiState = uiState.copy(categoria = value, error = null)
    }

    fun buscar() {
        loadPage(1)
    }

    fun irPagina(page: Int) {
        val p = page.coerceIn(1, uiState.totalPages.coerceAtLeast(1))
        loadPage(p)
    }

    private fun loadPage(page: Int) {
        viewModelScope.launch {
            uiState = uiState.copy(loading = true, error = null)
            try {
                val res: CatalogPage = getCatalogPage(
                    page = page,
                    nombre = uiState.nombre,
                    autor = uiState.autor,
                    categoria = uiState.categoria,
                )
                uiState = uiState.copy(
                    loading = false,
                    page = res.page,
                    totalPages = res.totalPages,
                    items = res.items,
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    loading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }
}
