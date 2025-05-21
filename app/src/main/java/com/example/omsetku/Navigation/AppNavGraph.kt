package com.example.omsetku.Navigation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.omsetku.ui.screen.*

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(navController)
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
            CashierScreen(navController)
        }
        composable(Routes.HPP) {
            HppScreen(navController)
        }
        composable(Routes.REPORT) {
            ReportScreen(navController)
        }
        composable(Routes.TRANSACTION_DETAIL) {
            TransactionDetailScreen(navController)
        }
        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(navController)
        }
        composable(Routes.BUSINESS_INFO) {
            BusinessInfoScreen(navController)
        }
    }
}
