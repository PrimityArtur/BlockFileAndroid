package com.example.blockfile.feature.productdetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blockfile.core.data.repository.ProductDetailResult
import com.example.blockfile.core.domain.product.GetProductDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductDetailUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val detail: ProductDetailResult? = null,
)

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductDetail: GetProductDetailUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var uiState by mutableStateOf(ProductDetailUiState())
        private set

    private val productId: Long =
        savedStateHandle["productId"] ?: error("productId requerido en la ruta")

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            uiState = uiState.copy(loading = true, error = null)
            try {
                val result = getProductDetail(productId)
                uiState = ProductDetailUiState(
                    loading = false,
                    error = null,
                    detail = result,
                )
            } catch (e: Exception) {
                uiState = ProductDetailUiState(
                    loading = false,
                    error = e.message ?: "Error desconocido",
                    detail = null,
                )
            }
        }
    }
}
