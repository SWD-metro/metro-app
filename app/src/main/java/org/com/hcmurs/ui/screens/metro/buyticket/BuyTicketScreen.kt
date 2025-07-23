package org.com.hcmurs.ui.screens.metro.buyticket

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.com.hcmurs.R
import org.com.hcmurs.RouteResponse
import org.com.hcmurs.Screen
import org.com.hcmurs.repositories.apis.ticket.TicketType
import org.com.hcmurs.ui.screens.login.LoginViewModel
import org.com.hcmurs.ui.screens.stationselection.StationSelectionViewModel

// Define colors consistently
private val AppWhite = Color(0xFFFFFFFF)
private val BluePrimary = Color(0xFF2196F3)
private val AppMediumGray = Color(0xFFB0B0B0)
private val AppDarkGray = Color(0xFF424242)
private val BlueDark = Color(0xFF1976D2)
private val LightGreenBackground = Color(0xFFE8F5E9)
private val TextPrimaryColor = Color(0xFF212121)
private val TextSecondaryColor = Color(0xFF757575)


// --- MAIN SCREEN: BUY TICKET SCREEN ---
@Composable
fun BuyTicketScreen(
    navController: NavController,
    buyTicketViewModel: BuyTicketViewModel,
    stationViewModel: StationSelectionViewModel,
    loginViewModel: LoginViewModel
) {
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
        Spacer(modifier = Modifier.height(24.dp))
        TicketOptionsSection(
            navController = navController,
            buyTicketViewModel = buyTicketViewModel,
            stationViewModel = stationViewModel,
            loginViewModel = loginViewModel
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}


// --- TICKET OPTIONS SECTION ---
@SuppressLint("SuspiciousIndentation")
@Composable
fun TicketOptionsSection(
    navController: NavController,
    buyTicketViewModel: BuyTicketViewModel,
    stationViewModel: StationSelectionViewModel,
    loginViewModel: LoginViewModel
) {
    val ticketOptions by buyTicketViewModel.ticketTypes.collectAsState()
    val isLoadingTickets by buyTicketViewModel.isLoading.collectAsState()
    val ticketErrorMessage by buyTicketViewModel.errorMessage.collectAsState()
    val stationUiState by stationViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        if (ticketOptions.isEmpty() && !isLoadingTickets && ticketErrorMessage == null) {
            buyTicketViewModel.fetchTicketTypes()
        }
    }

    if (isLoadingTickets) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = BluePrimary)
        }
    } else if (ticketErrorMessage != null) {
        Text(
            text = stringResource(R.string.error_loading_tickets, ticketErrorMessage!!),
            color = Color.Red,
            modifier = Modifier.padding(16.dp)
        )
    } else {
        val ticketSingle = ticketOptions.find { it.name == "Vé đơn" }
        val ticketStudent = ticketOptions.find { it.name == "Vé sinh viên" }
        val otherTickets =
            ticketOptions.filterNot { it.name == "Vé đơn" || it.name == "Vé sinh viên" }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ticketSingle?.let {
                SectionHeader(
                    title = stringResource(R.string.route_ticket_section),
                    icon = Icons.Default.Route
                )
                RouteSelectionCard(
                    navController = navController,
                    routes = stationUiState.routes,
                    selectedRoute = stationUiState.selectedRoute,
                    onRouteSelected = { route -> stationViewModel.onRouteSelected(route) },
                    isLoading = stationUiState.isLoadingRoutes,
                    errorMessage = if (stationUiState.routes.isEmpty()) stationUiState.errorMessage else null
                )
            }

            ticketStudent?.let {
                SectionHeader(
                    title = stringResource(R.string.student_ticket_section),
                    icon = Icons.Default.School
                )
                TicketCard(
                    ticket = it,
                    navController = navController,
                    loginViewModel = loginViewModel,
                    buyTicketViewModel = buyTicketViewModel
                )
            }

            if (otherTickets.isNotEmpty()) {
                SectionHeader(
                    title = stringResource(R.string.other_tickets_section),
                    icon = Icons.Default.LocalActivity
                )
                otherTickets.forEach { ticket ->
                    TicketCard(
                        ticket = ticket,
                        navController = navController,
                        loginViewModel = loginViewModel,
                        buyTicketViewModel = buyTicketViewModel
                    )
                }
            }
        }
    }
}


// --- ROUTE SELECTION CARD ---
@Composable
fun RouteSelectionCard(
    navController: NavController,
    routes: List<RouteResponse>,
    selectedRoute: RouteResponse?,
    onRouteSelected: (RouteResponse) -> Unit,
    isLoading: Boolean,
    errorMessage: String?
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Bạn muốn đi tuyến nào:",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppDarkGray
            )
            Spacer(modifier = Modifier.height(12.dp))

            Box {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clickable(enabled = !isLoading && errorMessage == null) { expanded = !expanded },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, AppMediumGray.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = Icons.Default.Route,
                                contentDescription = "Route",
                                tint = AppMediumGray
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            when {
                                isLoading -> Text("Đang tải các tuyến...", color = AppMediumGray, fontSize = 16.sp)
                                errorMessage != null -> Text("Lỗi tải tuyến", color = Color.Red, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                else -> Text(
                                    text = selectedRoute?.routeName ?: "Chọn tuyến Metro",
                                    color = if (selectedRoute != null) AppDarkGray else AppMediumGray,
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        if (selectedRoute != null) {
                            IconButton(
                                onClick = {
                                    navController.navigate(Screen.StationSelection.route)
                                },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Chọn ga",
                                    tint = BluePrimary
                                )
                            }
                        } else {
                            Icon(
                                imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = "Mở danh sách tuyến",
                                tint = AppMediumGray
                            )
                        }
                    }
                }

                DropdownMenu(
                    expanded = expanded && !isLoading && errorMessage == null,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    routes.forEach { route ->
                        DropdownMenuItem(
                            text = { Text(route.routeName) },
                            onClick = {
                                onRouteSelected(route)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

// --- TICKET CARD ---
@Composable
fun TicketCard(
    ticket: TicketType,
    navController: NavController,
    loginViewModel: LoginViewModel,
    buyTicketViewModel: BuyTicketViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (ticket.name != "Vé đơn") {
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
                        // Sử dụng hàm định dạng từ ViewModel
                        text = buyTicketViewModel.getFormattedPrice(ticket.price.toLong()),
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
