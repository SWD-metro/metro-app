package org.com.hcmurs.ui.screens.metro.buyticket

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Home // Không còn dùng Home, có thể xóa import này nếu không dùng
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.com.hcmurs.ui.theme.HcmursAppTheme // Import theme của bạn

// Data classes remains the same, they are good
data class TicketOption(
    val title: String,
    val price: String,
    val icon: ImageVector = Icons.Default.ConfirmationNumber
)

data class RouteInfo(
    val from: String,
    val details: String = "Xem chi tiết"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyTicketScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            BuyTicketTopBar(
                onBackClick = { navController.popBackStack() }
            )
        },
        containerColor = MaterialTheme.colorScheme.background // Nền của Scaffold là màu background của theme
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp) // Điều chỉnh padding tổng thể
        ) {
            // Welcome section
            WelcomeCard()

            Spacer(modifier = Modifier.height(24.dp))

            // Hot section (Changed to use a more generic SectionHeader)
            SectionHeader(title = "Vé Nổi bật", icon = "🔥")

            Spacer(modifier = Modifier.height(12.dp))

            // Regular tickets
            TicketOptionsSection(navController)

            Spacer(modifier = Modifier.height(32.dp))

            // Student/Discount section
            SectionHeader(title = "Ưu đãi", icon = "🌟")
            Spacer(modifier = Modifier.height(12.dp))
            StudentDiscountSection(navController)

            Spacer(modifier = Modifier.height(32.dp))

            // Routes section
            SectionHeader(title = "Tuyến đường", icon = "🗺️")
            Spacer(modifier = Modifier.height(12.dp))
            RoutesSection()

            Spacer(modifier = Modifier.height(32.dp))

            // Long term ticket section
            SectionHeader(title = "Vé dài hạn", icon = "🗓️")
            Spacer(modifier = Modifier.height(12.dp))
            LongTermTicketSection(navController)

            Spacer(modifier = Modifier.height(80.dp)) // Space for bottom navigation
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyTicketTopBar(onBackClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Mua vé",
                color = MaterialTheme.colorScheme.onSurface, // Màu chữ tiêu đề từ theme
                style = MaterialTheme.typography.titleLarge, // Sử dụng typography từ theme
                fontWeight = FontWeight.Bold // Có thể giữ fontweight nếu muốn nổi bật hơn style mặc định
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary // Màu icon từ theme
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent, // Nền trong suốt
            titleContentColor = MaterialTheme.colorScheme.onSurface // Đảm bảo màu chữ tiêu đề đúng
        )
    )
}

@Composable
fun WelcomeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), // Màu nền từ theme
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.onPrimary // Màu trắng tinh khiết trên nền primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "🦁",
                        fontSize = 32.sp // Giữ fontSize cho emoji vì typography không áp dụng cho emoji
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "Chào mừng, Anh Tú!",
                    style = MaterialTheme.typography.titleMedium, // Sử dụng typography từ theme
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer // Màu chữ tương phản từ theme
                )
                Text(
                    text = "Bắt đầu các trải nghiệm mới cùng Metro nhé!",
                    style = MaterialTheme.typography.bodyMedium, // Sử dụng typography từ theme
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) // Màu chữ nhạt hơn từ theme
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(text = icon, fontSize = 24.sp) // Giữ fontSize cho emoji
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall, // Sử dụng typography từ theme
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface // Màu chữ tiêu đề từ theme
        )
    }
}

@Composable
fun TicketOptionsSection(navController: NavHostController) {
    val ticketOptions = listOf(
        TicketOption("Vé 1 ngày", "40.000 đ"),
        TicketOption("Vé 3 ngày", "90.000 đ"),
        TicketOption("Vé tháng", "300.000 đ")
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ticketOptions.forEach { ticket ->
            TicketCard(ticket = ticket, navController = navController)
        }
    }
}

@Composable
fun TicketCard(ticket: TicketOption, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("ticket_detail/${ticket.title}/${ticket.price}")
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // Màu nền card từ theme
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) // Nền màu chính nhạt từ theme
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = ticket.icon,
                        contentDescription = ticket.title,
                        tint = MaterialTheme.colorScheme.primary, // Màu icon từ theme
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = ticket.title,
                    style = MaterialTheme.typography.titleMedium, // Sử dụng typography từ theme
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface // Màu chữ trên surface từ theme
                )
                Text(
                    text = ticket.price,
                    style = MaterialTheme.typography.bodyLarge, // Sử dụng typography từ theme
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary // Màu giá riêng biệt từ theme
                )
            }
        }
    }
}

@Composable
fun StudentDiscountSection(navController: NavHostController) {
    TicketCard(
        ticket = TicketOption("Vé tháng HSSV", "150.000 đ")
        ,navController = navController
    )
}

@Composable
fun LongTermTicketSection(navController: NavHostController) {
    TicketCard(
        ticket = TicketOption("Vé quý", "850.000 đ"),
        navController = navController
    )
    Spacer(modifier = Modifier.height(16.dp))
    TicketCard(
        ticket = TicketOption("Vé năm", "3.000.000 đ"),
        navController = navController
    )
}

@Composable
fun RoutesSection() {
    val routes = listOf(
        RouteInfo("Đi từ ga Bến Thành"),
        RouteInfo("Đi từ ga Ba Son"),
        RouteInfo("Đi từ ga Văn Thánh")
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        routes.forEach { route ->
            RouteCard(route = route)
        }
    }
}

@Composable
fun RouteCard(route: RouteInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle route selection */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // Màu nền card từ theme
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = route.from,
                    style = MaterialTheme.typography.titleMedium, // Sử dụng typography từ theme
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface // Màu chữ trên surface từ theme
                )
            }

            Text(
                text = route.details,
                style = MaterialTheme.typography.bodyMedium, // Sử dụng typography từ theme
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary, // Màu chi tiết từ theme
                modifier = Modifier.clickable { /* Handle details */ }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BuyTicketScreenPreview() {
    // Đảm bảo preview sử dụng HcmursAppTheme để thấy đúng màu sắc
    HcmursAppTheme {
        val navController = rememberNavController()
        BuyTicketScreen(navController = navController)
    }
}