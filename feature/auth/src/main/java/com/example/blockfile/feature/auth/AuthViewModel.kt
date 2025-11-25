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
    val correo: String = "",
    val contrasena: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val tipoUsuario: String? = null,   // "cliente" o "administrador"
    val idUsuario: Long? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {

    var loginState by mutableStateOf(AuthUiState())
        private set

    var registerState by mutableStateOf(AuthUiState())
        private set

    // ====== LOGIN ======
    fun onLoginNombreChange(value: String) {
        loginState = loginState.copy(nombre = value.take(10), error = null)
    }

    fun onLoginContrasenaChange(value: String) {
        loginState = loginState.copy(contrasena = value, error = null)
    }

    fun login(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            loginState = loginState.copy(loading = true, error = null)
            try {
                val res = loginUseCase(loginState.nombre, loginState.contrasena)

                // Guardamos info básica del usuario logueado
                loginState = loginState.copy(
                    loading = false,
                    success = true,
                    tipoUsuario = res.tipo,
                    idUsuario = res.id_usuario
                )

                // notificamos el tipo al NavHost
                onSuccess(res.tipo)
            } catch (e: Exception) {
                loginState = loginState.copy(
                    loading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    // ====== REGISTER ======
    fun onRegisterNombreChange(value: String) {
        registerState = registerState.copy(nombre = value.take(10), error = null)
    }

    fun onRegisterCorreoChange(value: String) {
        registerState = registerState.copy(correo = value, error = null)
    }

    fun onRegisterContrasenaChange(value: String) {
        registerState = registerState.copy(contrasena = value, error = null)
    }

    fun register(onSuccess: () -> Unit) {
        viewModelScope.launch {
            registerState = registerState.copy(loading = true, error = null)
            try {
                val res = registerUseCase(
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
