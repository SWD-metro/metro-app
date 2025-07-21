package org.com.hcmurs.ui.screens.stationselection

import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.com.hcmurs.FareMatrix
import org.com.hcmurs.R
import org.com.hcmurs.Screen
import org.com.hcmurs.Station
import org.com.hcmurs.ui.screens.metro.buyticket.FareMatrixViewModel
import org.com.hcmurs.ui.theme.BlueDark
import org.com.hcmurs.ui.theme.BluePrimary
import org.com.hcmurs.ui.theme.AppLightGray

private val TextPrimaryColor = Color(0xFF212121)
private val TextSecondaryColor = Color(0xFF757575)
private val CardBackgroundColor = Color.White
private val DividerColor = Color.Black.copy(alpha = 0.08f)
private val AccentGreen = Color(0xFF4CAF50) // For positive highlights

data class LocalPaymentMethod(
    val id: Int,
    val name: String,
    val iconRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderFareInfoScreen(
    navController: NavHostController,
    entryStationId: Int,
    exitStationId: Int,
    fareMatrixViewModel: FareMatrixViewModel,
    stationViewModel: StationSelectionViewModel
) {
    val fareMatrixUiState by fareMatrixViewModel.uiState.collectAsState()
    val stationUiState by stationViewModel.uiState.collectAsState()

    val fareInfo = fareMatrixUiState.calculatedFare?.data
    val entryStation = stationUiState.stations.find { it.stationId == entryStationId }
    val exitStation = stationUiState.stations.find { it.stationId == exitStationId }

    var showPaymentSheet by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    val paymentMethods = listOf(
        LocalPaymentMethod(1, "VNPAY", R.drawable.ic_vnpay),
        LocalPaymentMethod(2, "MoMo", R.drawable.ic_momo)
    )
    var selectedPaymentMethod by remember { mutableStateOf(paymentMethods.first()) }
    val context = LocalContext.current

    LaunchedEffect(key1 = fareMatrixUiState.createOrderResponse, key2 = fareMatrixUiState.createOrderError) {
        val response = fareMatrixUiState.createOrderResponse
        if (response != null) {
            if (response.status == 200 && response.data != null) {
                Toast.makeText(context, "Tạo đơn hàng thành công!", Toast.LENGTH_SHORT).show()
                navController.navigate(Screen.MyTicket.route)
            } else {
                Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
            }
            fareMatrixViewModel.clearCreateOrderStatus()
        }

        val error = fareMatrixUiState.createOrderError
        if (error != null) {
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            fareMatrixViewModel.clearCreateOrderStatus()
        }
    }

    // Modals and Dialogs are drawn outside the main layout flow
    if (showPaymentSheet) {
        PaymentMethodBottomSheet(
            paymentMethods = paymentMethods,
            selectedMethod = selectedPaymentMethod,
            onDismiss = { showPaymentSheet = false },
            onSelectMethod = { method ->
                selectedPaymentMethod = method
                showPaymentSheet = false
            }
        )
    }

    if (showTermsDialog) {
        TermsAndConditionsDialog(onDismiss = { showTermsDialog = false })
    }

    // --- Main Screen Layout (replacing Scaffold) ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, AppLightGray.copy(alpha = 0.6f)),
                    startY = 0f,
                    endY = 1500f
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween // Pushes content to top/bottom
    ) {
        // --- Custom Top App Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimaryColor
                )
            }
            Text(
                text = "Xác nhận & Thanh toán",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BlueDark,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            // Empty space to balance the back button on the left
            Spacer(modifier = Modifier.width(48.dp))
        }
        Divider(color = DividerColor, thickness = 1.dp)
        // --- End Custom Top App Bar ---

        // --- Scrollable Content Area ---
        // Show loading indicator if either VM is loading
        if (fareMatrixUiState.isLoading || stationUiState.isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BluePrimary, modifier = Modifier.size(48.dp))
            }
        } else if (fareInfo != null && entryStation != null && exitStation != null) {
            // Display content when data is loaded
            Column(
                modifier = Modifier
                    .weight(1f) // Takes up remaining space between top bar and bottom bar
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 24.dp), // Consistent padding for content
                verticalArrangement = Arrangement.spacedBy(24.dp), // More space between sections
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PaymentInfoSection(fare = fareInfo, entryStation = entryStation, exitStation = exitStation)
                TicketDetailsSection(entryStation = entryStation, exitStation = exitStation)
                PaymentMethodSection(
                    selectedMethod = selectedPaymentMethod,
                    onClick = { showPaymentSheet = true }
                )
            }
        } else {
            // Display error message if data failed to load
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = fareMatrixUiState.errorMessage ?: "Không thể tải thông tin. Vui lòng thử lại.",
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        // --- End Scrollable Content Area ---

        // --- Fixed Bottom Bar ---
        if (fareInfo != null) {
            PaymentBottomBar(
                price = fareInfo.price,
                isLoading = fareMatrixUiState.isCreatingOrder,
                onPayClick = {
                    fareMatrixViewModel.createSingleOrder(
                        fareMatrixId = fareInfo.fareMatrixId,
                        paymentMethodId = selectedPaymentMethod.id
                    )
                },
                onTermsClick = { showTermsDialog = true }
            )
        }
        // --- End Fixed Bottom Bar ---
    }
}

@Composable
private fun PaymentMethodSection(selectedMethod: LocalPaymentMethod, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Phương thức thanh toán", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimaryColor)
        Spacer(Modifier.height(12.dp)) // Increased spacing
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(16.dp), // More rounded corners
            colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Increased elevation
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp), // Increased padding inside card
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = selectedMethod.iconRes),
                        contentDescription = selectedMethod.name,
                        modifier = Modifier.size(32.dp) // Larger icon
                    )
                    Spacer(Modifier.width(16.dp)) // Increased spacing
                    Text(selectedMethod.name, color = TextPrimaryColor, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                }
                Icon(Icons.Filled.ChevronRight, contentDescription = "Select", tint = TextSecondaryColor.copy(alpha = 0.7f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentMethodBottomSheet(
    paymentMethods: List<LocalPaymentMethod>,
    selectedMethod: LocalPaymentMethod,
    onDismiss: () -> Unit,
    onSelectMethod: (LocalPaymentMethod) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                "Chọn phương thức thanh toán",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BlueDark,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(24.dp)) // Increased spacing
            paymentMethods.forEach { method ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectMethod(method) }
                        .padding(vertical = 16.dp), // Increased vertical padding
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = method.iconRes),
                        contentDescription = method.name,
                        modifier = Modifier.size(36.dp) // Larger icon in sheet
                    )
                    Spacer(modifier = Modifier.width(20.dp)) // Increased spacing
                    Text(method.name, modifier = Modifier.weight(1f), fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    if (method == selectedMethod) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = "Selected",
                            tint = AccentGreen, // Use AccentGreen for selected state
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TermsAndConditionsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Điều khoản dịch vụ", fontWeight = FontWeight.Bold, color = BlueDark, fontSize = 20.sp) },
        text = {
            Text(
                "Bằng việc sử dụng dịch vụ, bạn đồng ý tuân thủ tất cả các quy định về vận chuyển hành khách công cộng. " +
                        "Vé đã mua không thể hoàn trả. Vui lòng giữ vé cẩn thận để xuất trình khi có yêu cầu. " +
                        "Mọi hành vi gian lận sẽ bị xử lý theo quy định của pháp luật. " +
                        "Cảm ơn bạn đã sử dụng dịch vụ của Metro.",
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = TextPrimaryColor
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Đã hiểu", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Composable
private fun PaymentBottomBar(
    price: Int,
    onTermsClick: () -> Unit,
    onPayClick: () -> Unit,
    isLoading: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp) // Prominent shadow
            .background(CardBackgroundColor)
            .padding(20.dp) // Increased padding
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            val annotatedString = buildAnnotatedString {
                append("Bằng việc bấm thanh toán, bạn đồng ý với ")
                pushStringAnnotation(tag = "TERMS", annotation = "TERMS")
                withStyle(
                    style = SpanStyle(
                        color = BlueDark,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("Điều khoản & Điều kiện") // More explicit text
                }
                pop()
                append(" của Metro.")
            }

            ClickableText(
                text = annotatedString,
                onClick = { offset ->
                    annotatedString.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
                        .firstOrNull()?.let {
                            onTermsClick()
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                style = LocalTextStyle.current.copy(
                    fontSize = 13.sp, // Slightly larger
                    color = TextSecondaryColor,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(Modifier.height(16.dp)) // Increased spacing
            Button(
                onClick = onPayClick,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp), // More rounded corners
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(28.dp), color = Color.White, strokeWidth = 3.dp) // Larger and thicker indicator
                } else {
                    Text("Thanh toán: ${price}đ", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White) // Larger text
                }
            }
        }
    }
}

@Composable
private fun PaymentInfoSection(fare: FareMatrix, entryStation: Station, exitStation: Station) {
    val routeName = "${entryStation.name} – ${exitStation.name}"

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Thông tin thanh toán", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimaryColor)
        Spacer(Modifier.height(12.dp)) // Increased spacing
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp), // More rounded corners
            colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Increased elevation
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { // Increased padding and spacing
                InfoRow(label = "Sản phẩm:", value = "Vé lượt: $routeName")
                Divider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp)) // Thinner vertical padding for divider
                InfoRow(label = "Đơn giá:", value = "${fare.price}đ")
                Divider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                InfoRow(label = "Số lượng:", value = "1")
                Divider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                InfoRow(label = "Thành tiền:", value = "${fare.price}đ")
                Divider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                InfoRow(label = "Tổng giá tiền:", value = "${fare.price}đ", isTotal = true)
            }
        }
    }
}

@Composable
private fun TicketDetailsSection(entryStation: Station, exitStation: Station) {
    val routeName = "${entryStation.name} – ${exitStation.name}"

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Thông tin vé lượt", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimaryColor)
        Spacer(Modifier.height(12.dp)) // Increased spacing
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp), // More rounded corners
            colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Increased elevation
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) { // Increased padding and spacing
                InfoRow(label = "Loại vé:", value = "Vé lượt")
                InfoRow(label = "HSD:", value = "30 ngày kể từ ngày mua")
                InfoRow(label = "Lưu ý:", value = "Vé sử dụng một lần", valueColor = Color.Red)
                InfoRow(label = "Mô tả:", value = "Vé lượt: $routeName")
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, isTotal: Boolean = false, valueColor: Color? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextSecondaryColor,
            fontSize = if (isTotal) 16.sp else 15.sp, // Slightly larger for regular labels
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            color = valueColor ?: if (isTotal) BlueDark else TextPrimaryColor,
            fontSize = if (isTotal) 19.sp else 16.sp, // Larger for total, slightly larger for regular values
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}

