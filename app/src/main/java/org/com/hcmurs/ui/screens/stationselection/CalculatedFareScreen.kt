package org.com.hcmurs.ui.screens.stationselection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import org.com.hcmurs.FareMatrix
import org.com.hcmurs.Screen
import org.com.hcmurs.ui.screens.metro.buyticket.FareMatrixViewModel
import org.com.hcmurs.ui.theme.BlueDark
import org.com.hcmurs.ui.theme.BluePrimary
import org.com.hcmurs.ui.theme.AppLightGray


private val TextPrimaryColor = Color(0xFF212121)
private val TextSecondaryColor = Color(0xFF757575)
private val WarningColor = Color(0xFFD32F2F)
private val AccentOrange = Color(0xFFFF9800)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatedFareScreen(
    navController: NavHostController,
    entryStationId: Int,
    exitStationId: Int,
    viewModel: FareMatrixViewModel,
    stationViewModel: StationSelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val stationUiState by stationViewModel.uiState.collectAsState()

    val currentFareResponse = uiState.calculatedFare
    val entryStation = stationUiState.stations.find { it.stationId == entryStationId }
    val exitStation = stationUiState.stations.find { it.stationId == exitStationId }

    // Sử dụng Column làm layout gốc vì không cần Scaffold nữa
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, AppLightGray.copy(alpha = 0.6f)),
                    startY = 0f,
                    endY = 1500f
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween // Đẩy nội dung và nút ra hai phía
    ) {
        // Phần nội dung chính (Card hoặc Loading/Error)
        // Dùng Box để đảm bảo nó chiếm không gian và đẩy các nút xuống dưới
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = BluePrimary, modifier = Modifier.size(48.dp))
            } else if (currentFareResponse != null && entryStation != null && exitStation != null) {
                // Hiển thị Card chi tiết khi có dữ liệu
                FareDetailCard(
                    entryStationName = entryStation.name,
                    exitStationName = exitStation.name,
                    fare = currentFareResponse.data!!
                )
            } else {
                // Hiển thị lỗi
                Text(
                    text = uiState.errorMessage ?: "Không thể tính giá vé. Vui lòng thử lại.",
                    color = WarningColor,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Cụm nút hành động
        ActionButtons(
            navController = navController,
            entryStation = entryStation,
            exitStation = exitStation,
            isConfirmEnabled = currentFareResponse != null
        )
    }
}

@Composable
private fun ActionButtons(
    navController: NavHostController,
    entryStation: org.com.hcmurs.Station?,
    exitStation: org.com.hcmurs.Station?,
    isConfirmEnabled: Boolean
) {
    Column {
        Button (
            onClick = {
                if (entryStation != null && exitStation != null) {
                    navController.navigate(
                        Screen.OrderFareInfo.createRoute(
                            entryStationId = entryStation.stationId,
                            exitStationId = exitStation.stationId
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
            enabled = isConfirmEnabled
        ) {
            Text("Xác nhận mua vé", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, BluePrimary.copy(alpha = 0.5f))
        ) {
            Text("Chọn lại ga", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = BluePrimary)
        }
    }
}

@Composable
fun FareDetailCard(entryStationName: String, exitStationName: String, fare: FareMatrix) {
    Card (
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "HÀNH TRÌNH CỦA BẠN",
                fontSize = 14.sp,
                color = TextSecondaryColor,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StationDisplay(name = entryStationName, isEntry = true)
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "to",
                    tint = TextSecondaryColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(32.dp)
                )
                StationDisplay(name = exitStationName, isEntry = false)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Divider(color = AppLightGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoRow(title = "Hạn sử dụng:", value = "Trong ngày")
                InfoRow(title = "Lưu ý:", value = "Tự động kích hoạt sau 30 ngày kể từ ngày mua.", valueColor = WarningColor)
                InfoRow(title = "Mô tả:", value = "Vé cho phép di chuyển một lượt giữa ${entryStationName} và ${exitStationName}.")
            }
            Spacer(modifier = Modifier.height(24.dp))

            Divider(color = AppLightGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(24.dp))

            Text("TỔNG CỘNG", fontSize = 14.sp, color = TextSecondaryColor, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "${fare.price} đ",
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BlueDark
            )
        }
    }
}

@Composable
fun StationDisplay(name: String, isEntry: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(140.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(if (isEntry) BluePrimary else AccentOrange),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsBus,
                contentDescription = "Station",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = name,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 22.sp,
            color = TextPrimaryColor
        )
    }
}


@Composable
private fun InfoRow(title: String, value: String, valueColor: Color = TextPrimaryColor) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = TextSecondaryColor,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = valueColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
