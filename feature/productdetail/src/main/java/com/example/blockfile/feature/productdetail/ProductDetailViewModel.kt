package com.example.blockfile.feature.productdetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blockfile.core.data.repository.ProductDetailResult
import com.example.blockfile.core.domain.product.BuyProductUseCase
import com.example.blockfile.core.domain.product.CommentOnProductUseCase
import com.example.blockfile.core.domain.product.DownloadProductUseCase
import com.example.blockfile.core.domain.product.GetProductDetailUseCase
import com.example.blockfile.core.domain.product.RateProductUseCase
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

data class CommentUiState(
    val showDialog: Boolean = false,
    val text: String = "",
    val sending: Boolean = false,
    val error: String? = null,
    val success: String? = null,
)

data class RatingUiState(
    val showDialog: Boolean = false,
    val selectedRating: Int = 0,   // 1..5
    val sending: Boolean = false,
    val error: String? = null,
    val success: String? = null,
)

data class PurchaseUiState(
    val buying: Boolean = false,
    val error: String? = null,
    val success: String? = null,
)

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductDetailUseCase: GetProductDetailUseCase,
    private val commentOnProductUseCase: CommentOnProductUseCase,
    private val downloadProductUseCase: DownloadProductUseCase,
    private val rateProductUseCase: RateProductUseCase,
    private val buyProductUseCase: BuyProductUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val productId: Long =
        savedStateHandle["productId"] ?: error("productId requerido en la ruta")

    var uiState by mutableStateOf(ProductDetailUiState())
        private set

    var downloadState by mutableStateOf(ProductDetailDownloadState())
        private set

    var commentUiState by mutableStateOf(CommentUiState())
        private set

    var ratingUiState by mutableStateOf(RatingUiState())
        private set

    var purchaseUiState by mutableStateOf(PurchaseUiState())
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


    // ====== Comentario ======

    fun openCommentDialog() {
        commentUiState = CommentUiState(showDialog = true)
    }

    fun dismissCommentDialog() {
        commentUiState = CommentUiState(showDialog = false)
    }

    fun onCommentTextChange(newText: String) {
        commentUiState = commentUiState.copy(
            text = newText,
            error = null,
            success = null,
        )
    }

    fun sendComment() {
        val text = commentUiState.text.trim()
        if (text.isEmpty()) {
            commentUiState = commentUiState.copy(
                error = "El comentario no puede estar vacío."
            )
            return
        }

        viewModelScope.launch {
            commentUiState = commentUiState.copy(
                sending = true,
                error = null,
                success = null,
            )
            try {
                commentOnProductUseCase(productId, text)
                // Recargar comentarios (y detalle) desde el backend
                load()

                commentUiState = commentUiState.copy(
                    sending = false,
                    success = "Comentario enviado correctamente.",
                    text = "",
                )
            } catch (e: Exception) {
                commentUiState = commentUiState.copy(
                    sending = false,
                    error = e.message ?: "Error al enviar el comentario.",
                )
            }
        }
    }


    // ====== CALIFICACIÓN ======

    fun openRatingDialog() {
        ratingUiState = RatingUiState(showDialog = true)
    }

    fun dismissRatingDialog() {
        ratingUiState = RatingUiState(showDialog = false)
    }

    fun onRatingSelected(newRating: Int) {
        ratingUiState = ratingUiState.copy(
            selectedRating = newRating.coerceIn(1, 5),
            error = null,
            success = null,
        )
    }

    fun sendRating() {
        val rating = ratingUiState.selectedRating
        if (rating !in 1..5) {
            ratingUiState = ratingUiState.copy(
                error = "Selecciona una calificación entre 1 y 5 estrellas."
            )
            return
        }

        viewModelScope.launch {
            ratingUiState = ratingUiState.copy(
                sending = true,
                error = null,
                success = null,
            )
            try {
                rateProductUseCase(productId, rating)
                // Recargar detalle para actualizar promedio
                load()
                ratingUiState = ratingUiState.copy(
                    sending = false,
                    success = "Calificación registrada correctamente.",
                )
            } catch (e: Exception) {
                ratingUiState = ratingUiState.copy(
                    sending = false,
                    error = e.message ?: "Error al registrar la calificación.",
                )
            }
        }
    }

    // ====== COMPRA ======

    fun buyProduct() {
        viewModelScope.launch {
            purchaseUiState = purchaseUiState.copy(
                buying = true,
                error = null,
                success = null,
            )
            try {
                val result = buyProductUseCase(productId)
                uiState = uiState.copy(
                    detail = result,
                    error = null,
                )
                purchaseUiState = purchaseUiState.copy(
                    buying = false,
                    success = "Compra realizada correctamente.",
                )
            } catch (e: Exception) {
                purchaseUiState = purchaseUiState.copy(
                    buying = false,
                    error = e.message ?: "Error al realizar la compra.",
                )
            }
        }
    }
}
