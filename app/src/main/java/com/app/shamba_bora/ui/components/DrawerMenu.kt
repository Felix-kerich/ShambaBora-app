package com.app.shamba_bora.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.shamba_bora.navigation.Screen

@Composable
fun DrawerMenu(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocalFlorist,
                contentDescription = "ShambaBora",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "ShambaBora",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Maize Farmers",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        
        // Menu Items
        DrawerMenuItem(
            icon = Icons.Default.Person,
            title = "Profile",
            route = Screen.Profile.route,
            currentRoute = currentRoute,
            onClick = { onNavigate(Screen.Profile.route) }
        )
        
        DrawerMenuItem(
            icon = Icons.Default.Badge,
            title = "Farmer Profile",
            route = Screen.FarmerProfile.route,
            currentRoute = currentRoute,
            onClick = { onNavigate(Screen.FarmerProfile.route) }
        )
        
        DrawerMenuItem(
            icon = Icons.Default.WbSunny,
            title = "Weather",
            route = Screen.Weather.route,
            currentRoute = currentRoute,
            onClick = { onNavigate(Screen.Weather.route) }
        )
        
        DrawerMenuItem(
            icon = Icons.Default.Inventory2,
            title = "My Products",
            route = Screen.MyProducts.route,
            currentRoute = currentRoute,
            onClick = { onNavigate(Screen.MyProducts.route) }
        )
        
        DrawerMenuItem(
            icon = Icons.Default.Receipt,
            title = "Orders",
            route = Screen.Orders.route,
            currentRoute = currentRoute,
            onClick = { onNavigate(Screen.Orders.route) }
        )
        
        DrawerMenuItem(
            icon = Icons.Default.Groups,
            title = "Groups",
            route = Screen.Groups.route,
            currentRoute = currentRoute,
            onClick = { onNavigate(Screen.Groups.route) }
        )
        
        DrawerMenuItem(
            icon = Icons.Default.Message,
            title = "Messages",
            route = Screen.Messages.route,
            currentRoute = currentRoute,
            onClick = { onNavigate(Screen.Messages.route) }
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        
        DrawerMenuItem(
            icon = Icons.Default.Settings,
            title = "Settings",
            route = Screen.Settings.route,
            currentRoute = currentRoute,
            onClick = { onNavigate(Screen.Settings.route) }
        )
        
        DrawerMenuItem(
            icon = Icons.Default.Logout,
            title = "Logout",
            route = "",
            currentRoute = null,
            onClick = { 
                // Handle logout
                onNavigate(Screen.Login.route)
            }
        )
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
        label = { Text(title) },
        selected = currentRoute == route,
        onClick = onClick,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

