package com.example.blockfile.feature.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blockfile.core.data.repository.ComprasPerfilPage
import com.example.blockfile.core.domain.profile.GetComprasPerfilUseCase
import com.example.blockfile.core.domain.profile.GetPerfilClienteUseCase
import com.example.blockfile.core.domain.profile.UpdatePerfilClienteUseCase
import com.example.blockfile.core.model.CompraPerfil
import com.example.blockfile.core.model.PerfilCliente
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val perfil: PerfilCliente? = null,

    val nombre: String = "",
    val correo: String = "",
    val contrasena: String = "",

    val saving: Boolean = false,
    val saveError: String? = null,
    val saveSuccess: Boolean = false,
)

data class ProfileComprasState(
    val items: List<CompraPerfil> = emptyList(),
    val page: Int = 1,
    val totalPages: Int = 1,
    val loading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getPerfilCliente: GetPerfilClienteUseCase,
    private val updatePerfilCliente: UpdatePerfilClienteUseCase,
    private val getComprasPerfil: GetComprasPerfilUseCase,
) : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    var comprasState by mutableStateOf(ProfileComprasState())
        private set

    fun loadInitial() {
        loadPerfil()
        loadCompras(1)
    }

    fun loadPerfil() {
        viewModelScope.launch {
            uiState = uiState.copy(loading = true, error = null, saveSuccess = false)
            try {
                val perfil = getPerfilCliente()
                uiState = uiState.copy(
                    loading = false,
                    perfil = perfil,
                    nombre = perfil.nombreUsuario,
                    correo = perfil.correo,
                    contrasena = "",
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    loading = false,
                    error = e.message ?: "Error desconocido",
                )
            }
        }
    }

    fun loadCompras(page: Int) {
        viewModelScope.launch {
            comprasState = comprasState.copy(loading = true, error = null)
            try {
                val res: ComprasPerfilPage = getComprasPerfil(page)
                comprasState = comprasState.copy(
                    items = res.items,
                    page = res.page,
                    totalPages = res.totalPages,
                    loading = false,
                )
            } catch (e: Exception) {
                comprasState = comprasState.copy(
                    loading = false,
                    error = e.message ?: "Error desconocido",
                )
            }
        }
    }

    fun onNombreChange(value: String) {
        uiState = uiState.copy(nombre = value, saveError = null, saveSuccess = false)
    }

    fun onCorreoChange(value: String) {
        uiState = uiState.copy(correo = value, saveError = null, saveSuccess = false)
    }

    fun onContrasenaChange(value: String) {
        uiState = uiState.copy(contrasena = value, saveError = null, saveSuccess = false)
    }

    fun guardarCambios() {
        viewModelScope.launch {
            val current = uiState
            uiState = current.copy(saving = true, saveError = null, saveSuccess = false)
            try {
                val perfilActualizado = updatePerfilCliente(
                    nombre = current.nombre,
                    correo = current.correo,
                    contrasena = current.contrasena.ifBlank { null },
                )
                uiState = uiState.copy(
                    saving = false,
                    perfil = perfilActualizado,
                    nombre = perfilActualizado.nombreUsuario,
                    correo = perfilActualizado.correo,
                    contrasena = "",
                    saveSuccess = true,
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    saving = false,
                    saveError = e.message ?: "Error al actualizar el perfil.",
                    saveSuccess = false,
                )
            }
        }
    }
}
