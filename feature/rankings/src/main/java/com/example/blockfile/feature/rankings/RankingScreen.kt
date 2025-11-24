package com.example.blockfile.feature.rankings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.blockfile.core.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingsScreen(
    viewModel: RankingsViewModel,
    onGoHome: () -> Unit,
    onGoPerfil: () -> Unit,
    onLogout: () -> Unit,
) {
    val state = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.selectTab(RankingTab.PRODUCTOS_MAS_COMPRADOS)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rankings") },
                actions = {
                    TextButton(onClick = onGoHome) { Text("Inicio") }
                    TextButton(onClick = { /* ya estás en ranking */ }) { Text("Ranking") }
                    TextButton(onClick = onGoPerfil) { Text("Perfil") }
                    TextButton(onClick = onLogout) { Text("Cerrar sesión") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // ===== TABS =====
            TabRow(selectedTabIndex = state.selectedTab.ordinal) {
                Tab(
                    selected = state.selectedTab == RankingTab.PRODUCTOS_MAS_COMPRADOS,
                    onClick = { viewModel.selectTab(RankingTab.PRODUCTOS_MAS_COMPRADOS) },
                    text = { Text("Prod. más comprados") },
                )
                Tab(
                    selected = state.selectedTab == RankingTab.MEJORES_COMPRADORES,
                    onClick = { viewModel.selectTab(RankingTab.MEJORES_COMPRADORES) },
                    text = { Text("Mejores compradores") },
                )
                Tab(
                    selected = state.selectedTab == RankingTab.PRODUCTOS_MEJOR_CALIFICADOS,
                    onClick = { viewModel.selectTab(RankingTab.PRODUCTOS_MEJOR_CALIFICADOS) },
                    text = { Text("Prod. mejor calificados") },
                )
            }

            when (state.selectedTab) {
                RankingTab.PRODUCTOS_MAS_COMPRADOS ->
                    RankingProductosMasCompradosSection(
                        tabState = state.pmc,
                        onPageChange = { viewModel.loadPMC(it) }
                    )

                RankingTab.MEJORES_COMPRADORES ->
                    RankingMejoresCompradoresSection(
                        tabState = state.mc,
                        onPageChange = { viewModel.loadMC(it) }
                    )

                RankingTab.PRODUCTOS_MEJOR_CALIFICADOS ->
                    RankingProductosMejorCalificadosSection(
                        tabState = state.pmcal,
                        onPageChange = { viewModel.loadPMCal(it) }
                    )
            }
        }
    }
}

@Composable
private fun RankingProductosMasCompradosSection(
    tabState: RankingTabState<ProductoMasComprado>,
    onPageChange: (Int) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        if (tabState.loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        tabState.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            items(tabState.items) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Top ${item.top}", fontWeight = FontWeight.Bold)
                        Text(item.nombre, fontWeight = FontWeight.SemiBold)
                        Text("Autor: ${item.autor.ifBlank { "-" }}")
                        Text("Categoría: ${item.categoria.ifBlank { "-" }}")
                        Text("Precio: ${item.precio?.let { "S/ $it" } ?: "-"}")
                        Text("Compras: ${item.compras}")
                    }
                }
            }
        }

        PagerControls(tabState.page, tabState.totalPages, onPageChange)
    }
}

@Composable
private fun RankingMejoresCompradoresSection(
    tabState: RankingTabState<MejorComprador>,
    onPageChange: (Int) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        if (tabState.loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        tabState.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            items(tabState.items) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Top ${item.top}", fontWeight = FontWeight.Bold)
                        Text(item.nombre, fontWeight = FontWeight.SemiBold)
                        Text("N.º de compras: ${item.compras}")
                    }
                }
            }
        }

        PagerControls(tabState.page, tabState.totalPages, onPageChange)
    }
}

@Composable
private fun RankingProductosMejorCalificadosSection(
    tabState: RankingTabState<ProductoMejorCalificado>,
    onPageChange: (Int) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        if (tabState.loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        tabState.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            items(tabState.items) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Top ${item.top}", fontWeight = FontWeight.Bold)
                        Text(item.nombre, fontWeight = FontWeight.SemiBold)
                        Text("Autor: ${item.autor.ifBlank { "-" }}")
                        Text("Categoría: ${item.categoria.ifBlank { "-" }}")
                        Text("Precio: ${item.precio?.let { "S/ $it" } ?: "-"}")
                        Text("N.º calificaciones: ${item.numCalificaciones}")
                        Text("Promedio: ${item.promedio}")
                    }
                }
            }
        }

        PagerControls(tabState.page, tabState.totalPages, onPageChange)
    }
}

@Composable
private fun PagerControls(
    page: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(
            onClick = { onPageChange(1) },
            enabled = page > 1,
        ) { Text("« Primero") }

        TextButton(
            onClick = { onPageChange(page - 1) },
            enabled = page > 1,
        ) { Text("‹ Ant") }

        Text("Página $page de $totalPages")

        TextButton(
            onClick = { onPageChange(page + 1) },
            enabled = page < totalPages,
        ) { Text("Sig ›") }

        TextButton(
            onClick = { onPageChange(totalPages) },
            enabled = page < totalPages,
        ) { Text("Último »") }
    }
}
