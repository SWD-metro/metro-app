package org.com.hcmurs.ui.screens.metro.myticket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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

// Các hằng số màu sắc được sử dụng trong file
private val TextPrimaryColor = Color(0xFF212121)
private val TextSecondaryColor = Color(0xFF757575)
private val LightDividerColor = Color(0xFFE9EDF8)
private val CardBackgroundColor = Color(0xFFFFFFFF)
private val PrimaryActionButtonColor = Color(0xFF2196F3)

/**
 * Composable `YourTicket` chịu trách nhiệm hiển thị vé "chưa sử dụng" đầu tiên của người dùng
 * hoặc một lời nhắc đặt vé nếu không có vé nào hợp lệ.
 *
 * @param navController Controller để điều hướng giữa các màn hình.
 * @param viewModel ViewModel cung cấp trạng thái giao diện và dữ liệu vé.
 */
@Composable
fun YourTicket(
    navController: NavController,
    viewModel: MyTicketViewModel = hiltViewModel()
) {
    // Tự động gọi API để lấy danh sách vé khi Composable được hiển thị lần đầu tiên.
    LaunchedEffect(Unit) {
        // Giả định ViewModel có hàm `fetchOrders()` để tải dữ liệu.
        // Hãy đảm bảo tên hàm này khớp với tên trong ViewModel của bạn.
        viewModel.fetchUserOrders()
    }

    // Lắng nghe trạng thái (loading, error, success) từ ViewModel.
    val uiState by viewModel.uiState.collectAsState()

    // Từ danh sách vé, chỉ lấy vé đầu tiên có trạng thái "NOT_USED".
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
                .defaultMinSize(minHeight = 150.dp), // Đảm bảo đủ không gian cho các trạng thái
            contentAlignment = Alignment.Center
        ) {
            // Hiển thị giao diện dựa trên trạng thái hiện tại.
            when {
                // Đang tải dữ liệu
                uiState.isLoading -> {
                    CircularProgressIndicator(color = PrimaryGreen)
                }
                // Có lỗi xảy ra
                uiState.errorMessage != null -> {
                    Text(
                        text = stringResource(R.string.error_label, uiState.errorMessage!!),
                        color = Color.Red
                    )
                }
                // Tải thành công và có vé hợp lệ
                activeTicketOrder != null -> {
                    ActiveTicketCard(navController = navController, order = activeTicketOrder)
                }
                // Tải thành công nhưng không có vé hợp lệ
                else -> {
                    NoActiveTicketView(navController = navController)
                }
            }
        }
    }
}

/**
 * Hiển thị thẻ thông tin chi tiết cho một vé đang hoạt động.
 */
@Composable
private fun ActiveTicketCard(navController: NavController, order: OrderWithTicketDetails) {
    val ticket = order.ticket!! // Đảm bảo ticket không null

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            // Dòng Tên vé và Icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_ticket_info),
                    contentDescription = "Ticket Icon",
                    tint = PrimaryGreen,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = ticket.name, // Giả định tên vé đã được dịch hoặc không cần dịch
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryColor,
                    modifier = Modifier.weight(1f)
                )
            }

            Divider(Modifier.padding(vertical = 12.dp), color = LightDividerColor)

            // Các dòng thông tin chi tiết
            InfoRow(
                label = stringResource(R.string.order_code),
                value = "#${ticket.ticketCode}"
            )
            Spacer(Modifier.height(8.dp))
            InfoRow(
                label = stringResource(R.string.validity_label),
                value = "${formatValidityDate(ticket.validFrom)} - ${formatValidityDate(ticket.validUntil)}"
            )

            Spacer(Modifier.height(24.dp))

            // Nút điều hướng đến màn hình QR Code
            Button(
                onClick = {
                    navController.navigate(Screen.TicketQRCode.createRoute(ticket.ticketCode))
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryActionButtonColor),
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

/**
 * Hiển thị giao diện khi không có vé nào đang hoạt động.
 */
@Composable
private fun NoActiveTicketView(navController: NavController) {
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
                // Điều hướng tới màn hình mua vé
                navController.navigate("buy_ticket_screen")
            },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryActionButtonColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = stringResource(R.string.book_ticket_now),
                color = Color.White
            )
        }
    }
}

/**
 * Composable helper để hiển thị một dòng thông tin gồm nhãn và giá trị.
 */
@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            color = TextSecondaryColor,
            fontSize = 14.sp,
            modifier = Modifier.width(100.dp) // Điều chỉnh độ rộng của nhãn nếu cần
        )
        Text(
            text = value,
            color = TextPrimaryColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Định dạng chuỗi ngày tháng ISO 8601 thành "HH:mm dd/MM/yyyy".
 */
private fun formatValidityDate(dateString: String): String {
    return try {
        // Xử lý các múi giờ có dấu hai chấm (ví dụ: +07:00 -> +0700)
        val cleanedDateString = dateString.replace(Regex("(\\+|\\-)\\d{2}:(\\d{2})")) {
            "${it.groupValues[1]}${it.groupValues[2]}"
        }
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
        val date: Date = parser.parse(cleanedDateString) ?: return dateString

        val formatter = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
        formatter.timeZone = TimeZone.getDefault() // Sử dụng múi giờ của thiết bị
        formatter.format(date)
    } catch (e: Exception) {
        dateString.take(10) // Fallback: trả về phần ngày nếu có lỗi
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF0F0F0)
@Composable
fun YourTicketPreview() {
    val navController = rememberNavController()
    // Trong môi trường preview, không có Hilt ViewModel, nên nó sẽ hiển thị trạng thái `else`.
    // Để preview các trạng thái khác, bạn cần tạo ViewModel giả.
    YourTicket(navController = navController)
}
