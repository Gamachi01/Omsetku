package com.example.omsetku.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.omsetku.ui.screen.*
import com.example.omsetku.viewmodels.CartViewModel
import com.example.omsetku.viewmodels.TaxViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    cartViewModel: CartViewModel,
    taxViewModel: TaxViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(route = Routes.LOGIN) {
            LoginScreen(navController = navController)
        }

        composable(route = Routes.HOME) {
            HomeScreen(navController = navController)
        }

        composable(route = Routes.CASHIER) {
            CashierScreen(navController = navController, cartViewModel = cartViewModel)
        }

        composable(route = Routes.TRANSACTION) {
            TransactionScreen(navController = navController)
        }

        composable(route = Routes.HPP) {
            HppScreen(navController = navController)
        }

        composable(route = Routes.PROFILE) {
            ProfileScreen(navController = navController)
        }

        // Detail Transaksi
        composable(route = Routes.TRANSACTION_DETAIL) {
            TransactionDetailScreen(
                navController = navController,
                cartViewModel = cartViewModel,
                taxViewModel = taxViewModel
            )
        }

        composable(route = Routes.TAX_SETTINGS) {
            TaxSettingsScreen(navController = navController, taxViewModel = taxViewModel)
        }
    }
}