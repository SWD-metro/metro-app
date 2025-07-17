package org.com.hcmurs.ui.screens.metro.buyticket

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

// Màu sắc được định nghĩa để nhất quán với OrderInfoScreenContent
private val BackgroundColor = Color(0xFFF5F5F5)
private val CardBackgroundColor = Color.White
private val PrimaryTextColor = Color(0xFF1A237E) // Màu xanh đậm cho tiêu đề
private val SecondaryTextColor = Color(0xFF333333) // Màu chữ thông thường
private val HintTextColor = Color(0xFF999999) // Màu chữ gợi ý/disabled
private val AccentGreen = Color(0xFF4CAF50) // Màu xanh lá cây cho nút thanh toán, checkmark
private val AccentBlue = Color(0xFF4A90E2) // Màu xanh dương cho điều khoản
private val WarningRed = Color(0xFFE53935) // Màu đỏ cho lưu ý quan trọng

data class OrderInfo(
    val ticketType: String,
    val unitPrice: String,
    val quantity: Int,
    val totalPrice: String,
    val validity: String,
    val note: String
)
data class PaymentMethod(
    val id: String,
    val name: String,
    val icon: @Composable () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderInfoScreen(
    navController: NavHostController,
    viewModel: OrderInfoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var selectedPaymentMethod by remember { mutableStateOf<PaymentMethod?>(null) }

    // Available payment methods
    val paymentMethods = remember {
        listOf(
            PaymentMethod(
                id = "momo",
                name = "Ví MoMo",
                icon = {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = "MoMo",
                        tint = Color(0xFFE91E63), // Momo Pink
                        modifier = Modifier.size(24.dp)
                    )
                }
            ),
            PaymentMethod(
                id = "zalopay",
                name = "ZaloPay",
                icon = {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = "ZaloPay",
                        tint = Color(0xFF0066CC), // ZaloPay Blue
                        modifier = Modifier.size(24.dp)
                    )
                }
            ),
            PaymentMethod(
                id = "vnpay",
                name = "VNPay",
                icon = {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = "VNPay",
                        tint = Color(0xFF1976D2), // VNPay Blue
                        modifier = Modifier.size(24.dp)
                    )
                }
            )
        )
    }
    // Derive OrderInfo from fetched TicketType
    val orderInfo = remember(uiState.ticketType) {
        val ticket = uiState.ticketType
        if (ticket != null) {
            val validityText = when (ticket.validityDuration) {
                "ONE_DAY" -> "24h kể từ thời điểm kích hoạt"
                "THREE_DAYS" -> "72h kể từ thời điểm kích hoạt"
                "ONE_WEEK" -> "7 ngày kể từ thời điểm kích hoạt"
                "ONE_MONTH" -> "30 ngày kể từ thời điểm kích hoạt"
                "SINGLE" -> "Sử dụng một lần"
                else -> "Theo quy định"
            }
            val noteText = when (ticket.name) {
                "One Day", "Three Days", "One Week", "One Month" -> "Tự động kích hoạt sau 30 ngày kể từ ngày mua vé"
                "Student Monthly" -> "Tự động kích hoạt sau 30 ngày kể từ ngày mua vé. Chỉ dành cho học sinh, sinh viên có thẻ hợp lệ"
                else -> "Vui lòng xem chi tiết tại quầy vé"
            }
            OrderInfo(
                ticketType = ticket.description,
                unitPrice = "${ticket.price}đ",
                quantity = 1, // Assuming quantity is always 1 for now
                totalPrice = "${ticket.price}đ",
                validity = validityText,
                note = noteText
            )
        } else {
            // Default or loading state for OrderInfo
            OrderInfo(
                ticketType = "Đang tải...",
                unitPrice = "0đ",
                quantity = 0,
                totalPrice = "0đ",
                validity = "Đang tải...",
                note = "Đang tải..."
            )
        }
    }

    var isTicketInfoExpanded by remember { mutableStateOf(true) }

    // Loại bỏ Scaffold và TopBar
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor) // Nền màu xám nhạt
            .verticalScroll(rememberScrollState()) // Có thể cuộn
    ) {
        // Có thể thêm Spacer ở đầu nếu muốn có khoảng trống từ mép trên cùng
        Spacer(modifier = Modifier.height(16.dp))

        // Payment Method Section
        PaymentMethodSection(
            selectedPaymentMethod = selectedPaymentMethod,
            paymentMethods = paymentMethods,
            onPaymentMethodSelected = { selectedPaymentMethod = it }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Payment Info Section
        PaymentInfoSection(orderInfo = orderInfo)

        Spacer(modifier = Modifier.height(16.dp))

        // Ticket Info Section
        TicketInfoSection(
            orderInfo = orderInfo,
            isExpanded = isTicketInfoExpanded,
            onExpandClick = { isTicketInfoExpanded = !isTicketInfoExpanded }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Terms Text
        Text(
            text = "Bằng việc bấm thanh toán, bạn đồng ý với điều khoản của Metro",
            fontSize = 12.sp,
            color = AccentBlue, // Màu xanh dương cho điều khoản
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { /* Handle terms click */ }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Payment Button
        Button(
            onClick = {
                // Handle payment action
                if (selectedPaymentMethod != null) {
                    // Proceed with payment
                } else {
                    // Show message to select payment method
                }
            },
            enabled = selectedPaymentMethod != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp), // Áp dụng padding horizontal
            shape = RoundedCornerShape(28.dp), // Bo góc 28.dp cho nút
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentGreen, // Màu xanh lá
                disabledContainerColor = HintTextColor.copy(alpha = 0.5f) // Màu xám mờ khi disabled
            )
        ) {
            Text(
                text = "Thanh toán: ${orderInfo.totalPrice}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// Xóa OrderInfoTopBar vì không dùng Scaffold
// @OptIn(ExperimentalMaterial3Api::class)
// @Composable
// fun OrderInfoTopBar(
//     title: String,
//     onBackClick: () -> Unit
// ) {
//     CenterAlignedTopAppBar(
//         title = {
//             Text(
//                 text = title,
//                 color = PrimaryTextColor,
//                 fontSize = 18.sp,
//                 fontWeight = FontWeight.Medium
//             )
//         },
//         navigationIcon = {
//             IconButton(onClick = onBackClick) {
//                 Icon(
//                     imageVector = Icons.Default.ArrowBack,
//                     contentDescription = "Back",
//                     tint = PrimaryTextColor
//                 )
//             }
//         },
//         colors = TopAppBarDefaults.topAppBarColors(
//             containerColor = Color.White
//         )
//     )
// }


@Composable
fun PaymentMethodSection(
    selectedPaymentMethod: PaymentMethod?,
    paymentMethods: List<PaymentMethod>,
    onPaymentMethodSelected: (PaymentMethod) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), // Padding horizontal cho Card
        shape = RoundedCornerShape(12.dp), // Bo góc 12.dp
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor), // Nền trắng
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp) // Đổ bóng nhẹ
    ) {
        Column(
            modifier = Modifier.padding(16.dp) // Padding nội dung Card
        ) {
            Text(
                text = "Phương thức thanh toán",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryTextColor // Màu xanh đậm
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedPaymentMethod == null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Logic chọn phương thức thanh toán
                            onPaymentMethodSelected(paymentMethods.first()) // Chọn mặc định
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = "Payment Method",
                            tint = HintTextColor, // Màu xám cho icon khi chưa chọn
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Chọn phương thức thanh toán",
                            fontSize = 14.sp,
                            color = HintTextColor // Màu xám
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Arrow Right",
                        tint = HintTextColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Logic thay đổi phương thức thanh toán
                            val currentIndex = paymentMethods.indexOf(selectedPaymentMethod)
                            val nextIndex = (currentIndex + 1) % paymentMethods.size
                            onPaymentMethodSelected(paymentMethods[nextIndex])
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        selectedPaymentMethod.icon()
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = selectedPaymentMethod.name,
                            fontSize = 14.sp,
                            color = SecondaryTextColor, // Màu chữ thông thường
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = AccentGreen, // Màu xanh lá cho checkmark
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Change",
                            tint = HintTextColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun PaymentInfoSection(orderInfo: OrderInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Thông tin thanh toán",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryTextColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            PaymentInfoRow(
                label = "Sản phẩm:",
                value = orderInfo.ticketType
            )

            Spacer(modifier = Modifier.height(12.dp))

            PaymentInfoRow(
                label = "Đơn giá:",
                value = orderInfo.unitPrice
            )

            Spacer(modifier = Modifier.height(12.dp))

            PaymentInfoRow(
                label = "Số lượng:",
                value = orderInfo.quantity.toString()
            )

            Spacer(modifier = Modifier.height(12.dp))

            PaymentInfoRow(
                label = "Thành tiền:",
                value = orderInfo.totalPrice
            )

            Spacer(modifier = Modifier.height(16.dp))

            Divider(color = HintTextColor.copy(alpha = 0.3f)) // Divider màu xám nhạt

            Spacer(modifier = Modifier.height(16.dp))

            PaymentInfoRow(
                label = "Tổng giá tiền:",
                value = orderInfo.totalPrice,
                isTotal = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            PaymentInfoRow(
                label = "Thành tiền:",
                value = orderInfo.totalPrice,
                isTotal = true // Có vẻ dòng này bị trùng, có thể xem xét bỏ bớt
            )
        }
    }
}

@Composable
fun PaymentInfoRow(
    label: String,
    value: String,
    isTotal: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = if (isTotal) 15.sp else 14.sp,
            fontWeight = if (isTotal) FontWeight.Medium else FontWeight.Normal,
            color = if (isTotal) PrimaryTextColor else SecondaryTextColor // Màu đậm hơn cho tổng cộng
        )
        Text(
            text = value,
            fontSize = if (isTotal) 15.sp else 14.sp,
            fontWeight = if (isTotal) FontWeight.Medium else FontWeight.Normal,
            color = if (isTotal) PrimaryTextColor else SecondaryTextColor // Màu đậm hơn cho tổng cộng
        )
    }
}

@Composable
fun TicketInfoSection(
    orderInfo: OrderInfo,
    isExpanded: Boolean,
    onExpandClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandClick() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Thông tin ${orderInfo.ticketType}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryTextColor
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = PrimaryTextColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))

                TicketDetailRow(
                    label = "Loại vé:",
                    value = orderInfo.ticketType
                )

                Spacer(modifier = Modifier.height(12.dp))

                TicketDetailRow(
                    label = "HSD:",
                    value = orderInfo.validity
                )

                Spacer(modifier = Modifier.height(12.dp))

                TicketDetailRow(
                    label = "Lưu ý:",
                    value = orderInfo.note,
                    valueColor = WarningRed // Màu đỏ cho lưu ý
                )
            }
        }
    }
}

@Composable
fun TicketDetailRow(
    label: String,
    value: String,
    valueColor: Color = SecondaryTextColor // Màu chữ thông thường cho giá trị
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = HintTextColor // Màu xám cho label
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            color = valueColor,
            lineHeight = 20.sp
        )
    }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun OrderInfoScreenPreview() {
//    val navController = rememberNavController()
//    OrderInfoScreen(
//        navController = navController,
//    )
//}