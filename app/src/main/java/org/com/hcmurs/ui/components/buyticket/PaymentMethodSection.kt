// In file: .../buyticket/components/PaymentMethodSection.kt
package org.com.hcmurs.ui.screens.metro.buyticket.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.com.hcmurs.repositories.payment.PaymentMethod

@Composable
fun PaymentMethodSection(
    selectedPaymentMethod: PaymentMethod?,
    paymentMethods: List<PaymentMethod>,
    onPaymentMethodSelected: (PaymentMethod) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Phương thức thanh toán",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1565C0)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Dùng key để Compose nhận biết sự thay đổi và tạo hiệu ứng animation (nếu có)
            val currentMethod = selectedPaymentMethod ?: PaymentMethod(
                id = "placeholder",
                name = "Chọn phương thức thanh toán",
                icon = { Icon(Icons.Default.CreditCard, "Payment", tint = Color(0xFF4A90E2)) }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // Demo: chọn phương thức đầu tiên nếu chưa có, hoặc chuyển sang cái tiếp theo
                        val currentIndex = paymentMethods.indexOf(selectedPaymentMethod)
                        val nextIndex = if(currentIndex == -1) 0 else (currentIndex + 1) % paymentMethods.size
                        onPaymentMethodSelected(paymentMethods[nextIndex])
                    }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    currentMethod.icon()
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = currentMethod.name,
                        fontSize = 14.sp,
                        color = if (selectedPaymentMethod != null) Color(0xFF333333) else Color(0xFF999999),
                        fontWeight = if (selectedPaymentMethod != null) FontWeight.Medium else FontWeight.Normal
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selectedPaymentMethod != null) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Arrow Right",
                        tint = Color(0xFF999999),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentMethodSectionPreview() {
    val paymentMethods = listOf(
        PaymentMethod("momo", "Ví MoMo", { Icon(Icons.Default.CreditCard, "MoMo") })
    )
    var selectedMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    Column {
        PaymentMethodSection(
            selectedPaymentMethod = null,
            paymentMethods = paymentMethods,
            onPaymentMethodSelected = { selectedMethod = it }
        )
        Spacer(Modifier.height(16.dp))
        PaymentMethodSection(
            selectedPaymentMethod = paymentMethods.first(),
            paymentMethods = paymentMethods,
            onPaymentMethodSelected = { selectedMethod = it }
        )
    }
}