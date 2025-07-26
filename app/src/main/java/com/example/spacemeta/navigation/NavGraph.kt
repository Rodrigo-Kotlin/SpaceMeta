package com.example.spacemeta.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.spacemeta.screens.PeriodoSelectionScreen
import com.example.spacemeta.screens.ControleAScreen
import com.example.spacemeta.screens.*

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "periodoSelection") {
        composable("periodoSelection") { PeriodoSelectionScreen(navController) }
        composable("controle/{periodo}") { backStackEntry ->
            ControleAScreen(periodo = backStackEntry.arguments?.getString("periodo") ?: "")
        }
    }
}
