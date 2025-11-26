package com.example.blockfile.feature.adminusers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blockfile.core.domain.adminusers.DeleteAdminUserUseCase
import com.example.blockfile.core.domain.adminusers.GetAdminUserDetailUseCase
import com.example.blockfile.core.domain.adminusers.GetAdminUsersPageUseCase
import com.example.blockfile.core.domain.adminusers.SaveAdminUserSaldoUseCase
import com.example.blockfile.core.model.AdminUserItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminUsersUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val items: List<AdminUserItem> = emptyList(),

    // Filtros
    val id: String = "",
    val nombre: String = "",
    val saldo: String = "",

    val page: Int = 1,
    val totalPages: Int = 1,

    // Modal edición
    val showEditDialog: Boolean = false,
    val formId: Long? = null,
    val formNombre: String = "",
    val formCorreo: String = "",
    val formFecha: String = "",
    val formSaldo: String = "",

    // Confirm delete
    val showDeleteDialog: Boolean = false,
)

@HiltViewModel
class AdminUsersViewModel @Inject constructor(
    private val getAdminUsersPageUseCase: GetAdminUsersPageUseCase,
    private val getAdminUserDetailUseCase: GetAdminUserDetailUseCase,
    private val saveAdminUserSaldoUseCase: SaveAdminUserSaldoUseCase,
    private val deleteAdminUserUseCase: DeleteAdminUserUseCase,
) : ViewModel() {

    var uiState by mutableStateOf(AdminUsersUiState())
        private set

    init {
        buscarPrimeraPagina()
    }

    // -------- filtros --------
    fun onIdChange(value: String) {
        uiState = uiState.copy(id = value)
    }

    fun onNombreChange(value: String) {
        uiState = uiState.copy(nombre = value)
    }

    fun onSaldoChange(value: String) {
        uiState = uiState.copy(saldo = value)
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
                val saldo = uiState.saldo.takeIf { it.isNotBlank() }

                val result = getAdminUsersPageUseCase(
                    page = page,
                    id = idLong,
                    nombre = nombre,
                    saldo = saldo,
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
                    error = e.message ?: "Error al cargar usuarios",
                )
            }
        }
    }

    // -------- edición --------
    fun onEditClick(item: AdminUserItem) {
        viewModelScope.launch {
            uiState = uiState.copy(loading = true, error = null)
            try {
                val detail = getAdminUserDetailUseCase(item.id)

                val fechaPretty = detail.fecha
                    ?.let { it.replace("T", " ").take(19) }
                    ?: "—"

                uiState = uiState.copy(
                    loading = false,
                    showEditDialog = true,
                    formId = detail.id,
                    formNombre = detail.nombre,
                    formCorreo = detail.correo ?: "",
                    formFecha = fechaPretty,
                    formSaldo = detail.saldo ?: "",
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    loading = false,
                    error = e.message ?: "Error al obtener detalle del usuario",
                )
            }
        }
    }

    fun onDismissEditDialog() {
        uiState = uiState.copy(showEditDialog = false)
    }

    fun onFormSaldoChange(value: String) {
        uiState = uiState.copy(formSaldo = value)
    }

    fun onSubmitForm() {
        val id = uiState.formId ?: return
        val saldo = uiState.formSaldo.trim()

        if (saldo.isBlank()) {
            uiState = uiState.copy(error = "El saldo es obligatorio.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(loading = true, error = null)
            try {
                saveAdminUserSaldoUseCase(id = id, saldo = saldo)
                uiState = uiState.copy(
                    loading = false,
                    showEditDialog = false,
                )
                irPagina(uiState.page)
            } catch (e: Exception) {
                uiState = uiState.copy(
                    loading = false,
                    error = e.message ?: "Error al guardar el usuario",
                )
            }
        }
    }

    // -------- borrar --------
    fun onDeleteClickFromDialog() {
        uiState = uiState.copy(showDeleteDialog = true)
    }

    fun onDismissDeleteDialog() {
        uiState = uiState.copy(showDeleteDialog = false)
    }

    fun onConfirmDelete() {
        val id = uiState.formId ?: return

        viewModelScope.launch {
            uiState = uiState.copy(loading = true, error = null)
            try {
                deleteAdminUserUseCase(id)
                uiState = uiState.copy(
                    loading = false,
                    showDeleteDialog = false,
                    showEditDialog = false,
                )
                irPagina(uiState.page)
            } catch (e: Exception) {
                uiState = uiState.copy(
                    loading = false,
                    error = e.message ?: "Error al eliminar el usuario",
                )
            }
        }
    }
}
