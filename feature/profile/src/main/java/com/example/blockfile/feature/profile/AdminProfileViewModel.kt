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

    // En tu flujo real, este id debería venir del login (guardado en prefs, etc.)
    fun loadInitial(idUsuario: Long) {
        // Evitar recarga si ya lo tenemos
        if (uiState.idUsuario != null) return

        viewModelScope.launch {
            uiState = uiState.copy(loading = true, error = null, success = null)
            try {
                val dto = api.getAdminProfile(idUsuario)
                uiState = uiState.copy(
                    idUsuario = dto.id_usuario,
                    nombre = dto.nombre,
                    correo = dto.correo,
                    contrasena = dto.contrasena,
                    loading = false
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    loading = false,
                    error = e.message ?: "Error al cargar perfil"
                )
            }
        }
    }

    fun onNombreChange(value: String) {
        uiState = uiState.copy(nombre = value.take(10), error = null, success = null)
    }

    fun onCorreoChange(value: String) {
        uiState = uiState.copy(correo = value, error = null, success = null)
    }

    fun onContrasenaChange(value: String) {
        uiState = uiState.copy(contrasena = value, error = null, success = null)
    }

    fun guardarCambios() {
        val id = uiState.idUsuario ?: return

        viewModelScope.launch {
            uiState = uiState.copy(saving = true, error = null, success = null)
            try {
                val body = AdminProfileDto(
                    id_usuario = id,
                    nombre = uiState.nombre,
                    correo = uiState.correo,
                    contrasena = uiState.contrasena,
                )
                val actualizado = api.updateAdminProfile(body)

                uiState = uiState.copy(
                    nombre = actualizado.nombre,
                    correo = actualizado.correo,
                    contrasena = actualizado.contrasena,
                    saving = false,
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
