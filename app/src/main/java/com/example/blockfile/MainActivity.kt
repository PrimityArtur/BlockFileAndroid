package com.example.blockfile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.blockfile.core.ui.theme.BlockFileTheme
import com.example.blockfile.feature.auth.AuthViewModel
import com.example.blockfile.feature.auth.LoginScreen
import com.example.blockfile.feature.auth.RegisterScreen
import com.example.blockfile.feature.catalog.CatalogScreen
import com.example.blockfile.feature.catalog.CatalogViewModel
import com.example.blockfile.feature.productdetail.ProductDetailScreen
import com.example.blockfile.feature.productdetail.ProductDetailViewModel
import com.example.blockfile.feature.profile.ProfileScreen
import com.example.blockfile.feature.profile.ProfileViewModel
import com.example.blockfile.feature.rankings.RankingsScreen
import com.example.blockfile.feature.rankings.RankingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.blockfile.feature.profile.AdminProfileScreen
import com.example.blockfile.feature.profile.AdminProfileViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BlockFileTheme {
                val navController = rememberNavController()
                BlockFileNavHost(navController)
            }
        }
    }
}

sealed class Routes(val route: String) {
    data object Login : Routes("login")
    data object Register : Routes("register")
}

// =========================================================
//                      NAVIGATION
// =========================================================
@Composable
fun BlockFileNavHost(
    navController: NavHostController,
) {
    val authViewModel: AuthViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
    ) {

        // ============= LOGIN =============
        composable(Routes.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onGoToRegister = {
                    navController.navigate(Routes.Register.route)
                },
                onLoginSuccess = { tipoUsuario ->
                    if (tipoUsuario == "administrador") {
                        navController.navigate("adminHome") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        navController.navigate("catalog") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            )
        }

        // ============= REGISTER =============
        composable(Routes.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onGoToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.popBackStack()
                }
            )
        }

        // ============= CATALOGO =============
        composable("catalog") {
            val vm: CatalogViewModel = hiltViewModel()
            CatalogScreen(
                viewModel = vm,
                onGoHome = {},
                onGoRanking = { navController.navigate("ranking") },
                onGoPerfil = { navController.navigate("perfil") },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("catalog") { inclusive = true }
                    }
                },
                onProductClick = { productId ->
                    navController.navigate("productdetail/$productId")
                }
            )
        }

        // ============= RANKINGS =============
        composable("ranking") {
            val vm: RankingsViewModel = hiltViewModel()
            RankingsScreen(
                viewModel = vm,
                onGoHome = { navController.navigate("catalog") },
                onGoPerfil = { navController.navigate("perfil") },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("ranking") { inclusive = true }
                    }
                },
                onProductClick = { productId ->
                    navController.navigate("productdetail/$productId")
                }
            )
        }

        // ============= PRODUCT DETAIL =============
        composable(
            route = "productdetail/{productId}",
            arguments = listOf(
                navArgument("productId") { type = NavType.LongType }
            )
        ) {
            val vm: ProductDetailViewModel = hiltViewModel()
            ProductDetailScreen(
                viewModel = vm,
                onGoHome = { navController.navigate("catalog") },
                onGoRanking = { navController.navigate("ranking") },
                onGoPerfil = { navController.navigate("perfil") },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ============= PERFIL CLIENTE =============
        composable("perfil") {
            val vm: ProfileViewModel = hiltViewModel()
            ProfileScreen(
                viewModel = vm,
                onGoHome = { navController.navigate("catalog") },
                onGoRankings = { navController.navigate("ranking") },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onProductClick = { productId ->
                    navController.navigate("productdetail/$productId")
                }
            )
        }

        // ============= PERFIL / PANEL ADMIN =============
        composable("adminHome") {
            val vm: AdminProfileViewModel = hiltViewModel()

            val authVm: AuthViewModel = hiltViewModel()
            val idUsuario = authVm.loginState.idUsuario ?: 0L

            AdminProfileScreen(
                viewModel = vm,
                idUsuario = idUsuario,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
