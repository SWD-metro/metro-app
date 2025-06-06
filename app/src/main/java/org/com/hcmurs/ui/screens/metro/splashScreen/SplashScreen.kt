package org.com.hcmurs.ui.screens

import android.os.Build
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.navigation.NavHostController
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import org.com.hcmurs.R
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import org.com.hcmurs.Screen

@Composable
fun SplashScreen(
    navController: NavHostController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(Unit) { // Thêm modifier pointerInput để phát hiện cử chỉ
                detectHorizontalDragGestures { change, dragAmount ->
                    change.consume()
                    if (dragAmount < -50) {
                        navController.navigate(Screen.Register.route) { // Điều hướng đến màn hình
                            popUpTo(Screen.Splash.route) { inclusive = true } // Xóa Splash khỏi back stack
                        }
                    }
                }
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        renderEffect = BlurEffect(radiusX = 8.dp.toPx(), radiusY = 8.dp.toPx())
                    }
                }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 32.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hurcremovebg),
                    contentDescription = "Logo HCMC Metro",
                    modifier = Modifier
                        .size(300.dp)
                        .padding(bottom = 20.dp)
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
            val infiniteTransition = rememberInfiniteTransition(label = "swipe_animation")
            val animatedAlpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ), label = "alpha_animation"
            )
            val animatedOffset by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = -10f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ), label = "offset_animation"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(x = animatedOffset.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Swipe Left",
                        tint = Color.White.copy(alpha = animatedAlpha),
                        modifier = Modifier.size(28.dp)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Swipe Left",
                        tint = Color.White.copy(alpha = animatedAlpha),
                        modifier = Modifier.size(28.dp)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Swipe Left",
                        tint = Color.White.copy(alpha = animatedAlpha),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "swipe left to start",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = animatedAlpha)
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