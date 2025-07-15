package org.com.hcmurs.ui.screens.metro.home

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
import org.com.hcmurs.ui.screens.guide.GuideSection
import org.com.hcmurs.ui.screens.metro.myticket.YourTicket
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.com.hcmurs.ui.screens.login.LoginViewModel
import org.com.hcmurs.ui.screens.news.BlogSection

@Composable
fun HomeScreen(navController: NavHostController,
               loginViewModel: LoginViewModel = hiltViewModel()
) {
    val userProfile by loginViewModel.userProfile.collectAsState()
    val userName = userProfile?.name ?: "Guest"
    AppHomeScreen(
        navController = navController,
        showFloatingButton = true,
        role = UserRole.USER,
        userName = userName
        ) {

        item {
            Spacer(modifier = Modifier.height(20.dp))
            YourTicket(navController)
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
            Box(modifier = Modifier.padding(start = 16.dp)) {
                GuideSection()
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

@Preview(showBackground = true)
@Composable
fun HomeMetroScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController = navController)
}