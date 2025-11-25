package com.example.blockfile.feature.productdetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blockfile.core.data.repository.ProductDetailResult
import com.example.blockfile.core.domain.product.DownloadProductUseCase
import com.example.blockfile.core.domain.product.GetProductDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class ProductDetailUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val detail: ProductDetailResult? = null,
)

data class ProductDetailDownloadState(
    val downloading: Boolean = false,
    val lastDownloadedFile: File? = null,
    val error: String? = null,
)

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductDetailUseCase: GetProductDetailUseCase,
    private val downloadProductUseCase: DownloadProductUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val productId: Long =
        savedStateHandle["productId"] ?: error("productId requerido en la ruta")

    var uiState by mutableStateOf(ProductDetailUiState())
        private set

    var downloadState by mutableStateOf(ProductDetailDownloadState())
        private set

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            uiState = uiState.copy(loading = true, error = null)
            try {
                val result = getProductDetailUseCase(productId)
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

    fun downloadProduct() {
        viewModelScope.launch {
            downloadState = downloadState.copy(
                downloading = true,
                error = null,
                lastDownloadedFile = null,
            )
            try {
                val file = downloadProductUseCase(productId)
                downloadState = downloadState.copy(
                    downloading = false,
                    lastDownloadedFile = file,
                    error = null,
                )
            } catch (e: Exception) {
                downloadState = downloadState.copy(
                    downloading = false,
                    error = e.message ?: "Error al descargar el archivo.",
                )
            }
        }
    }
}
