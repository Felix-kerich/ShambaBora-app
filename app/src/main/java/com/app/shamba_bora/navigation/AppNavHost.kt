package com.app.shamba_bora.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.shamba_bora.ui.screens.auth.LoginScreen
import com.app.shamba_bora.ui.screens.auth.RegisterScreen
import com.app.shamba_bora.ui.screens.chatbot.ChatbotScreen
import com.app.shamba_bora.ui.screens.collaboration.CollaborationScreen
import com.app.shamba_bora.ui.screens.dashboard.DashboardScreen
import com.app.shamba_bora.ui.screens.marketplace.MarketplaceScreen
import com.app.shamba_bora.ui.screens.records.RecordsScreen
import com.app.shamba_bora.ui.screens.farm.ActivitiesScreen
import com.app.shamba_bora.ui.screens.farm.ExpensesScreen
import com.app.shamba_bora.ui.screens.farm.YieldsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Auth Screens
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                } },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Register.route) { inclusive = true }
                } },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        
        // Main Bottom Navigation Screens
        composable(Screen.Home.route) {
            DashboardScreen(
                onNavigateToActivities = { navController.navigate(Screen.Activities.route) },
                onNavigateToExpenses = { navController.navigate(Screen.Expenses.route) },
                onNavigateToYields = { navController.navigate(Screen.Yields.route) },
                onNavigateToWeather = { navController.navigate(Screen.Weather.route) }
            )
        }
        
        composable(Screen.Marketplace.route) {
            MarketplaceScreen(
                onNavigateToProductDetails = { productId ->
                    navController.navigate(Screen.ProductDetails.createRoute(productId))
                }
            )
        }
        
        composable(Screen.Collaboration.route) {
            CollaborationScreen(
                onNavigateToPostDetails = { postId ->
                    navController.navigate(Screen.PostDetail.createRoute(postId))
                },
                onNavigateToGroups = { navController.navigate(Screen.Groups.route) },
                onNavigateToMessages = { navController.navigate(Screen.Messages.route) }
            )
        }
        
        composable(Screen.Records.route) {
            RecordsScreen(
                onNavigateToActivities = { navController.navigate(Screen.Activities.route) },
                onNavigateToExpenses = { navController.navigate(Screen.Expenses.route) },
                onNavigateToYields = { navController.navigate(Screen.Yields.route) }
            )
        }
        
        composable(Screen.Chatbot.route) {
            ChatbotScreen()
        }
        
        // Record Keeping Screens
        composable(Screen.Activities.route) {
            ActivitiesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Expenses.route) {
            ExpensesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Yields.route) {
            YieldsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // TODO: Add other screens (Profile, Settings, Weather, etc.)
    }
}

