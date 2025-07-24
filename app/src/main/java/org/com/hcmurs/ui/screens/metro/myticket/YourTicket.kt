package org.com.hcmurs.ui.screens.metro.myticket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Subway
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect // Thêm import này
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.com.hcmurs.R
import org.com.hcmurs.Screen
import org.com.hcmurs.repositories.apis.order.OrderWithTicketDetails
import org.com.hcmurs.ui.theme.PrimaryGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

val cardBackground = Color(0xFFFFFFFF)
val orange = Color(0xFFFF9800)
val primaryBlue = Color(0xFF2196F3)

@Composable
fun YourTicket(
    navController: NavController,
    viewModel: MyTicketViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {
    }

    val uiState by viewModel.uiState.collectAsState()

    val activeTicketOrder = remember(uiState.orders) {
        uiState.orders.firstOrNull { it.ticket?.status.equals("NOT_USED", ignoreCase = true) }
    }

    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Text(
            text = stringResource(R.string.your_tickets),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 150.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(color = PrimaryGreen)
                }
                uiState.errorMessage != null -> {
                    Text(
                        text = stringResource(R.string.error_label, uiState.errorMessage!!),
                        color = Color.Red
                    )
                }
                activeTicketOrder != null -> {
                    ActiveTicketCard(navController = navController, order = activeTicketOrder)
                }
                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.no_ticket_message),
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                navController.navigate("buy_ticket_screen")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.book_ticket_now),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}


// ... Các hàm còn lại giữ nguyên ...
@Composable
private fun ActiveTicketCard(navController: NavController, order: OrderWithTicketDetails) {
    val ticket = order.ticket!!
    val unknownStation = stringResource(id = R.string.unknown_station)
    val stationInfo = remember(ticket.name) {
        val parts = ticket.name.removePrefix("Vé tuyến ").split(" - ")
        if (parts.size == 2) {
            Pair(parts[0], parts[1])
        } else {
            Pair(unknownStation, unknownStation)
        }
    }
    val departure = stationInfo.first
    val arrival = stationInfo.second

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = formatDisplayDate(ticket.validFrom),
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(orange, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.fastest_tag),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.departure_info, departure),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StationInfoColumn(stationType = stringResource(R.string.departure), stationName = departure, color = primaryBlue)
                Text(
                    text = stringResource(R.string.est_35_min),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                StationInfoColumn(stationType = stringResource(R.string.arrival), stationName = arrival, color = orange, horizontalAlignment = Alignment.End)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Subway,
                    contentDescription = "Metro Line",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.train_arrival_info, departure),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate(Screen.TicketQRCode.createRoute(ticket.ticketCode))
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.view_ticket_qr_code),
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun StationInfoColumn(
    stationType: String,
    stationName: String,
    color: Color,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start
) {
    Column(horizontalAlignment = horizontalAlignment) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stationType,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = stationName,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = if (horizontalAlignment == Alignment.Start) 16.dp else 0.dp)
        )
    }
}

private fun formatDisplayDate(dateString: String): String {
    return try {
        val cleanedDateString = dateString.replace(Regex("(\\+|\\-)\\d{2}:(\\d{2})")) {
            "${it.groupValues[1]}${it.groupValues[2]}"
        }
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
        val date: Date = parser.parse(cleanedDateString) ?: return dateString.take(10)

        val formatter = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale("vi", "VN"))
        formatter.timeZone = TimeZone.getDefault()
        formatter.format(date)
    } catch (e: Exception) {
        dateString.take(10)
    }
}

@Preview(showBackground = true)
@Composable
fun YourTicketPreview() {
    val navController = rememberNavController()
    YourTicket(navController = navController)
}