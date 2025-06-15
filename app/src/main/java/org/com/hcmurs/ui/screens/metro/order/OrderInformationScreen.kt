package org.com.hcmurs.ui.screens.metro.buyticket

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.com.hcmurs.repositories.order.OrderInfo
import org.com.hcmurs.repositories.payment.PaymentMethod
import org.com.hcmurs.ui.components.AppBottomNavigationBar
import org.com.hcmurs.ui.components.buyticket.PaymentInfoSection
import org.com.hcmurs.ui.components.topNav.CustomTopAppBar
import org.com.hcmurs.ui.components.buyticket.TicketInfoSection
import org.com.hcmurs.ui.screens.metro.buyticket.components.PaymentMethodSection
import org.com.hcmurs.ui.screens.metro.order.OrderInfoViewModel


@Composable
fun OrderInfoScreen(
    navController: NavHostController,
    viewModel: OrderInfoViewModel = hiltViewModel()
) {
    val selectedPaymentMethod by viewModel.selectedPaymentMethod.collectAsState()
    val orderInfo by viewModel.orderInfo.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val paymentMethods = remember {
        listOf(
            PaymentMethod(id = "momo", name = "Ví MoMo", icon = { /* Icon cho MoMo */ }),
            PaymentMethod(id = "zalopay", name = "ZaloPay", icon = { /* Icon cho ZaloPay */ }),
            PaymentMethod(id = "vnpay", name = "VNPay", icon = { /* Icon cho VNPay */ })
        )
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Chi tiết vé",
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color(0xFF1565C0) // Màu xanh dương đậm
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
    ) { paddingValues ->
        OrderInfoScreenContent(
            paddingValues = paddingValues,
            orderInfo = orderInfo,
            paymentMethods = paymentMethods,
            selectedPaymentMethod = selectedPaymentMethod,
            onPaymentMethodSelected = { viewModel.onPaymentMethodSelected(it) },
            onPayClick = {
            }
        )
    }
}

@Composable
private fun OrderInfoScreenContent(
    paddingValues: PaddingValues,
    orderInfo: OrderInfo,
    paymentMethods: List<PaymentMethod>,
    selectedPaymentMethod: PaymentMethod?,
    onPaymentMethodSelected: (PaymentMethod) -> Unit,
    onPayClick: () -> Unit
) {
    var isTicketInfoExpanded by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        PaymentMethodSection(
            selectedPaymentMethod = selectedPaymentMethod,
            paymentMethods = paymentMethods,
            onPaymentMethodSelected = onPaymentMethodSelected
        )
        Spacer(modifier = Modifier.height(16.dp))

        PaymentInfoSection(orderInfo = orderInfo)

        Spacer(modifier = Modifier.height(16.dp))

        TicketInfoSection(
            orderInfo = orderInfo,
            isExpanded = isTicketInfoExpanded,
            onExpandClick = { isTicketInfoExpanded = !isTicketInfoExpanded }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Bằng việc bấm thanh toán, bạn đồng ý với điều khoản của Metro",
            fontSize = 12.sp,
            color = Color(0xFF4A90E2),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { /* Xử lý khi nhấn vào điều khoản */ }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Nút thanh toán
        Button(
            onClick = onPayClick,
            enabled = selectedPaymentMethod != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50), // Màu xanh lá
                disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
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


// --- Composable để xem trước (Preview) ---
@Preview(showBackground = true)
@Composable
fun OrderInfoScreenPreview() {
    val sampleOrder = OrderInfo("Vé 1 ngày", "40.000đ", 1, "40.000đ", "24h kể từ thời điểm kích hoạt", "Ghi chú...")
    val samplePaymentMethods = listOf(
        PaymentMethod("momo", "Ví MoMo", { Icon(Icons.Default.CreditCard, "momo") })
    )
    var selectedMethod by remember { mutableStateOf<PaymentMethod?>(null) }

    Scaffold(
        topBar = {
            CustomTopAppBar(title = "Chi tiết vé", navigationIcon = {
                IconButton(onClick = {}) { Icon(Icons.Default.ArrowBack, "Back") }
            })
        }
    ) { padding ->
        OrderInfoScreenContent(
            paddingValues = padding,
            orderInfo = sampleOrder,
            paymentMethods = samplePaymentMethods,
            selectedPaymentMethod = selectedMethod,
            onPaymentMethodSelected = { selectedMethod = it },
            onPayClick = {}
        )
    }
}