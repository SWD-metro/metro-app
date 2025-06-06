package org.com.hcmurs.ui.screens

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.navigation.NavHostController
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import org.com.hcmurs.R // Đảm bảo đúng package R của bạn
import androidx.compose.ui.input.pointer.pointerInput // Import này cần thiết
import androidx.compose.foundation.gestures.detectHorizontalDragGestures // Import này cần thiết
import org.com.hcmurs.Screen // Import sealed class Screen của bạn

@Composable
fun SplashScreen(
    navController: NavHostController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Màu nền trắng dự phòng
            .pointerInput(Unit) { // Thêm modifier pointerInput để phát hiện cử chỉ
                detectHorizontalDragGestures { change, dragAmount ->
                    change.consume()
                    if (dragAmount < -50) { // Vuốt sang trái đủ xa (ví dụ -50 pixels)
                        navController.navigate(Screen.Login.route) { // Điều hướng đến màn hình Login
                            popUpTo(Screen.Splash.route) { inclusive = true } // Xóa Splash khỏi back stack
                        }
                    }
                }
            }
    ) {
        // 1. Ảnh nền full màn hình và bị blur
        Image(
            painter = painterResource(id = R.drawable.splash), // Thay bằng tên file ảnh của bạn
            contentDescription = null, // Đây là ảnh nền, không cần contentDescription cho accessibility
            contentScale = ContentScale.Crop, // Crop để ảnh tràn ra toàn bộ Box
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // Áp dụng hiệu ứng blur chỉ nếu API level >= S (31)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        renderEffect = BlurEffect(radiusX = 8.dp.toPx(), radiusY = 8.dp.toPx()) // Tăng độ mờ lên 8dp
                    }
                }
        )

        // 2. Lớp phủ (Overlay) màu đen nhẹ để tăng độ tương phản cho chữ
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)) // Lớp phủ màu đen với độ trong suốt 30%
        )

        // 3. Nội dung chữ và các thành phần khác nằm phía trên (Column)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding() // Đảm bảo nội dung không bị che bởi status/navigation bars
                .padding(horizontal = 32.dp, vertical = 20.dp), // Padding tổng thể cho nội dung
            horizontalAlignment = Alignment.CenterHorizontally, // Căn giữa nội dung theo chiều ngang
            verticalArrangement = Arrangement.SpaceBetween // Đẩy nội dung lên trên, "swipe" xuống dưới
        ) {
            // Spacer này để đẩy toàn bộ nội dung Column xuống dưới một chút từ mép trên màn hình.
            // Điều chỉnh giá trị này để căn chỉnh vị trí logo và chữ.
            Spacer(modifier = Modifier.height(80.dp)) // Đã điều chỉnh từ 0.dp để tạo khoảng trống cho logo

            // Phần nội dung chữ chính và Logo
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Thêm Logo ở đây
                Image(
                    painter = painterResource(id = R.drawable.hurcremovebg), // Đảm bảo R.drawable.hurc tồn tại
                    contentDescription = "Logo HCMC Metro", // Mô tả logo
                    modifier = Modifier
                        .size(300.dp) // Điều chỉnh kích thước logo
                        .padding(bottom = 20.dp) // Khoảng cách dưới logo
                )
                Text(
                    text = "A safe and convenient service for all family members",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    lineHeight = 36.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Don't worry about the problems and dangers of traveling for yourself and your family members because we want comfort and convenience for you",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(alpha = 0.8f),
                    lineHeight = 24.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Phần "swipe left to start"
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Swipe Left",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Swipe Left",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Swipe Left",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "swipe left to start",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_8_pro")
@Composable
fun SplashScreenPreview() {
    val navController = rememberNavController()
    SplashScreen(
        navController = navController
    )
}