package org.com.hcmurs.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.com.hcmurs.MainViewModel
import org.com.hcmurs.R
import androidx.navigation.compose.currentBackStackEntryAsState
import org.com.hcmurs.ui.components.AppBottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeMetro(
    navController: NavHostController,
    viewModel: HomeViewModel,
    mainViewModel: MainViewModel
) {
    val state by viewModel.uiState.collectAsState()

    val primaryBlue = Color(0xFF4A6FA5)
    val backgroundColor = Color(0xFFFFFFFF)
    val cardBackground = Color.White
    val orange = Color(0xFFFF6B35)
    val green = Color(0xFF4CAF50)package org.com.hcmurs.ui.screens.metro.home

    import android.os.Build
            import androidx.annotation.RequiresApi
            import androidx.compose.foundation.layout.Box
            import androidx.compose.foundation.layout.Spacer
            import androidx.compose.foundation.layout.height
            import androidx.compose.foundation.layout.padding
            import androidx.compose.runtime.Composable
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.tooling.preview.Preview
            import androidx.compose.ui.unit.dp
            import androidx.navigation.NavHostController
            import androidx.navigation.compose.rememberNavController
            import org.com.hcmurs.constant.UserRole
            import org.com.hcmurs.ui.components.common.AppHomeScreen
            import org.com.hcmurs.ui.components.featured.FeaturedBlogsSection
            import org.com.hcmurs.ui.screens.news.BlogSection

            @RequiresApi(Build.VERSION_CODES.O)
            @Composable
            fun HomeScreen(navController: NavHostController) {
                AppHomeScreen(
                    navController = navController,
                    showFloatingButton = true,
                    role = UserRole.USER
                ) {
                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                        Box(modifier = Modifier.padding(start = 16.dp)) {
                            FeaturedBlogsSection(navController)
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(modifier = Modifier.padding(start = 16.dp)) {
                            BlogSection(navController)
                        }
                    }
                }
            }

    @RequiresApi(Build.VERSION_CODES.O)
    @Preview(showBackground = true)
    @Composable
    fun HomeMetroScreenPreview() {
        val navController = rememberNavController()
        HomeScreen(navController = navController)
    }

    var searchInput by remember { mutableStateOf("") }
    val userName by remember { mutableStateOf("Passenger") }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(Unit) {
        viewModel.loadNotes()
    }

    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(
                navController = navController,
                currentRoute = currentRoute
            )
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Box( // Box lớn chứa cả ảnh nền và nội dung phía trên
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp) // Giữ chiều cao tương đối để ảnh hiển thị đủ
                ) {
                    // Background Image
                    Image(
                        painter = painterResource(id = R.drawable.home), // Đảm bảo R.drawable.home là ảnh của bạn
                        contentDescription = null, // Decorative image
                        contentScale = ContentScale.Crop, // Crop to fill the bounds
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlay content (Good Morning, Search Bar, Notification, and Quick Action Cards)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent)
                                )
                            )
                            .padding(horizontal = 20.dp) // Padding ngang cho toàn bộ nội dung trong Column
                            .padding(top = 40.dp) // Padding trên cùng
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Good Morning,",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = userName,
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(Color.White, CircleShape)
                                    .clickable { /* TODO: Open QR Code */ },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = searchInput,
                            onValueChange = { searchInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    "Which station are you going to?",
                                    color = Color.Gray.copy(alpha = 0.7f)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color.Gray
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(20.dp)) // Khoảng cách giữa search bar và Quick Action Cards

                        // Quick Action Cards now inside the same Column
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            QuickActionCard(
                                modifier = Modifier.weight(1f),
                                title = "Book ticket to",
                                subtitle = "Ben Thanh Station",
                                backgroundColor = cardBackground,
                                onClick = { /* TODO: Navigate to ticket booking for Ben Thanh */ }
                            )
                            QuickActionCard(
                                modifier = Modifier.weight(1f),
                                title = "Book ticket to",
                                subtitle = "Suoi Tien Station",
                                backgroundColor = cardBackground,
                                onClick = { /* TODO: Navigate to ticket booking for Suoi Tien */ }
                            )
                            QuickActionCard(
                                modifier = Modifier.weight(1f),
                                title = "View Map",
                                subtitle = "Metro Lines",
                                backgroundColor = cardBackground,
                                onClick = { /* TODO: Navigate to metro map */ }
                            )
                        }

                        // Spacer này sẽ đẩy nội dung phía dưới của Column lên trên
                        Spacer(modifier = Modifier.weight(1f)) // Dùng weight để đẩy mọi thứ lên trên và card nằm cuối Column
                    }
                }
            }

            // Các phần nội dung khác vẫn như cũ, nhưng không cần Spacer bù trừ nữa
            item {
                Spacer(modifier = Modifier.height(20.dp)) // Khoảng cách sau phần ảnh nền và card
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = "Your Tickets",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = cardBackground),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "Monday, February 19, 2024",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(orange, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Fastest",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = "Departure: Ben Thanh",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(green, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Departure",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Text(
                                        text = "Ben Thanh",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }

                                Text(
                                    text = "Est. 35 min",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(orange, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Arrival",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Text(
                                        text = "Suoi Tien",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Subway,
                                    contentDescription = "Metro Line",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Line 1 | Train arrives at 15:30 at Ben Thanh Station",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { /* TODO: Show barcode */ },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = primaryBlue
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "View Ticket QR Code",
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "News & Offers",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        TextButton(onClick = { /* TODO: See all news */ }) {
                            Text(
                                text = "See All >",
                                color = primaryBlue,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(3) { index ->
                            NewsCard(
                                imageRes = R.drawable.loginbackground, // TODO: Replace with actual news images (e.g., R.drawable.news_metro_1)
                                modifier = Modifier.width(200.dp)
                            )
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun NewsCard(
    imageRes: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "News",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeMetroPreview() {
    HomeMetro(
        navController = NavHostController(LocalContext.current),
        viewModel = HomeViewModel(null, null),
        mainViewModel = MainViewModel()
    )
}