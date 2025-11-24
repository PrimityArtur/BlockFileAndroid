package com.example.blockfile.feature.rankings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blockfile.core.data.repository.*
import com.example.blockfile.core.domain.rankings.*
import com.example.blockfile.core.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class RankingTab { PRODUCTOS_MAS_COMPRADOS, MEJORES_COMPRADORES, PRODUCTOS_MEJOR_CALIFICADOS }

data class RankingTabState<T>(
    val items: List<T> = emptyList(),
    val page: Int = 1,
    val totalPages: Int = 1,
    val loading: Boolean = false,
    val error: String? = null,
)

data class RankingsUiState(
    val selectedTab: RankingTab = RankingTab.PRODUCTOS_MAS_COMPRADOS,
    val pmc: RankingTabState<ProductoMasComprado> = RankingTabState(),
    val mc: RankingTabState<MejorComprador> = RankingTabState(),
    val pmcal: RankingTabState<ProductoMejorCalificado> = RankingTabState(),
)

@HiltViewModel
class RankingsViewModel @Inject constructor(
    private val getPMC: GetProductosMasCompradosUseCase,
    private val getMC: GetMejoresCompradoresUseCase,
    private val getPMCal: GetProductosMejorCalificadosUseCase,
) : ViewModel() {

    var uiState by mutableStateOf(RankingsUiState())
        private set

    fun selectTab(tab: RankingTab) {
        uiState = uiState.copy(selectedTab = tab)
        when (tab) {
            RankingTab.PRODUCTOS_MAS_COMPRADOS ->
                if (uiState.pmc.items.isEmpty()) loadPMC(1)
            RankingTab.MEJORES_COMPRADORES ->
                if (uiState.mc.items.isEmpty()) loadMC(1)
            RankingTab.PRODUCTOS_MEJOR_CALIFICADOS ->
                if (uiState.pmcal.items.isEmpty()) loadPMCal(1)
        }
    }

    fun loadPMC(page: Int) {
        viewModelScope.launch {
            uiState = uiState.copy(pmc = uiState.pmc.copy(loading = true, error = null))
            try {
                val res = getPMC(page)
                uiState = uiState.copy(
                    pmc = uiState.pmc.copy(
                        items = res.items,
                        page = res.page,
                        totalPages = res.totalPages,
                        loading = false,
                    )
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    pmc = uiState.pmc.copy(
                        loading = false,
                        error = e.message ?: "Error desconocido"
                    )
                )
            }
        }
    }

    fun loadMC(page: Int) {
        viewModelScope.launch {
            uiState = uiState.copy(mc = uiState.mc.copy(loading = true, error = null))
            try {
                val res = getMC(page)
                uiState = uiState.copy(
                    mc = uiState.mc.copy(
                        items = res.items,
                        page = res.page,
                        totalPages = res.totalPages,
                        loading = false,
                    )
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    mc = uiState.mc.copy(
                        loading = false,
                        error = e.message ?: "Error desconocido"
                    )
                )
            }
        }
    }

    fun loadPMCal(page: Int) {
        viewModelScope.launch {
            uiState = uiState.copy(pmcal = uiState.pmcal.copy(loading = true, error = null))
            try {
                val res = getPMCal(page)
                uiState = uiState.copy(
                    pmcal = uiState.pmcal.copy(
                        items = res.items,
                        page = res.page,
                        totalPages = res.totalPages,
                        loading = false,
                    )
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    pmcal = uiState.pmcal.copy(
                        loading = false,
                        error = e.message ?: "Error desconocido"
                    )
                )
            }
        }
    }
}
