package com.app.shamba_bora.navigation

sealed class Screen(val route: String, val title: String, val icon: String) {
    // Main Bottom Navigation Screens
    object Home : Screen("home", "Home", "home")
    object Marketplace : Screen("marketplace", "Market", "shopping")
    object Collaboration : Screen("collaboration", "Community", "people")
    object Records : Screen("records", "Records", "description")
    object Chatbot : Screen("chatbot", "AI Chat", "chat")
    
    // Auth Screens
    object Login : Screen("login", "Login", "login")
    object Register : Screen("register", "Register", "person_add")
    
    // Record Keeping Screens
    object Activities : Screen("activities", "Farm Activities", "agriculture")
    object ActivityDetail : Screen("activity_detail/{activityId}", "Activity Details", "info") {
        fun createRoute(activityId: Long) = "activity_detail/$activityId"
    }
    object CreateActivity : Screen("create_activity", "Create Activity", "add")
    object Expenses : Screen("expenses", "Expenses", "payments")
    object ExpenseDetail : Screen("expense_detail/{expenseId}", "Expense Details", "info") {
        fun createRoute(expenseId: Long) = "expense_detail/$expenseId"
    }
    object CreateExpense : Screen("create_expense", "Create Expense", "add")
    object Yields : Screen("yields", "Yields", "inventory")
    object YieldDetail : Screen("yield_detail/{yieldId}", "Yield Details", "info") {
        fun createRoute(yieldId: Long) = "yield_detail/$yieldId"
    }
    object CreateYield : Screen("create_yield", "Create Yield", "add")
    object Patches : Screen("patches", "My Patches", "landscape")
    object CreatePatch : Screen("create_patch", "Create Patch", "add")
    object PatchDetail : Screen("patch_detail/{patchId}", "Patch Details", "info") {
        fun createRoute(patchId: Long) = "patch_detail/$patchId"
    }
    
    // Profile & Settings
    object Profile : Screen("profile", "Profile", "person")
    object Settings : Screen("settings", "Settings", "settings")
    object FarmerProfile : Screen("farmer_profile", "Farmer Profile", "badge")
    
    // Marketplace Screens
    object ProductDetails : Screen("product_details/{productId}", "Product Details", "info") {
        fun createRoute(productId: Long) = "product_details/$productId"
    }
    object Checkout : Screen("checkout/{productId}/{quantity}", "Checkout", "payment") {
        fun createRoute(productId: Long, quantity: Int) = "checkout/$productId/$quantity"
    }
    object AddProduct : Screen("add_product", "Add Product", "add")
    object EditProduct : Screen("edit_product/{productId}", "Edit Product", "edit") {
        fun createRoute(productId: Long) = "edit_product/$productId"
    }
    object MyProducts : Screen("my_products", "My Products", "inventory_2")
    object Orders : Screen("orders", "Orders", "receipt")
    
    // Collaboration Screens
    object PostDetail : Screen("post_detail/{postId}", "Post Details", "article") {
        fun createRoute(postId: Long) = "post_detail/$postId"
    }
    object Groups : Screen("groups", "Groups", "groups")
    object Messages : Screen("messages", "Messages", "message")
    object GroupDetail : Screen("group_detail/{groupId}", "Group Details", "group") {
        fun createRoute(groupId: Long) = "group_detail/$groupId"
    }
    object Conversation : Screen("conversation/{userId}/{userName}", "Conversation", "chat") {
        fun createRoute(userId: Long, userName: String) = "conversation/$userId/$userName"
    }
    
    // Weather
    object Weather : Screen("weather", "Weather", "wb_sunny")
    
    // Dashboard
    object Dashboard : Screen("dashboard", "Dashboard", "dashboard")
}

