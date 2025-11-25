package com.example.blockfile.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProfileScreen(
    viewModel: AdminProfileViewModel,
    idUsuario: Long,
    onGoPerfil: () -> Unit,
    onGoInventario: () -> Unit,
    onGoCategorias: () -> Unit,
    onGoUsuarios: () -> Unit,
    onLogout: () -> Unit,
) {
    val state = viewModel.uiState
    var isEditing by remember { mutableStateOf(false) }

    var nombre by remember(state.nombre) { mutableStateOf(state.nombre) }
    var correo by remember(state.correo) { mutableStateOf(state.correo) }
    var contrasena by remember(state.contrasena) { mutableStateOf(state.contrasena) }

    LaunchedEffect(idUsuario) {
        if (idUsuario != 0L) {
            viewModel.loadProfile(idUsuario)
        }
    }

    // Sincronizar cuando el ViewModel se actualice
    LaunchedEffect(state.idUsuario, state.nombre, state.correo, state.contrasena) {
        nombre = state.nombre
        correo = state.correo
        contrasena = state.contrasena
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // ===== HEADER ARRIBA (similar a ProfileScreen, pero con opciones de admin) =====
        TopAppBar(
            title = {
                Text(
                    text = "BlockFile",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            },
            actions = {
                TextButton(onClick = onGoPerfil) {
                    Text("Perfil")
                }
                TextButton(onClick = onGoInventario) {
                    Text("Inventario")
                }
                TextButton(onClick = onGoCategorias) {
                    Text("Categorías")
                }
                TextButton(onClick = onGoUsuarios) {
                    Text("Usuarios")
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {

            item {
                // Título principal
                Text(
                    text = "Perfil del Administrador",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (state.loading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                // Bloque de errores tipo card (similar al perfil cliente)
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

                // Mensaje de éxito sencillo debajo del título
                state.success?.let { msg ->
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // ===== CARD DE DATOS DEL ADMIN (similar a PerfilFormCard) =====
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
                        OutlinedTextField(
                            value = state.idUsuario?.toString() ?: "",
                            onValueChange = {},
                            label = { Text("ID") },
                            enabled = false,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { if (isEditing) nombre = it },
                            label = { Text("Nombre de usuario") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditing && !state.saving,
                        )

                        OutlinedTextField(
                            value = correo,
                            onValueChange = { if (isEditing) correo = it },
                            label = { Text("Correo") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditing && !state.saving,
                        )

                        OutlinedTextField(
                            value = contrasena,
                            onValueChange = { if (isEditing) contrasena = it },
                            label = { Text("Contraseña") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditing && !state.saving,
                        )

                        // Botones Editar / Guardar / Cancelar alineados a la derecha
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            if (!isEditing) {
                                Button(
                                    onClick = { isEditing = true },
                                    enabled = !state.loading && state.idUsuario != null
                                ) {
                                    Text("Editar")
                                }
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.onNombreChange(nombre)
                                            viewModel.onCorreoChange(correo)
                                            viewModel.onContrasenaChange(contrasena)
                                            viewModel.saveProfile()
                                            isEditing = false
                                        },
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
                                            // Revertir a valores del ViewModel
                                            nombre = state.nombre
                                            correo = state.correo
                                            contrasena = state.contrasena
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

                Spacer(modifier = Modifier.height(8.dp))

                // ===== Botón rojo de cerrar sesión (lo mantenemos) =====
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar sesión")
                }
            }
        }
    }
}
