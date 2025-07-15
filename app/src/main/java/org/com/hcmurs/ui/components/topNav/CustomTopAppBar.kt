package org.com.hcmurs.ui.components.topNav

import androidx.compose.foundation.Image // Import Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box // Import Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize // Import fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment // Import Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale // Import ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    title: String,
    navigationIcon: @Composable () -> Unit,
    titleColor: Color = Color.White,
    iconColor: Color = Color.White,
    height: Dp = 75.dp,
    backgroundImage: Painter? = null,
    backgroundBrush: Brush? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .shadow(elevation = 4.dp)
    ) {
        // Ảnh nền
        if (backgroundImage != null) {
            Image(
                painter = backgroundImage,
                contentDescription = null, // ContentDescription có thể là null cho background image
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop // Cắt ảnh để lấp đầy không gian
            )
        } else if (backgroundBrush != null) {
            Spacer(modifier = Modifier.fillMaxSize().background(backgroundBrush))
        } else {
            Spacer(modifier = Modifier.fillMaxSize().background(Color.Transparent))
        }


        // Đặt TopAppBar lên trên ảnh/gradient
        CenterAlignedTopAppBar(
            modifier = Modifier.fillMaxSize(), // Đảm bảo TopAppBar lấp đầy Box
            title = {
                Text(
                    text = title,
                    color = titleColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            navigationIcon = navigationIcon,
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent, // RẤT QUAN TRỌNG: Để nền trong suốt
                titleContentColor = titleColor,
                navigationIconContentColor = iconColor
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCustomTopAppBar() {
    // Ví dụ về Painter cho Preview
    val placeholderImage: Painter = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_gallery)

    // Ví dụ về Brush cho Preview
    val previewGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF2196F3), Color(0xFF1976D2))
    )

}