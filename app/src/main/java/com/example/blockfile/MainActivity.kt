package com.example.blockfile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.blockfile.core.ui.theme.BlockFileTheme
import com.example.blockfile.feature.auth.AuthViewModel
import com.example.blockfile.feature.auth.LoginScreen
import com.example.blockfile.feature.auth.RegisterScreen
import dagger.hilt.android.AndroidEntryPoint

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
    // luego agregarás Home, etc.
}

@Composable
fun BlockFileNavHost(
    navController: NavHostController,
) {
    val authViewModel: AuthViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
    ) {
        composable(Routes.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onGoToRegister = {
                    navController.navigate(Routes.Register.route)
                },
                onLoginSuccess = {
                    // TODO: navegar al Home del catálogo cuando exista
                    // navController.navigate("home") { popUpTo(Routes.Login.route) { inclusive = true } }
                }
            )
        }

        composable(Routes.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onGoToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    // luego de registro exitoso puedes volver al login
                    navController.popBackStack()
                }
            )
        }
    }
}
