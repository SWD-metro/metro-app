package org.com.hcmurs.ui.screens.metro.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.com.hcmurs.Screen
import org.com.hcmurs.ui.screens.login.LoginViewModel
import androidx.compose.material3.Divider as HorizontalDivider

// Thêm imports cho insets
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
// import androidx.compose.material3.ExperimentalMaterial3Api // Không cần thiết nếu không dùng TopAppBar của Material3

// Define colors consistently (based on your provided style)
private val AppWhite = Color(0xFFFFFFFF)
private val BluePrimary = Color(0xFF2196F3) // Màu xanh dương cho icon thường
private val AppLightGray = Color(0xFFE0E0E0) // Light gray cho divider và background icon default
private val AppMediumGray = Color(0xFF9E9E9E) // Medium gray cho mũi tên
private val AppDarkGray = Color(0xFF424242) // Dark gray cho text
// private val BlueDark = Color(0xFF1976D2) // Not explicitly used without TopAppBar

// Gradient colors from your provided style
private val CyanColor = Color(0xFF00BCD4)
private val BlueColor = Color(0xFF2196F3)
private val DarkBlueColor = Color(0xFF1976D2)


data class MenuItem(
    val icon: ImageVector,
    val title: String,
    val hasArrow: Boolean = true,
    val isDestructive: Boolean = false,
    val onClickAction: (() -> Unit)? = null
)

@Composable
fun HurcLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = org.com.hcmurs.R.drawable.hurc),
        contentDescription = "HURC Logo",
        modifier = modifier
    )
}

@Composable
fun MenuItemRow(
    item: MenuItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = if (item.isDestructive)
                Color(0xFFFFEBEE)
            else
                Color(0xFFE3F2FD)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = if (item.isDestructive)
                        Color(0xFFE53935) // Red tint for destructive
                    else
                        BluePrimary, // Blue tint for regular items
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Title
        Text(
            text = item.title,
            color = if (item.isDestructive)
                Color(0xFFE53935)
            else
                AppDarkGray, // Dark gray for regular text
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        // Arrow
        if (item.hasArrow) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = AppMediumGray, // Medium gray for arrow
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun AccountScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()

    LaunchedEffect(isAuthenticated) {
        if (!isAuthenticated) {
            navController.navigate(Screen.Login.route) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.refreshUserProfile()
    }

    val userName = userProfile?.name ?: "Chưa cập nhật"
    val userEmail = userProfile?.email ?: "Chưa cập nhật"

    val menuItems = listOf(
        MenuItem(
            icon = Icons.Default.Person,
            title = "Họ tên: $userName",
            hasArrow = false
        ),
        MenuItem(
            icon = Icons.Default.Email,
            title = "Email: $userEmail",
            hasArrow = false
        ),
        MenuItem(
            icon = Icons.Default.Security, // Đã đổi từ AccountBox/Info sang Security để khớp với ngữ cảnh xác thực
            title = "Xác thực tài khoản",
            hasArrow = true,
            onClickAction = { navController.navigate(Screen.CCCD.route) }
        ),
        MenuItem(
            icon = Icons.Default.ShoppingCart,
            title = "Quản lý phương thức thanh toán",
            hasArrow = true,
            onClickAction = { /* onMenuItemClick(it) hoặc navigate to payment screen */ } // Để trống hoặc thêm điều hướng
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Full screen gradient background, starting from the top of the screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(CyanColor, BlueColor, DarkBlueColor),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY // Extend gradient to bottom
                    )
                )
        ) {
            // Spacer to push content down below the status bar
            Spacer(modifier = Modifier.statusBarsPadding())
        }

        // 2. Main content area including Profile section and Content Card
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 16.dp), // Adjusted padding
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(AppWhite, CircleShape)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color(0xFFE0E0E0), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            modifier = Modifier.size(48.dp),
                            tint = Color(0xFF9E9E9E)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Name
                Text(
                    text = userName,
                    color = AppWhite, // White text on gradient background
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Content Card (Menu Items and Logout Button)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Makes the card fill remaining height
                    .offset(y = 28.dp), // Move card up to overlap profile section
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = AppWhite,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 28.dp, start = 16.dp, end = 16.dp) // Padding inside the card
                ) {
                    // Menu Items
                    menuItems.forEach { item ->
                        MenuItemRow(
                            item = item,
                            onClick = { item.onClickAction?.invoke() }
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = AppLightGray
                    )

                    // Logout Button
                    MenuItemRow(
                        item = MenuItem(
                            icon = Icons.Default.ExitToApp,
                            title = "Đăng xuất",
                            hasArrow = false,
                            isDestructive = false
                        ),
                        onClick = { viewModel.logout() } // Gọi hàm logout từ ViewModel
                    )

                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .offset(y = 210.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = AppWhite,
                shadowElevation = 6.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    HurcLogo(modifier = Modifier.size(40.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountInfoScreenPreview() {
    MaterialTheme {
        // Provide a dummy NavController and ViewModel for preview
        AccountScreen(
            navController = rememberNavController(),
            viewModel = hiltViewModel<LoginViewModel>() // Or mock it
        )
    }
}