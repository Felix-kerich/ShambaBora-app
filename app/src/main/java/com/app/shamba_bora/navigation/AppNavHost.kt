package com.app.shamba_bora.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
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
import com.app.shamba_bora.ui.screens.messaging.ConversationListScreen
import com.app.shamba_bora.ui.screens.messaging.ChatScreen
import com.app.shamba_bora.ui.screens.messaging.GroupChatScreen
import com.app.shamba_bora.ui.screens.profile.ProfileScreen
import com.app.shamba_bora.ui.screens.profile.FarmerProfileScreen
import com.app.shamba_bora.ui.screens.profile.EditProfileScreen
import com.app.shamba_bora.ui.screens.profile.SettingsScreen

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
        
        // Messaging Screens
        composable(Screen.Messages.route) {
            ConversationListScreen(
                onNavigateToChat = { otherUserId ->
                    navController.navigate("chat/$otherUserId")
                },
                onNavigateToGroups = { navController.navigate(Screen.Groups.route) }
            )
        }
        
        composable(
            route = "chat/{otherUserId}",
            arguments = listOf(navArgument("otherUserId") { type = NavType.LongType })
        ) { backStackEntry ->
            val otherUserId = backStackEntry.arguments?.getLong("otherUserId") ?: 0L
            ChatScreen(
                otherUserId = otherUserId,
                otherUserName = "User $otherUserId", // TODO: Fetch actual username
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = "group_chat/{groupId}",
            arguments = listOf(navArgument("groupId") { type = NavType.LongType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getLong("groupId") ?: 0L
            GroupChatScreen(
                groupId = groupId,
                groupName = "Group $groupId", // TODO: Fetch actual group name
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Profile Screens
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToFarmerProfile = { navController.navigate(Screen.FarmerProfile.route) },
                onNavigateToEditProfile = { navController.navigate("edit_profile/user") },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        
        composable(Screen.FarmerProfile.route) {
            FarmerProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate("edit_profile/farmer") }
            )
        }
        
        composable(
            route = "edit_profile/{type}",
            arguments = listOf(navArgument("type") { type = NavType.StringType })
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "user"
            EditProfileScreen(
                isFarmerProfile = type == "farmer",
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

