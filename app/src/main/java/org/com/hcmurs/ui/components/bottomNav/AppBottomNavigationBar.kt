package org.com.hcmurs.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults // Quan trọng để tùy chỉnh màu
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.com.hcmurs.Screen
import org.com.hcmurs.ui.theme.BluePrimary // Vẫn giữ import nếu muốn dùng màu xanh cho icon/text
import org.com.hcmurs.ui.theme.AppLightGray // Import màu xám nhạt cho indicator

data class BottomNavItem(
    val icon: ImageVector,
    val label: String,
    val route: String
)

@Composable
fun AppBottomNavigationBar(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    currentRoute: String?
) {
    val navItems = listOf(
        BottomNavItem(Icons.Default.Home, "Home", Screen.Home.route),
        BottomNavItem(Icons.Default.Search, "Search", Screen.BuyTicket.route),
        BottomNavItem(Icons.Default.QrCodeScanner, "My Ticket", Screen.BuyTicket.route),
        BottomNavItem(Icons.Default.Settings, "Account", Screen.Account.route)
    )
    
    val selectedContentColor = Color(0xFF4A6FA5)
    val unselectedContentColor = Color.Gray
    val navBarBackgroundColor = Color.White
    val indicatorHighlightColor = AppLightGray


    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .background(navBarBackgroundColor, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        containerColor = Color.Transparent
    ) {
        navItems.forEach { item ->
            val isSelected = currentRoute == item.route

            val iconScale by animateFloatAsState(
                targetValue = if (isSelected) 1.2f else 1.0f,
                animationSpec = tween(durationMillis = 200), label = "iconScaleAnimation"
            )

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) selectedContentColor else unselectedContentColor,
                        modifier = Modifier
                            .size(32.dp)
                            .scale(iconScale)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedContentColor,
                    selectedTextColor = selectedContentColor,
                    unselectedIconColor = unselectedContentColor,
                    unselectedTextColor = unselectedContentColor,
                    indicatorColor = indicatorHighlightColor
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppBottomNavigationBarPreview() {
    AppBottomNavigationBar(
        navController = rememberNavController(),
        currentRoute = Screen.Home.route
    )
}