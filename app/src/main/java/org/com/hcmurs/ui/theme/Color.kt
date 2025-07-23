package org.com.hcmurs.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

//val GreenPrimary = Color(0xFF347433)
val PrimaryGreen = Color(0xFF2196F3)
val DarkGreen = Color(0xFF1976D2)
val BluePrimary = Color(0xFF2196F3)
val AppLightGray = Color(0xFFF5F5F5)  // Màu xanh chủ đạo
val BlueLight = Color(0xFF64B5F6)       // Màu xanh nhạt hơn, dùng cho primaryContainer
val BlueDark = Color(0xFF1976D2)
val LightOrange = Color(0xFFFF6F3C)
val LightYellow = Color(0xFFFFC107)
val LightBeige = Color(0xFFB4D2BA)
val PaleYellow = Color(0xFFDCE2AA)
val EarthBrown = Color(0xFFB57F50)
val ErrorRed = Color(0xFFD32F2F)
val LightGreen = Color(0xFF64B5F6)
val SecondaryGreen = Color(0xFF2196F3)


val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,
    background = Color.White,
    onBackground = DarkGreen,
    surface = LightBeige,
    onSurface = DarkGreen,
    secondary = EarthBrown,
    onSecondary = Color.White
)

val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.Black,
    secondary = EarthBrown,
    onSecondary = Color.White,
    background = Color.White,
    surface = LightBeige,
)
