package com.app.shamba_bora.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.shamba_bora.navigation.Screen
import com.app.shamba_bora.utils.PreferenceManager

@Composable
fun DrawerMenu(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onClose: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Header with gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "ShambaBora",
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "ShambaBora",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = PreferenceManager.getUsername().ifEmpty { "Maize Farmers" },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Menu Items
            DrawerMenuItem(
                icon = Icons.Default.Person,
                title = "Profile",
                route = Screen.Profile.route,
                currentRoute = currentRoute,
                onClick = { 
                    onNavigate(Screen.Profile.route)
                    onClose()
                }
            )
        
            
            DrawerMenuItem(
                icon = Icons.Default.Build,
                title = "Farmer Profile",
                route = Screen.FarmerProfile.route,
                currentRoute = currentRoute,
                onClick = { 
                    onNavigate(Screen.FarmerProfile.route)
                    onClose()
                }
            )
            
            DrawerMenuItem(
                icon = Icons.Default.DateRange,
                title = "Weather",
                route = Screen.Weather.route,
                currentRoute = currentRoute,
                onClick = { 
                    onNavigate(Screen.Weather.route)
                    onClose()
                }
            )
            
            DrawerMenuItem(
                icon = Icons.Default.ShoppingCart,
                title = "My Products",
                route = Screen.MyProducts.route,
                currentRoute = currentRoute,
                onClick = { 
                    onNavigate(Screen.MyProducts.route)
                    onClose()
                }
            )
            
            DrawerMenuItem(
                icon = Icons.Default.ShoppingCart,
                title = "Orders",
                route = Screen.Orders.route,
                currentRoute = currentRoute,
                onClick = { 
                    onNavigate(Screen.Orders.route)
                    onClose()
                }
            )
            
            DrawerMenuItem(
                icon = Icons.Default.Person,
                title = "Groups",
                route = Screen.Groups.route,
                currentRoute = currentRoute,
                onClick = { 
                    onNavigate(Screen.Groups.route)
                    onClose()
                }
            )
            
            DrawerMenuItem(
                icon = Icons.Default.Email,
                title = "Messages",
                route = Screen.Messages.route,
                currentRoute = currentRoute,
                onClick = { 
                    onNavigate(Screen.Messages.route)
                    onClose()
                }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            DrawerMenuItem(
                icon = Icons.Default.Settings,
                title = "Settings",
                route = Screen.Settings.route,
                currentRoute = currentRoute,
                onClick = { 
                    onNavigate(Screen.Settings.route)
                    onClose()
                }
            )
            
            DrawerMenuItem(
                icon = Icons.Default.Lock,
                title = "Logout",
                route = "",
                currentRoute = null,
                onClick = { 
                    // Clear user data and navigate to login
                    PreferenceManager.clear()
                    onNavigate(Screen.Login.route)
                    onClose()
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DrawerMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    route: String,
    currentRoute: String?,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = title) },
        label = { Text(title, style = MaterialTheme.typography.bodyLarge) },
        selected = currentRoute == route,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            unselectedContainerColor = Color.Transparent
        )
    )
}

