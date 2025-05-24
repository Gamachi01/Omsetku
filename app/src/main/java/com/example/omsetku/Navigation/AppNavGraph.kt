package com.example.omsetku.Navigation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.omsetku.ui.screen.*
import com.example.omsetku.viewmodels.CartViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    cartViewModel: CartViewModel
) {
    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(navController)
        }
        composable(Routes.SIGNUP) {
            RegisterScreen(navController)
        }
        composable(Routes.OTP) {
            OTPScreen(navController)
        }
        composable(Routes.BUSINESS_SETUP) {
            BusinessSetupScreen(navController)
        }
        composable(Routes.BUSINESS_FORM) {
            BusinessFormScreen(navController)
        }
        composable(Routes.HOME) {
            HomeScreen(navController)
        }
        composable(Routes.PROFILE) {
            ProfileScreen(navController)
        }
        composable(Routes.TRANSACTION) {
            TransactionScreen(navController)
        }
        composable(Routes.CASHIER) {
            CashierScreen(navController, cartViewModel = cartViewModel)
        }
        composable(Routes.HPP) {
            HppScreen(navController)
        }
        composable(Routes.REPORT) {
            ReportScreen(navController)
        }
        composable(Routes.TRANSACTION_DETAIL) {
            TransactionDetailScreen(navController, cartViewModel = cartViewModel)
        }
        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(navController)
        }
        composable(Routes.BUSINESS_INFO) {
            BusinessInfoScreen(navController)
        }
        composable(Routes.TAX_SETTINGS) {
            TaxSettingsScreen(navController)
        }
    }
}
