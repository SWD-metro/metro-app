package org.com.hcmurs.ui.components.buyticket

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.com.hcmurs.repositories.order.OrderInfo

@Composable
fun PaymentInfoSection(orderInfo: OrderInfo) {
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
                text = "Thông tin thanh toán",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1565C0)
            )
            Spacer(modifier = Modifier.height(16.dp))
            PaymentInfoRow(label = "Sản phẩm:", value = orderInfo.ticketType)
            Spacer(modifier = Modifier.height(12.dp))
            PaymentInfoRow(label = "Đơn giá:", value = orderInfo.unitPrice)
            Spacer(modifier = Modifier.height(12.dp))
            PaymentInfoRow(label = "Số lượng:", value = orderInfo.quantity.toString())
            Spacer(modifier = Modifier.height(12.dp))
            PaymentInfoRow(label = "Thành tiền:", value = orderInfo.totalPrice)
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFE0E0E0))
            Spacer(modifier = Modifier.height(16.dp))
            PaymentInfoRow(label = "Tổng giá tiền:", value = orderInfo.totalPrice, isTotal = true)
            Spacer(modifier = Modifier.height(12.dp))
            PaymentInfoRow(label = "Thành tiền:", value = orderInfo.totalPrice, isTotal = true)
        }
    }
}

@Composable
private fun PaymentInfoRow(
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
            color = Color(0xFF333333)
        )
        Text(
            text = value,
            fontSize = if (isTotal) 15.sp else 14.sp,
            fontWeight = if (isTotal) FontWeight.Medium else FontWeight.Normal,
            color = Color(0xFF333333)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentInfoSectionPreview() {
    val sampleOrder = OrderInfo("Vé 1 ngày", "40.000đ", 1, "40.000đ", "", "")
    PaymentInfoSection(orderInfo = sampleOrder)
}