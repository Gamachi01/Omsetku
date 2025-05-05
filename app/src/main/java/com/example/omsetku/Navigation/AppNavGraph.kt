package com.example.omsetku.Navigation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.omsetku.ui.screen.HomeScreen
import com.example.omsetku.ui.screen.ProfileScreen
import com.example.omsetku.ui.screen.TransactionScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(navController)
        }
        composable(Routes.PROFILE) {
            ProfileScreen(navController)
        }
        composable(Routes.TRANSACTION) {
            TransactionScreen(navController)
        }
    }
}
