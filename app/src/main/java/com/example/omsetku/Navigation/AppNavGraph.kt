package com.example.omsetku.Navigation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.omsetku.ui.screen.*
import com.example.omsetku.viewmodels.CartViewModel
import com.example.omsetku.viewmodels.TaxViewModel
import com.example.omsetku.viewmodels.BusinessViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    cartViewModel: CartViewModel,
    taxViewModel: TaxViewModel
) {
    // Inisialisasi BusinessViewModel
    val businessViewModel: BusinessViewModel = viewModel()

    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen(navController)
        }
        composable(Routes.LOGIN) {
            LoginScreen(navController)
        }
        composable(Routes.SIGNUP) {
            SignUpScreen(navController)
        }
        composable(Routes.OTP) {
            OTPScreen(navController)
        }
        composable(Routes.PERSONAL_DATA) {
            PersonalDataScreen(navController)
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
            TransactionDetailScreen(
                navController = navController,
                cartViewModel = cartViewModel,
                taxViewModel = taxViewModel
            )
        }
        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(navController)
        }
        composable(Routes.BUSINESS_INFO) {
            BusinessInfoScreen(navController, businessViewModel = businessViewModel)
        }
        composable(Routes.TAX_SETTINGS) {
            TaxSettingsScreen(navController, taxViewModel = taxViewModel)
        }
        composable(Routes.CHANGE_PASSWORD) {
            ChangePasswordScreen(navController)
        }
        composable(Routes.NOTIFICATIONS) {
            NotificationScreen(navController)
        }
        composable(Routes.HELP_CENTER) {
            HelpCenterScreen(navController)
        }
    }
}
