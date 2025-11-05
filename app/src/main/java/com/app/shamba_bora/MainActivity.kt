package com.app.shamba_bora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.shamba_bora.navigation.AppNavHost
import com.app.shamba_bora.navigation.Screen
import com.app.shamba_bora.ui.components.DrawerMenu
import com.app.shamba_bora.ui.theme.Shamba_BoraTheme
import com.app.shamba_bora.utils.PreferenceManager
import kotlinx.coroutines.launch
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Shamba_BoraTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    // Check if user is logged in to determine start destination
    val isLoggedIn = PreferenceManager.isLoggedIn()
    val startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route
    
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var showBottomNav by remember { mutableStateOf(true) }
    var showTopBar by remember { mutableStateOf(true) }
    
    // Show bottom nav and top bar only on main screens
    val mainScreens = listOf(
        Screen.Home.route,
        Screen.Marketplace.route,
        Screen.Collaboration.route,
        Screen.Records.route,
        Screen.Chatbot.route
    )
    
    val authScreens = listOf(
        Screen.Login.route,
        Screen.Register.route
    )
    
    LaunchedEffect(currentRoute) {
        showBottomNav = mainScreens.contains(currentRoute)
        showTopBar = !authScreens.contains(currentRoute)
    }
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerMenu(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                    coroutineScope.launch {
                        drawerState.close()
                    }
                },
                onClose = { 
                    coroutineScope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                if (showTopBar) {
                    TopAppBar(
                    title = { 
                        Text(
                            text = when (currentRoute) {
                                Screen.Home.route -> "ShambaBora"
                                Screen.Marketplace.route -> "Marketplace"
                                Screen.Collaboration.route -> "Community"
                                Screen.Records.route -> "Records"
                                Screen.Chatbot.route -> "AI Assistant"
                                else -> "ShambaBora"
                            }
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { 
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
                }
            },
            bottomBar = {
                if (showBottomNav) {
                    NavigationBar {
                        val bottomNavItems = listOf(
                            Screen.Home to Icons.Default.Home,
                            Screen.Marketplace to Icons.Default.ShoppingCart,
                            Screen.Collaboration to Icons.Outlined.Person,
                            Screen.Records to Icons.Default.Info,
                            Screen.Chatbot to Icons.Default.Face
                        )
                        
                        bottomNavItems.forEach { (screen, icon) ->
                            NavigationBarItem(
                                icon = { Icon(icon, contentDescription = screen.title) },
                                label = { Text(screen.title) },
                                selected = currentRoute == screen.route,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            AppNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                startDestination = startDestination
            )
        }
    }
}
