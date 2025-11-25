// feature/profile/src/main/java/com/example/blockfile/feature/profile/AdminProfileViewModel.kt
package com.example.blockfile.feature.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blockfile.core.data.network.BlockFileApi
import com.example.blockfile.core.model.AdminProfileDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminProfileUiState(
    val idUsuario: Long? = null,
    val nombre: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val loading: Boolean = false,
    val saving: Boolean = false,
    val error: String? = null,
    val success: String? = null,
)

@HiltViewModel
class AdminProfileViewModel @Inject constructor(
    private val api: BlockFileApi,
) : ViewModel() {

    var uiState by mutableStateOf(AdminProfileUiState())
        private set

    fun loadProfile(idUsuario: Long) {
        if (uiState.loading) return
        viewModelScope.launch {
            uiState = uiState.copy(loading = true, error = null, success = null)
            try {
                val dto = api.getAdminProfile(idUsuario)
                uiState = uiState.copy(
                    loading = false,
                    idUsuario = dto.id_usuario,
                    nombre = dto.nombre,
                    correo = dto.correo,
                    contrasena = dto.contrasena,
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    loading = false,
                    error = e.message ?: "Error al cargar perfil de administrador"
                )
            }
        }
    }

    fun onNombreChange(value: String) {
        uiState = uiState.copy(nombre = value, error = null, success = null)
    }

    fun onCorreoChange(value: String) {
        uiState = uiState.copy(correo = value, error = null, success = null)
    }

    fun onContrasenaChange(value: String) {
        uiState = uiState.copy(contrasena = value, error = null, success = null)
    }

    fun saveProfile() {
        val id = uiState.idUsuario ?: return
        if (uiState.saving) return
        viewModelScope.launch {
            uiState = uiState.copy(saving = true, error = null, success = null)
            try {
                val body = AdminProfileDto(
                    id_usuario = id,
                    nombre = uiState.nombre,
                    correo = uiState.correo,
                    contrasena = uiState.contrasena,
                )
                val updated = api.updateAdminProfile(body)
                uiState = uiState.copy(
                    saving = false,
                    nombre = updated.nombre,
                    correo = updated.correo,
                    contrasena = updated.contrasena,
                    success = "Datos actualizados correctamente.",
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    saving = false,
                    error = e.message ?: "Error al actualizar perfil"
                )
            }
        }
    }
}
