package org.com.hcmurs.ui.screens.metro.buyticket

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api // Vẫn cần nếu dùng TopAppBar / IconButton ở đâu đó
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.com.hcmurs.Screen
import org.com.hcmurs.repositories.apis.ticket.TicketType
import androidx.compose.ui.draw.clip
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.text.style.TextOverflow

// Thêm imports cho insets
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.safeDrawingPadding // Hoặc safeContentPadding, safeGesturesPadding


// Define colors consistently
private val AppWhite = Color(0xFFFFFFFF)
private val BluePrimary = Color(0xFF2196F3)
private val AppLightGray = Color(0xFFF0F0F0)
private val AppMediumGray = Color(0xFFB0B0B0)
private val AppDarkGray = Color(0xFF424242)
private val BlueDark = Color(0xFF1976D2)
private val LightGreenBackground = Color(0xFFE8F5E9)
private val TextPrimaryColor = Color(0xFF212121)
private val TextSecondaryColor = Color(0xFF757575)

// Data classes
data class TicketOption(
    val title: String,
    val price: String,
    val icon: ImageVector = Icons.Default.ConfirmationNumber
)

data class RouteInfo(
    val from: String,
    val to: String,
    val details: String = "Xem chi tiết"
)

// --- SECTION HEADER ---
@Composable
fun SectionHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = BlueDark,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = TextPrimaryColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// --- TICKET CARD ---
@Composable
fun TicketCard(
    ticket: TicketType,
    navController: NavHostController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (ticket.name == "Single") {
                    navController.navigate(Screen.StationSelection.route)
                } else {
                    navController.navigate(Screen.BuyTicketDetail.createRoute(ticket.id))
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(LightGreenBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ConfirmationNumber,
                        contentDescription = ticket.description,
                        tint = BluePrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = ticket.description,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimaryColor
                    )
                    Text(
                        text = "${ticket.price} đ",
                        fontSize = 14.sp,
                        color = TextSecondaryColor
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Mua vé",
                tint = TextSecondaryColor.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun SearchStationCard(
    navController: NavHostController,
    selectedStationFrom: String,
    selectedStationTo: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Bạn muốn đến ga nào:",
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
                        navController.navigate(Screen.StationSelection.route + "?focusField=from")
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


// --- TICKET OPTIONS SECTION ---
@Composable
fun TicketOptionsSection(
    navController: NavHostController,
    viewModel: BuyTicketViewModel
) {
    val ticketOptions by viewModel.ticketTypes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        if (ticketOptions.isEmpty() && !isLoading && errorMessage == null) {
            viewModel.fetchTicketTypes()
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BluePrimary)
        }
    } else if (errorMessage != null) {
        Text(text = "Lỗi tải dữ liệu: $errorMessage", color = Color.Red, modifier = Modifier.padding(16.dp))
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (ticketOptions.isEmpty()) {
                Text(
                    text = "Hiện không có loại vé nào khả dụng.",
                    color = TextSecondaryColor,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                ticketOptions.forEach { ticket ->
                    TicketCard(ticket = ticket, navController = navController)
                }
            }
        }
    }
}

// --- MAIN SCREEN: BUY TICKET SCREEN (WITHOUT SCAFFOLD) ---
@Composable
fun BuyTicketScreen(
    navController: NavHostController,
    buyTicketViewModel: BuyTicketViewModel = hiltViewModel(),
    fareMatrixViewModel: FareMatrixViewModel = hiltViewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedStationFrom = navBackStackEntry?.savedStateHandle?.get<String>("selectedFromStation") ?: "Chọn ga khởi hành"
    val selectedStationTo = navBackStackEntry?.savedStateHandle?.get<String>("selectedToStation") ?: "Chọn ga điểm đến"

    // Outermost Column to hold all content and apply global modifiers
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, LightGreenBackground),
                    startY = 0f,
                    endY = 1500f
                )
            )
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        SearchStationCard(
            navController = navController,
            selectedStationFrom = selectedStationFrom,
            selectedStationTo = selectedStationTo
        )
        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(title = "Mua vé theo lượt", icon = Icons.Default.LocalActivity)
        Spacer(modifier = Modifier.height(12.dp))
        TicketOptionsSection(navController, buyTicketViewModel)

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(title = "Ưu đãi Học sinh - Sinh viên", icon = Icons.Default.School)
        Spacer(modifier = Modifier.height(12.dp))

        TicketCard(
            ticket = TicketType(
                id = 0, name = "Student Monthly", description = "Vé tháng HSSV", price = 150000,
                validityDuration = "ONE_MONTH", isActive = true, createdAt = "", updatedAt = ""
            ),
            navController = navController
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(title = "Vé dài hạn", icon = Icons.Default.DateRange)
        Spacer(modifier = Modifier.height(12.dp))
        TicketOptionsSection(navController, buyTicketViewModel)

        Spacer(modifier = Modifier.height(16.dp)) // Add some bottom padding if needed, adjust 80.dp
    }

}

@Preview(showBackground = true)
@Composable
fun BuyTicketScreenPreview() {
    val navController = rememberNavController()
    BuyTicketScreen(navController = navController)
}