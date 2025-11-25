package com.example.blockfile.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.blockfile.core.ui.theme.BlockFileTheme  // si la necesitas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProfileScreen(
    viewModel: AdminProfileViewModel,
    idUsuario: Long,                    // lo pasas desde el login o prefs
    onLogout: () -> Unit,
) {
    val state = viewModel.uiState

    LaunchedEffect(idUsuario) {
        viewModel.loadInitial(idUsuario)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header similar al HeaderAdministrador.html
        TopAppBar(
            title = {
                Text(
                    text = "BlockFile - Admin",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            },
            actions = {
                TextButton(onClick = onLogout) {
                    Text("Cerrar sesión")
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Perfil del Administrador",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (state.loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // Errores (igual que card "Errores" en web)
            state.error?.let { err ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Errores:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = err,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Mensaje de éxito (success)
            state.success?.let { msg ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = msg,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            AdminPerfilFormCard(
                state = state,
                onNombreChange = viewModel::onNombreChange,
                onCorreoChange = viewModel::onCorreoChange,
                onContrasenaChange = viewModel::onContrasenaChange,
                onGuardar = viewModel::guardarCambios,
            )
        }
    }
}

/**
 * Card que replica el formulario de PerfilAdministrador.html:
 * - ID deshabilitado
 * - Nombre, Correo, Contraseña con readonly y modo edición
 * - Botones: Editar información / Guardar / Cancelar
 */
@Composable
private fun AdminPerfilFormCard(
    state: AdminProfileUiState,
    onNombreChange: (String) -> Unit,
    onCorreoChange: (String) -> Unit,
    onContrasenaChange: (String) -> Unit,
    onGuardar: () -> Unit,
) {
    val id = state.idUsuario ?: return

    var isEditing by remember { mutableStateOf(false) }

    // Valores originales para Cancelar
    var originalNombre by remember { mutableStateOf(state.nombre) }
    var originalCorreo by remember { mutableStateOf(state.correo) }
    var originalPass by remember { mutableStateOf(state.contrasena) }

    // Cuando hay éxito, volvemos a modo lectura y actualizamos originales
    LaunchedEffect(state.success) {
        if (state.success != null) {
            isEditing = false
            originalNombre = state.nombre
            originalCorreo = state.correo
            originalPass = state.contrasena
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // ID (disabled)
            OutlinedTextField(
                value = id.toString(),
                onValueChange = {},
                label = { Text("ID") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            )

            // Nombre
            OutlinedTextField(
                value = state.nombre,
                onValueChange = { if (isEditing) onNombreChange(it) },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditing && !state.saving
            )

            // Correo
            OutlinedTextField(
                value = state.correo,
                onValueChange = { if (isEditing) onCorreoChange(it) },
                label = { Text("Correo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditing && !state.saving
            )

            // Contraseña
            OutlinedTextField(
                value = state.contrasena,
                onValueChange = { if (isEditing) onContrasenaChange(it) },
                label = { Text("Contraseña") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditing && !state.saving
            )

            // Botones estilo PerfilAdministrador.html
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (!isEditing) {
                    Button(
                        onClick = {
                            isEditing = true
                            originalNombre = state.nombre
                            originalCorreo = state.correo
                            originalPass = state.contrasena
                        },
                        enabled = !state.saving
                    ) {
                        Text("Editar")
                    }
                } else {

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { onGuardar() },
                            enabled = !state.saving
                        ) {
                            if (state.saving) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .padding(end = 4.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                            Text("Guardar")
                        }

                        OutlinedButton(
                            onClick = {
                                onNombreChange(originalNombre)
                                onCorreoChange(originalCorreo)
                                onContrasenaChange(originalPass)
                                isEditing = false
                            },
                            enabled = !state.saving
                        ) {
                            Text("Cancelar")
                        }
                    }
                }
            }
        }
    }
}
