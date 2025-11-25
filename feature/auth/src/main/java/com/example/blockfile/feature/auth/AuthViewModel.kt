// feature/auth/src/main/java/com/example/blockfile/feature/auth/AuthViewModel.kt
package com.example.blockfile.feature.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blockfile.core.domain.auth.LoginUseCase
import com.example.blockfile.core.domain.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val nombre: String = "",
    val contrasena: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val idUsuario: Long? = null,
    val esAdmin: Boolean = false,
)

data class RegisterUiState(
    val nombre: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {

    var loginState by mutableStateOf(AuthUiState())
        private set

    var registerState by mutableStateOf(RegisterUiState())
        private set

    fun onLoginNombreChange(value: String) {
        loginState = loginState.copy(nombre = value, error = null)
    }

    fun onLoginContrasenaChange(value: String) {
        loginState = loginState.copy(contrasena = value, error = null)
    }

    fun login(onSuccess: (esAdmin: Boolean) -> Unit) {
        if (loginState.loading) return
        viewModelScope.launch {
            loginState = loginState.copy(loading = true, error = null)
            try {
                val resp = loginUseCase(loginState.nombre, loginState.contrasena)
                loginState = loginState.copy(
                    loading = false,
                    idUsuario = resp.id_usuario,
                    esAdmin = resp.es_admin,
                )
                onSuccess(resp.es_admin)
            } catch (e: Exception) {
                loginState = loginState.copy(
                    loading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    // --------- registro (igual que ya tenías, adaptado) ---------

    fun onRegisterNombreChange(value: String) {
        registerState = registerState.copy(nombre = value, error = null)
    }

    fun onRegisterCorreoChange(value: String) {
        registerState = registerState.copy(correo = value, error = null)
    }

    fun onRegisterContrasenaChange(value: String) {
        registerState = registerState.copy(contrasena = value, error = null)
    }

    fun register(onSuccess: () -> Unit) {
        if (registerState.loading) return
        viewModelScope.launch {
            registerState = registerState.copy(loading = true, error = null)
            try {
                registerUseCase(
                    registerState.nombre,
                    registerState.correo,
                    registerState.contrasena
                )
                registerState = registerState.copy(loading = false, success = true)
                onSuccess()
            } catch (e: Exception) {
                registerState = registerState.copy(
                    loading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }
}
