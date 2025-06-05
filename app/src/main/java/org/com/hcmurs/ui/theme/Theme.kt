// ui.theme.Theme.kt
package org.com.hcmurs.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.Color

// Import tất cả các màu từ file Color.kt
import org.com.hcmurs.ui.theme.BluePrimary
import org.com.hcmurs.ui.theme.BlueLight
import org.com.hcmurs.ui.theme.BlueDark
import org.com.hcmurs.ui.theme.AppWhite
import org.com.hcmurs.ui.theme.AppLightGray
import org.com.hcmurs.ui.theme.AppMediumGray
import org.com.hcmurs.ui.theme.AppDarkGray
import org.com.hcmurs.ui.theme.AppRedError
import org.com.hcmurs.ui.theme.AppLightRed
import org.com.hcmurs.ui.theme.AppCyan
// Import nếu bạn vẫn dùng các màu Purple/Pink
import org.com.hcmurs.ui.theme.Purple40
import org.com.hcmurs.ui.theme.Purple80
import org.com.hcmurs.ui.theme.PurpleGrey40
import org.com.hcmurs.ui.theme.PurpleGrey80
import org.com.hcmurs.ui.theme.Pink40
import org.com.hcmurs.ui.theme.Pink80


// --- Light Color Scheme của ứng dụng Metro ---
private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = AppWhite,
    primaryContainer = BlueLight,
    onPrimaryContainer = AppDarkGray,
    secondary = BlueDark,
    onSecondary = AppWhite,
    tertiary = AppCyan,
    onTertiary = AppWhite,

    background = AppLightGray,
    onBackground = AppDarkGray,
    surface = AppWhite,
    onSurface = AppDarkGray,
    surfaceVariant = AppLightGray.copy(alpha = 0.8f),
    onSurfaceVariant = AppMediumGray,

    error = AppRedError,
    onError = AppWhite,
    errorContainer = AppLightRed,
    onErrorContainer = AppRedError,
)

// --- Dark Color Scheme (bạn có thể tùy chỉnh thêm để khớp với light theme) ---
private val DarkColorScheme = darkColorScheme(
    primary = BlueLight,
    onPrimary = AppDarkGray,
    primaryContainer = BlueDark,
    onPrimaryContainer = AppWhite,
    secondary = AppCyan,
    onSecondary = AppDarkGray,
    tertiary = AppCyan,
    onTertiary = AppDarkGray,

    background = Color(0xFF121212), // Sử dụng màu trực tiếp nếu không định nghĩa trong Color.kt
    onBackground = AppWhite,
    surface = Color(0xFF1E1E1E),
    onSurface = AppWhite,
    surfaceVariant = Color(0xFF424242),
    onSurfaceVariant = AppLightGray,

    error = AppRedError,
    onError = AppWhite,
    errorContainer = AppLightRed,
    onErrorContainer = AppRedError,
)

@Composable
fun HcmursAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Mặc định là false để ưu tiên màu bạn định nghĩa
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}