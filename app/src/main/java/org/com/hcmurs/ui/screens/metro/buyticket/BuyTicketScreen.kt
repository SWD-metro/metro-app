package org.com.hcmurs.ui.screens.buyticket

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.com.hcmurs.R
import org.com.hcmurs.ui.components.AppBottomNavigationBar
import org.com.hcmurs.ui.theme.BlueDark
import org.com.hcmurs.ui.theme.AppWhite
import org.com.hcmurs.ui.theme.AppLightGray
import org.com.hcmurs.ui.theme.AppMediumGray
import org.com.hcmurs.ui.theme.AppDarkGray
import org.com.hcmurs.Screen // Import Screen của bạn

// Model đại diện cho một loại vé Metro
data class MetroTicket(
    val id: Int,
    val name: String,
    val description: String,
    val price: String,
    val validity: String, // Ví dụ: "1 ngày", "3 ngày", "1 tháng", "Học sinh/Sinh viên"
    val color: Color = Color(0xFF385F8E) // Giữ nguyên màu vé cụ thể
)

// Model StationSuggestion đã bị loại bỏ khỏi file này vì không còn sử dụng

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyTicketScreen(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Giữ state cho ga đã chọn từ màn hình SearchStationScreen
    // Bạn có thể dùng SavedStateHandle hoặc ViewModel để truyền dữ liệu phức tạp hơn
    // Ở đây đơn giản là lưu trữ tên ga nếu có
    val selectedStationFrom = navController.currentBackStackEntry?.savedStateHandle?.get<String>("selectedFromStation") ?: "Chọn ga khởi hành"
    val selectedStationTo = navController.currentBackStackEntry?.savedStateHandle?.get<String>("selectedToStation") ?: "Chọn ga điểm đến"


    Scaffold(
        topBar = {
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

                        // Thay thế OutlinedTextField bằng một click-able Surface
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp) // Chiều cao của OutlinedTextField
                                .clickable {
                                    // Điều hướng đến SearchStationScreen và yêu cầu focus vào trường "from"
                                    navController.navigate(Screen.SearchStation.route + "?focusField=from")
                                },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, AppMediumGray.copy(alpha = 0.5f)) // Giống border của OutlinedTextField
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
                    MetroTicket(
                        id = 1,
                        name = "Vé 1 Ngày",
                        description = "Di chuyển không giới hạn trong 1 ngày kể từ khi kích hoạt.",
                        price = "30.000 VNĐ",
                        validity = "Hiệu lực 24h",
                        color = Color(0xFF385F8E) // Giữ nguyên màu vé cụ thể
                    ),
                    MetroTicket(
                        id = 2,
                        name = "Vé 3 Ngày",
                        description = "Di chuyển không giới hạn trong 3 ngày liên tiếp.",
                        price = "80.000 VNĐ",
                        validity = "Hiệu lực 72h",
                        color = Color(0xFF2D426C)
                    ),
                    MetroTicket(
                        id = 3,
                        name = "Vé Tháng",
                        description = "Di chuyển không giới hạn trong 1 tháng. Cần đăng ký thông tin cá nhân.",
                        price = "200.000 VNĐ",
                        validity = "Hiệu lực 30 ngày",
                        color = Color(0xFF1E3F77)
                    ),
                    MetroTicket(
                        id = 4,
                        name = "Vé HSSV",
                        description = "Vé ưu đãi dành cho Học sinh/Sinh viên. Cần thẻ HSSV hợp lệ.",
                        price = "100.000 VNĐ",
                        validity = "Hiệu lực 1 tháng",
                        color = Color(0xFF281B9A)
                    ),
                    MetroTicket(
                        id = 5,
                        name = "Vé Lượt",
                        description = "Thanh toán theo từng chặng, tính phí theo quãng đường di chuyển.",
                        price = "Từ 8.000 VNĐ",
                        validity = "Theo chuyến",
                        color = Color(0xFFA52714)
                    )
                )
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    metroTickets.forEach { ticket ->
                        TicketCard(ticket = ticket) {
                            // TODO: Handle click on ticket, e.g., navigate to detail screen
                            // navController.navigate(Screen.BuyTicketDetail.route + "/${ticket.id}")
                        }
                    }
                }
            }
        }
    }
}

// HÀM StationSuggestionItem ĐÃ BỊ XÓA HOẶC COMMENT Ở ĐÂY VÌ KHÔNG CÒN ĐƯỢC SỬ DỤNG TRONG FILE NÀY
// @Composable
// fun StationSuggestionItem(suggestion: StationSuggestion) {
//     Row(
//         modifier = Modifier
//             .fillMaxWidth()
//             .padding(vertical = 8.dp)
//             .clickable { /* TODO: Handle click on station suggestion */ },
//         verticalAlignment = Alignment.CenterVertically
//     ) {
//         Icon(
//             imageVector = Icons.Default.LocationOn,
//             contentDescription = null,
//             tint = BlueDark,
//             modifier = Modifier.size(24.dp)
//         )
//         Spacer(modifier = Modifier.width(12.dp))
//         Column {
//             Text(text = suggestion.name, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = AppDarkGray)
//             Text(text = suggestion.address, fontSize = 12.sp, color = AppMediumGray)
//         }
//     }
// }

@Composable
fun TicketCard(ticket: MetroTicket, onClick: () -> Unit) {
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