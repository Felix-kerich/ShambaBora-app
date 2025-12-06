package com.app.shamba_bora.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.MarketplaceViewModel
import com.app.shamba_bora.ui.screens.auth.LoginScreen
import com.app.shamba_bora.ui.screens.auth.RegisterScreen
import com.app.shamba_bora.utils.PreferenceManager
import com.app.shamba_bora.ui.screens.chatbot.EnhancedChatbotScreen
import com.app.shamba_bora.ui.screens.collaboration.CollaborationScreen
import com.app.shamba_bora.ui.screens.dashboard.DashboardScreen
import com.app.shamba_bora.ui.screens.marketplace.MarketplaceScreen
import com.app.shamba_bora.ui.screens.marketplace.AddProductScreen
import com.app.shamba_bora.ui.screens.marketplace.ProductDetailScreen
import com.app.shamba_bora.ui.screens.marketplace.CheckoutScreen
import com.app.shamba_bora.ui.screens.marketplace.MyProductsScreen
import com.app.shamba_bora.ui.screens.marketplace.OrderListScreen
import com.app.shamba_bora.ui.screens.records.RecordsScreen
import com.app.shamba_bora.ui.screens.records.PatchesScreen
import com.app.shamba_bora.ui.screens.records.CreatePatchScreenWrapper
import com.app.shamba_bora.ui.screens.records.PatchDetailScreenWrapper
import com.app.shamba_bora.ui.screens.records.CreateActivityScreen
import com.app.shamba_bora.ui.screens.records.CreateExpenseScreen
import com.app.shamba_bora.ui.screens.records.CreateYieldScreen
import com.app.shamba_bora.ui.screens.farm.ActivitiesScreen
import com.app.shamba_bora.ui.screens.farm.ActivityDetailScreen
import com.app.shamba_bora.ui.screens.farm.ExpensesScreen
import com.app.shamba_bora.ui.screens.farm.ExpenseDetailScreen
import com.app.shamba_bora.ui.screens.farm.YieldsScreen
import com.app.shamba_bora.ui.screens.farm.YieldDetailScreen
import com.app.shamba_bora.ui.screens.weather.WeatherScreen
import com.app.shamba_bora.ui.screens.messaging.ConversationListScreen
import com.app.shamba_bora.ui.screens.messaging.ChatScreen
import com.app.shamba_bora.ui.screens.messaging.GroupChatScreen
import com.app.shamba_bora.ui.screens.messaging.ConversationScreen
import com.app.shamba_bora.ui.screens.community.PostDetailScreen
import com.app.shamba_bora.ui.screens.community.GroupsScreen
import com.app.shamba_bora.ui.screens.community.GroupDetailScreen
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
                onLoginSuccess = { 
                    // Redirect based on user role
                    val userRoles = PreferenceManager.getUserRoles()
                    val destination = when {
                        userRoles.contains("BUYER") -> Screen.Marketplace.route
                        userRoles.contains("EXTENSION_OFFICER") -> Screen.Collaboration.route
                        else -> Screen.Home.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { 
                    // Check if user is a farmer and needs to create profile
                    val userRoles = PreferenceManager.getUserRoles()
                    if (userRoles.contains("FARMER")) {
                        navController.navigate(Screen.FarmerProfile.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        
        // Main Bottom Navigation Screens
        composable(Screen.Home.route) {
            DashboardScreen(
                onNavigateToActivities = { navController.navigate(Screen.Activities.route) },
                onNavigateToExpenses = { navController.navigate(Screen.Expenses.route) },
                onNavigateToYields = { navController.navigate(Screen.Yields.route) },
                onNavigateToWeather = { navController.navigate(Screen.Weather.route) },
                onNavigateToFarmerProfile = { navController.navigate(Screen.FarmerProfile.route) }
            )
        }
        
        composable(Screen.Marketplace.route) {
            MarketplaceScreen(
                onNavigateToProductDetails = { productId ->
                    navController.navigate(Screen.ProductDetails.createRoute(productId))
                },
                onNavigateToAddProduct = { navController.navigate(Screen.AddProduct.route) },
                onNavigateToMyProducts = { navController.navigate(Screen.MyProducts.route) },
                onNavigateToOrders = { navController.navigate(Screen.Orders.route) }
            )
        }
        
        // Marketplace Detail Screens
        composable(
            route = Screen.ProductDetails.route,
            arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
            ProductDetailScreen(
                productId = productId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCheckout = { product, quantity ->
                    navController.navigate(Screen.Checkout.createRoute(product.id ?: 0L, quantity))
                }
            )
        }
        
        composable(
            route = Screen.Checkout.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.LongType },
                navArgument("quantity") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
            val quantity = backStackEntry.arguments?.getInt("quantity") ?: 1
            
            // Get product from marketplace viewmodel
            val marketplaceViewModel: MarketplaceViewModel = hiltViewModel()
            val productState by marketplaceViewModel.productState.collectAsState()
            
            if (productState is Resource.Success) {
                val product = (productState as Resource.Success).data
                if (product != null) {
                    CheckoutScreen(
                        product = product,
                        quantity = quantity,
                        onNavigateBack = { navController.popBackStack() },
                        onPaymentSuccess = {
                            navController.navigate(Screen.Marketplace.route) {
                                popUpTo(Screen.ProductDetails.route) { inclusive = true }
                            }
                        }
                    )
                }
            } else {
                // Load product if not already loaded
                LaunchedEffect(productId) {
                    marketplaceViewModel.loadProduct(productId)
                }
            }
        }
        
        composable(Screen.AddProduct.route) {
            AddProductScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.EditProduct.route,
            arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
            AddProductScreen(
                productId = productId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.MyProducts.route) {
            MyProductsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddProduct = { navController.navigate(Screen.AddProduct.route) },
                onNavigateToEditProduct = { productId ->
                    navController.navigate(Screen.EditProduct.createRoute(productId))
                }
            )
        }
        
        composable(Screen.Orders.route) {
            OrderListScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Collaboration.route) {
            CollaborationScreen(
                onNavigateToPostDetails = { postId ->
                    navController.navigate(Screen.PostDetail.createRoute(postId))
                },
                onNavigateToGroups = { navController.navigate(Screen.Groups.route) },
                onNavigateToMessages = { navController.navigate(Screen.Messages.route) },
                onNavigateToGroupDetail = { groupId ->
                    navController.navigate(Screen.GroupDetail.createRoute(groupId))
                }
            )
        }
        
        composable(
            route = Screen.PostDetail.route,
            arguments = listOf(navArgument("postId") { type = NavType.LongType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getLong("postId") ?: 0L
            PostDetailScreen(
                postId = postId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Groups.route) {
            GroupsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToGroupDetail = { groupId ->
                    navController.navigate(Screen.GroupDetail.createRoute(groupId))
                }
            )
        }
        
        composable(
            route = Screen.GroupDetail.route,
            arguments = listOf(navArgument("groupId") { type = NavType.LongType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getLong("groupId") ?: 0L
            GroupDetailScreen(
                groupId = groupId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPostDetails = { postId ->
                    navController.navigate(Screen.PostDetail.createRoute(postId))
                }
            )
        }
        
        composable(Screen.Records.route) {
            RecordsScreen(
                onNavigateToActivities = { navController.navigate(Screen.Activities.route) },
                onNavigateToExpenses = { navController.navigate(Screen.Expenses.route) },
                    onNavigateToYields = { navController.navigate(Screen.Yields.route) },
                    onNavigateToPatches = { navController.navigate(Screen.Patches.route) }
            )
        }
        
        composable(Screen.Chatbot.route) {
            EnhancedChatbotScreen()
        }
        
            // Patches Screen
            composable(Screen.Patches.route) {
                PatchesScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCreate = { navController.navigate(Screen.CreatePatch.route) },
                    onNavigateToPatchDetail = { patchId ->
                        navController.navigate(Screen.PatchDetail.createRoute(patchId))
                    }
                )
            }

            composable(Screen.CreatePatch.route) {
                CreatePatchScreenWrapper(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.PatchDetail.route,
                arguments = listOf(navArgument("patchId") { type = NavType.LongType })
            ) { backStackEntry ->
                val patchId = backStackEntry.arguments?.getLong("patchId") ?: 0L
                PatchDetailScreenWrapper(
                    patchId = patchId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        
        // Record Keeping Screens
        composable(Screen.Activities.route) {
            ActivitiesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToActivityDetail = { activityId ->
                    navController.navigate(Screen.ActivityDetail.createRoute(activityId))
                },
                onNavigateToCreate = {
                    navController.navigate(Screen.CreateActivity.route)
                }
            )
        }
        
        composable(Screen.CreateActivity.route) {
            com.app.shamba_bora.ui.screens.records.CreateActivityScreenWrapper(
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.ActivityDetail.route,
            arguments = listOf(navArgument("activityId") { type = NavType.LongType })
        ) { backStackEntry ->
            val activityId = backStackEntry.arguments?.getLong("activityId") ?: 0L
            ActivityDetailScreen(
                activityId = activityId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Expenses.route) {
            ExpensesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToExpenseDetail = { expenseId ->
                    navController.navigate(Screen.ExpenseDetail.createRoute(expenseId))
                },
                onNavigateToCreate = {
                    navController.navigate(Screen.CreateExpense.route)
                }
            )
        }
        
        composable(Screen.CreateExpense.route) {
            com.app.shamba_bora.ui.screens.records.CreateExpenseScreenWrapper(
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.ExpenseDetail.route,
            arguments = listOf(navArgument("expenseId") { type = NavType.LongType })
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getLong("expenseId") ?: 0L
            ExpenseDetailScreen(
                expenseId = expenseId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Yields.route) {
            YieldsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToYieldDetail = { yieldId ->
                    navController.navigate(Screen.YieldDetail.createRoute(yieldId))
                },
                onNavigateToCreate = {
                    navController.navigate(Screen.CreateYield.route)
                }
            )
        }
        
        composable(Screen.CreateYield.route) {
            com.app.shamba_bora.ui.screens.records.CreateYieldScreenWrapper(
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.YieldDetail.route,
            arguments = listOf(navArgument("yieldId") { type = NavType.LongType })
        ) { backStackEntry ->
            val yieldId = backStackEntry.arguments?.getLong("yieldId") ?: 0L
            YieldDetailScreen(
                yieldId = yieldId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Weather.route) {
            WeatherScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Messaging Screens
        composable(Screen.Messages.route) {
            ConversationListScreen(
                onNavigateToChat = { otherUserId ->
                    navController.navigate(Screen.Conversation.createRoute(otherUserId, "User $otherUserId"))
                },
                onNavigateToGroups = { navController.navigate(Screen.Groups.route) }
            )
        }
        
        composable(
            route = Screen.Conversation.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.LongType },
                navArgument("userName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
            val userName = backStackEntry.arguments?.getString("userName") ?: "User"
            ConversationScreen(
                otherUserId = userId,
                otherUserName = userName,
                onNavigateBack = { navController.popBackStack() }
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

