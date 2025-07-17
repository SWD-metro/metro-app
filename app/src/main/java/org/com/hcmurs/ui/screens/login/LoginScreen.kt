package org.com.hcmurs.ui.screens.login

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.ui.draw.blur
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures // Import this
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange // Import this
import androidx.compose.ui.input.pointer.pointerInput // Import this
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import org.com.hcmurs.R
import org.com.hcmurs.common.enum.LoadStatus
import org.com.hcmurs.common.enum.SplashScreenState

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()

    val context = LocalContext.current

    var splashScreenState by remember { mutableStateOf(SplashScreenState.Splash) }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("LoginFlow", "Google sign-in activity result: ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("LoginFlow", "Google sign-in completed successfully, processing result")
            viewModel.handleGoogleSignInResult(result.data)
        } else {
            Log.w(
                "LoginFlow",
                "Google sign-in canceled or failed with resultCode: ${result.resultCode}"
            )
            viewModel.updateLoginError("Sign-in was canceled or failed")
        }
    }

    // Navigate when authenticated
    LaunchedEffect(isAuthenticated) {
        Log.d("LoginFlow", "Authentication state changed: $isAuthenticated")
        if (isAuthenticated) {
            Log.d("LoginFlow", "User authenticated, navigating to home screen")
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    val status = when {
        isLoading -> LoadStatus.Loading()
        errorMessage != null -> LoadStatus.Error(errorMessage!!)
        isAuthenticated -> LoadStatus.Success()
        else -> LoadStatus.Init()
    }

    LoginScreenContent(
        navController = navController,
        status = status,
        splashScreenState = splashScreenState,
        onGoogleLoginClick = {
            val signInIntent = viewModel.signInWithGoogle()
            googleSignInLauncher.launch(signInIntent)
        },
        onFacebookLoginClick = {
        },
        onSwipeUpToStart = {
            splashScreenState = SplashScreenState.ReadyToLogin
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LoginScreenContent(
    navController: NavController,
    status: LoadStatus,
    splashScreenState: SplashScreenState,
    onGoogleLoginClick: () -> Unit,
    onFacebookLoginClick: () -> Unit,
    onSwipeUpToStart: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Background Image - luôn hiển thị
        AsyncImage(
            model = R.drawable.login_banner,
            contentDescription = "Background",
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 5.dp),
            contentScale = ContentScale.Crop
        )

        // Overlay cho hiệu ứng làm mờ nếu cần (tùy chọn)
        if (splashScreenState == SplashScreenState.Splash) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)) // Overlay làm mờ ảnh
            )
        }


        AnimatedVisibility(
            visible = splashScreenState == SplashScreenState.Splash,
            enter = fadeIn(animationSpec = tween(durationMillis = 1000, delayMillis = 500)),
            exit = fadeOut(animationSpec = tween(durationMillis = 500))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.BottomCenter)
            ) {
                // ✅ THAY ĐỔI: Thêm Logo vào giữa màn hình
                Image(
                    painter = painterResource(id = R.drawable.hurc_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(150.dp)
                )

                // Phần "Vuốt lên để bắt đầu"
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 64.dp)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragEnd = { onSwipeUpToStart() },
                                onDrag = { change: PointerInputChange, dragAmount: androidx.compose.ui.geometry.Offset ->
                                    change.consume()
                                }
                            )
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arow_up),
                        contentDescription = "Swipe up indicator",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Vuốt lên để bắt đầu",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }


        // White surface for login form - Animated based on splashScreenState
        AnimatedVisibility(
            visible = splashScreenState == SplashScreenState.ReadyToLogin,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 700)
            ) + fadeIn(animationSpec = tween(durationMillis = 700, delayMillis = 100)),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 500)
            ) + fadeOut(animationSpec = tween(durationMillis = 500))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .align(Alignment.BottomCenter)
            ) {
                Surface(
                    shape = RoundedCornerShape(
                        topStart = 32.dp,
                        topEnd = 32.dp,
                        bottomStart = 32.dp,
                        bottomEnd = 32.dp
                    ),
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Welcome to metro",
                                style = MaterialTheme.typography.headlineMedium,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.width(48.dp))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Ho Chi Minh Urban Railway System",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        when (status) {
                            is LoadStatus.Loading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        CircularProgressIndicator(
                                            color = MaterialTheme.colorScheme.primary,
                                            strokeWidth = 3.dp
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "Authenticating...",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.DarkGray
                                        )
                                    }
                                }
                            }

                            else -> {
                                LoginButton(
                                    onClick = onGoogleLoginClick,
                                    text = "Continue with Google",
                                    logoRes = R.drawable.google,
                                    backgroundColor = Color.White,
                                    contentColor = Color.Black
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                LoginButton(
                                    onClick = onFacebookLoginClick,
                                    text = "Continue with Facebook",
                                    logoRes = R.drawable.fb,
                                    backgroundColor = Color(0xFF1877F2),
                                    contentColor = Color.White
                                )

                                // Show error message if there's an error
                                if (status is LoadStatus.Error) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = status.description,
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoginButton(
    onClick: () -> Unit,
    text: String,
    logoRes: Int,
    backgroundColor: Color,
    contentColor: Color
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(8.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = logoRes),
                contentDescription = "$text logo",
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                color = contentColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenSplashPreview() {
    LoginScreenContent(
        navController = rememberNavController(),
        status = LoadStatus.Init(),
        splashScreenState = SplashScreenState.Splash,
        onGoogleLoginClick = {},
        onFacebookLoginClick = {},
        onSwipeUpToStart = {}
    )
}

@Preview(showBackground = true)
@Composable
fun LoginScreenReadyToLoginPreview() {
    LoginScreenContent(
        navController = rememberNavController(),
        status = LoadStatus.Init(),
        splashScreenState = SplashScreenState.ReadyToLogin,
        onGoogleLoginClick = {},
        onFacebookLoginClick = {},
        onSwipeUpToStart = {}
    )
}

// Các Preview khác giữ nguyên
@Preview(showBackground = true)
@Composable
fun LoginScreenInitPreview() {
    LoginScreenContent(
        navController = rememberNavController(),
        status = LoadStatus.Init(),
        splashScreenState = SplashScreenState.ReadyToLogin, // Để preview phần login
        onGoogleLoginClick = {},
        onFacebookLoginClick = {},
        onSwipeUpToStart = {}
    )
}

@Preview(showBackground = true)
@Composable
fun LoginScreenLoadingPreview() {
    LoginScreenContent(
        navController = rememberNavController(),
        status = LoadStatus.Loading(),
        splashScreenState = SplashScreenState.ReadyToLogin,
        onGoogleLoginClick = {},
        onFacebookLoginClick = {},
        onSwipeUpToStart = {}
    )
}

@Preview(showBackground = true)
@Composable
fun LoginScreenSuccessPreview() {
    LoginScreenContent(
        navController = rememberNavController(),
        status = LoadStatus.Success(),
        splashScreenState = SplashScreenState.ReadyToLogin,
        onGoogleLoginClick = {},
        onFacebookLoginClick = {},
        onSwipeUpToStart = {}
    )
}

@Preview(showBackground = true)
@Composable
fun LoginScreenErrorPreview() {
    LoginScreenContent(
        navController = rememberNavController(),
        status = LoadStatus.Error("An error occurred"),
        splashScreenState = SplashScreenState.ReadyToLogin,
        onGoogleLoginClick = {},
        onFacebookLoginClick = {},
        onSwipeUpToStart = {}
    )
}