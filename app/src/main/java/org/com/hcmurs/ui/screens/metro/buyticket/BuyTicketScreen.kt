package org.com.hcmurs.ui.screens.metro.buyticket

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.com.hcmurs.Screen
import org.com.hcmurs.ui.components.AppBottomNavigationBar
import org.com.hcmurs.ui.components.topNav.CustomTopAppBar
import org.com.hcmurs.ui.theme.AppWhite
import org.com.hcmurs.ui.theme.AppLightGray
import org.com.hcmurs.ui.theme.AppMediumGray
import org.com.hcmurs.ui.theme.AppDarkGray


data class MetroTicket(
    val id: Int,
    val name: String,
    val description: String,
    val price: String,
    val validity: String,
    val color: Color = Color(0xFF385F8E)
)

@Composable
fun BuyTicketScreen(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selectedStationFrom = navController.currentBackStackEntry?.savedStateHandle?.get<String>("selectedFromStation") ?: "Chọn ga khởi hành"
    val selectedStationTo = navController.currentBackStackEntry?.savedStateHandle?.get<String>("selectedToStation") ?: "Chọn ga điểm đến"

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Mua vé",
                navigationIcon = {
                    IconButton(onClick = { /* Có thể navigate về Home hoặc không làm gì */ }) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Trang chủ",
                            tint = Color(0xFF1565C0)
                        )
                    }
                }
            )
        },
        bottomBar = {
            AppBottomNavigationBar(
                navController = navController,
                currentRoute = currentRoute
            )
        },
        containerColor = AppLightGray
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AppWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Tìm kiếm ga:",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppDarkGray
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clickable {
                                    navController.navigate(Screen.SearchStation.route + "?focusField=from")
                                },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, AppMediumGray.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = AppMediumGray
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = if (selectedStationFrom != "Chọn ga khởi hành") selectedStationFrom else "Nhập tên ga...",
                                    color = if (selectedStationFrom != "Chọn ga khởi hành") AppDarkGray else AppMediumGray,
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
            item {
                Text(
                    text = "Các loại vé Metro",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppDarkGray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val metroTickets = listOf(
                    MetroTicket(id = 1, name = "Vé 1 ngày", description = "Di chuyển không giới hạn trong 1 ngày.", price = "40.000 VNĐ", validity = "Hiệu lực 24h", color = Color(0xFF385F8E)),
                    MetroTicket(id = 2, name = "Vé 3 ngày", description = "Di chuyển không giới hạn trong 3 ngày.", price = "90.000 VNĐ", validity = "Hiệu lực 72h", color = Color(0xFF2D426C)),
                    MetroTicket(id = 3, name = "Vé tháng", description = "Di chuyển không giới hạn trong 1 tháng.", price = "300.000 VNĐ", validity = "Hiệu lực 30 ngày", color = Color(0xFF1E3F77))
                    // Thêm các vé khác nếu bạn muốn
                )
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    metroTickets.forEach { ticket ->
                        TicketCard(ticket = ticket) {
                            // Logic điều hướng khi nhấn vào vé
                            navController.navigate("order_info_screen/${ticket.name}")
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun TicketCard(ticket: MetroTicket, onClick: () -> Unit) {
    // ... nội dung của TicketCard giữ nguyên ...
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ticket.color.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(0.7f)
            ) {
                Text(
                    text = ticket.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = ticket.description,
                    fontSize = 12.sp,
                    color = AppWhite.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AppWhite.copy(alpha = 0.2f),
                    modifier = Modifier.wrapContentSize()
                ) {
                    Text(
                        text = "Loại vé: ${ticket.validity}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppWhite,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(0.3f),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = ticket.price,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Thời hạn",
                    fontSize = 10.sp,
                    color = AppWhite.copy(alpha = 0.7f)
                )
                Text(
                    text = ticket.validity,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppWhite
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun PreviewBuyTicketScreen() {
    val navController = rememberNavController()
    BuyTicketScreen(navController = navController)
}