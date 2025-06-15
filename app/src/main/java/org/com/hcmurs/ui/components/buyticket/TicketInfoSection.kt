
package org.com.hcmurs.ui.components.buyticket

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import org.com.hcmurs.repositories.order.OrderInfo


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
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                    color = Color(0xFF1565C0)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = Color(0xFF1565C0),
                    modifier = Modifier.size(20.dp)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    TicketDetailRow(label = "Loại vé:", value = orderInfo.ticketType)
                    Spacer(modifier = Modifier.height(12.dp))
                    TicketDetailRow(label = "HSD:", value = orderInfo.validity)
                    Spacer(modifier = Modifier.height(12.dp))
                    TicketDetailRow(label = "Lưu ý:", value = orderInfo.note, valueColor = Color(0xFFE53935))
                }
            }
        }
    }
}

@Composable
private fun TicketDetailRow(
    label: String,
    value: String,
    valueColor: Color = Color(0xFF333333)
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF666666)
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

@Preview(showBackground = true)
@Composable
private fun TicketInfoSectionPreview() {
    var isExpanded by remember { mutableStateOf(true) }
    val sampleOrder = OrderInfo(
        ticketType = "Vé tháng",
        unitPrice = "300.000đ",
        quantity = 1,
        totalPrice = "300.000đ",
        validity = "30 ngày kể từ thời điểm kích hoạt",
        note = "Tự động kích hoạt sau 30 ngày kể từ ngày mua vé"
    )
    TicketInfoSection(
        orderInfo = sampleOrder,
        isExpanded = isExpanded,
        onExpandClick = { isExpanded = !isExpanded }
    )
}