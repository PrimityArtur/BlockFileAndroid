package com.example.blockfile.feature.rankings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.blockfile.core.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingsScreen(
    viewModel: RankingsViewModel,
    onGoHome: () -> Unit,
    onGoPerfil: () -> Unit,
    onLogout: () -> Unit,
    onProductClick: (Long) -> Unit,
) {
    val state = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.selectTab(RankingTab.PRODUCTOS_MAS_COMPRADOS)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "BlockFile",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                },
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
                        onPageChange = { viewModel.loadPMC(it) },
                        onProductClick = onProductClick,
                    )

                RankingTab.MEJORES_COMPRADORES ->
                    RankingMejoresCompradoresSection(
                        tabState = state.mc,
                        onPageChange = { viewModel.loadMC(it) }
                    )

                RankingTab.PRODUCTOS_MEJOR_CALIFICADOS ->
                    RankingProductosMejorCalificadosSection(
                        tabState = state.pmcal,
                        onPageChange = { viewModel.loadPMCal(it) },
                        onProductClick = onProductClick,
                    )
            }
        }
    }
}

/* --------------------------------------------------------------------- */
/*  COMPONENTES GENERALES PARA “TABLAS”                                  */
/* --------------------------------------------------------------------- */

@Composable
private fun TableHeader(
    headers: List<String>,
    weights: List<Float>,
) {
    Surface(
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            headers.forEachIndexed { index, title ->
                Text(
                    text = title,
                    modifier = Modifier
                        .weight(weights[index])
                        .padding(horizontal = 6.dp), // más espacio lateral
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center // <-- CENTRADO TOTAL
                )
            }
        }
    }
}


@Composable
private fun TableRowText(
    texts: List<String>,
    weights: List<Float>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 30.dp, horizontal = 16.dp), // MÁS ESPACIO ENTRE CELDAS
        verticalAlignment = Alignment.CenterVertically
    ) {
        texts.forEachIndexed { index, value ->
            Text(
                text = value,
                modifier = Modifier
                    .weight(weights[index])
                    .padding(horizontal = 6.dp), // separación interna
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center, // <-- CENTRADO TOTAL
                softWrap = true,
                maxLines = Int.MAX_VALUE
            )
        }
    }
}


/* --------------------------------------------------------------------- */
/*  PROD. MÁS COMPRADOS                                                  */
/* --------------------------------------------------------------------- */

@Composable
private fun RankingProductosMasCompradosSection(
    tabState: RankingTabState<ProductoMasComprado>,
    onPageChange: (Int) -> Unit,
    onProductClick: (Long) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        if (tabState.loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        tabState.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
        }

        // Pesos de cada columna
        val weights = listOf(0.3f, 2.2f, 1.6f, 1.6f, 1.1f, 1.1f)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            // HEADER
            TableHeader(
                headers = listOf("#", "Producto", "Autor", "Categoría", "Precio", "Compras"),
                weights = weights,
            )

            Divider()

            // FILAS
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(tabState.items) { item ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onProductClick(item.id) },
                    ) {
                        TableRowText(
                            texts = listOf(
                                item.top.toString(),
                                item.nombre,
                                item.autor.ifBlank { "-" },
                                item.categoria.ifBlank { "-" },
                                item.precio?.let { "S/ $it" } ?: "-",
                                item.compras.toString()
                            ),
                            weights = weights
                        )
                    }
                    Divider()
                }
            }
        }

        PagerControls(tabState.page, tabState.totalPages, onPageChange)
    }
}

/* --------------------------------------------------------------------- */
/*  MEJORES COMPRADORES                                                  */
/* --------------------------------------------------------------------- */

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

        val weights = listOf(0.8f, 2.6f, 1.2f)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            TableHeader(
                headers = listOf("#", "Cliente", "N.º compras"),
                weights = weights
            )

            Divider()

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(tabState.items) { item ->
                    Surface(modifier = Modifier.fillMaxWidth()) {
                        TableRowText(
                            texts = listOf(
                                item.top.toString(),
                                item.nombre,
                                item.compras.toString()
                            ),
                            weights = weights
                        )
                    }
                    Divider()
                }
            }
        }

        PagerControls(tabState.page, tabState.totalPages, onPageChange)
    }
}

/* --------------------------------------------------------------------- */
/*  PROD. MEJOR CALIFICADOS                                              */
/* --------------------------------------------------------------------- */

@Composable
private fun RankingProductosMejorCalificadosSection(
    tabState: RankingTabState<ProductoMejorCalificado>,
    onPageChange: (Int) -> Unit,
    onProductClick: (Long) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        if (tabState.loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        tabState.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
        }

        val weights = listOf(0.6f, 2.0f, 1.4f, 1.4f, 1.0f, 1.0f, 1.0f)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            TableHeader(
                headers = listOf(
                    "#",
                    "Producto",
                    "Autor",
                    "Categoría",
                    "Precio",
                    "Calif.",
                    "Prom."
                ),
                weights = weights
            )

            Divider()

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(tabState.items) { item ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onProductClick(item.id) },
                    ) {
                        TableRowText(
                            texts = listOf(
                                item.top.toString(),
                                item.nombre,
                                item.autor.ifBlank { "-" },
                                item.categoria.ifBlank { "-" },
                                item.precio?.let { "S/ $it" } ?: "-",
                                item.numCalificaciones.toString(),
                                item.promedio.toString()
                            ),
                            weights = weights
                        )
                    }
                    Divider()
                }
            }
        }

        PagerControls(tabState.page, tabState.totalPages, onPageChange)
    }
}

/* --------------------------------------------------------------------- */
/*  PAGINACIÓN (igual que antes)                                        */
/* --------------------------------------------------------------------- */

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
