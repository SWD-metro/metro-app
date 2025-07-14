package org.com.hcmurs

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.com.hcmurs.ui.screens.addoredit.AddOrEditScreen
import org.com.hcmurs.ui.screens.detail.DetailScreen
import org.com.hcmurs.ui.screens.home.HomeMetro
import org.com.hcmurs.ui.screens.home.HomeViewModel
import org.com.hcmurs.ui.screens.login.LoginScreen
import org.com.hcmurs.ui.screens.register.RegisterScreen
import org.com.hcmurs.ui.screens.SplashScreen
import org.com.hcmurs.ui.screens.metro.account.CCCDScreen
import org.com.hcmurs.ui.screens.metro.account.LinkCCCDScreen
import org.com.hcmurs.ui.screens.metro.account.RegisterFormScreen
import org.com.hcmurs.ui.screens.metro.buyticket.BuyTicketScreen
// THÊM IMPORT CHO MÀN HÌNH MỚI
import org.com.hcmurs.ui.screens.metro.buyticket.OrderInfoScreen
import org.com.hcmurs.ui.screens.searchstation.SearchStationScreen


sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Register: Screen("register")
    object Login : Screen("login")
    object Home : Screen("home")
    object Detail : Screen("detail")
    object AddOrEdit : Screen("addOrEdit")
    object HomeMetro : Screen("homeMetro")
    object Feedback : Screen("feedback")
    object RedeemCodeForTicket : Screen("redeemCodeForTicket")
    object MyTicket : Screen("myTicket")
    object CCCD : Screen("cccd")
    object RegisterCCCD : Screen("registerCCCD")
    object LinkCCCD : Screen("linkCCCD")

        // Add new screen routes for the grid items
    object SearchStation : Screen("searchStation")
    object BuyTicket : Screen("buyTicket")
    object BuyTicketDetail : Screen("buyTicketDetail")
    object Route : Screen("route")
    object Maps : Screen("maps")
    object VirtualTour : Screen("virtualTour")
    object TicketInformation : Screen("ticketInformation")
    object Account : Screen("account")
    object Event : Screen("event")
    object ConstructionImage : Screen("constructionImage")
    object Setting : Screen("setting")
    object CooperationLink : Screen("cooperationLink")
    object Introduction : Screen("introduction")

    // Test
    object UserProfile : Screen("userProfile")
    object OsmdroidMap : Screen("osmdroidMap")

    // THÊM VÀO: Route cho màn hình chi tiết đơn hàng
    object OrderInfo : Screen("order_info_screen")
}

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = hiltViewModel()
    val mainState = mainViewModel.uiState.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(mainState.value.error) {
        if (mainState.value.error != null && mainState.value.error.isNotEmpty()) {
            Toast.makeText(context, mainState.value.error, Toast.LENGTH_LONG).show()
            mainViewModel.setError("")
        }
    }

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController, viewModel = hiltViewModel())
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController, viewModel = hiltViewModel(), mainViewModel)
        }
        composable(Screen.Home.route) {
            HomeMetro(navController, viewModel = hiltViewModel<HomeViewModel>(), mainViewModel)
        }
        composable(Screen.SearchStation.route){
            SearchStationScreen(navController = navController)
        }
        composable(Screen.BuyTicket.route){
            BuyTicketScreen(navController = navController)
        }
        composable(Screen.CCCD.route) {
            CCCDScreen(navController)
        }
        composable(Screen.RegisterCCCD.route) {
            RegisterFormScreen(navController)
        }

        composable(Screen.LinkCCCD.route) {
            LinkCCCDScreen(navController)
        }
        composable(
            route = "${Screen.OrderInfo.route}/{ticketType}",
            arguments = listOf(navArgument("ticketType") { type = NavType.StringType })
        ) {
            OrderInfoScreen(navController = navController)
        }

        composable(Screen.Detail.route + "?noteIndex={noteIndex}",
            arguments = listOf(
                navArgument("noteIndex") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) {
            it.arguments?.getInt("noteIndex").let { index ->
                var parentEntry = remember(it) {
                    navController.getBackStackEntry(Screen.Home.route)
                }
                val homeViewModel: HomeViewModel = hiltViewModel(parentEntry)
                if (index != null) {
                    DetailScreen(navController, homeViewModel, mainViewModel, index)
                }
            }
        }

        composable(Screen.AddOrEdit.route + "?noteIndex={noteIndex}",
            arguments = listOf(
                navArgument("noteIndex") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) {
            it.arguments?.getInt("noteIndex").let { index ->
                var parentEntry = remember(it) {
                    navController.getBackStackEntry(Screen.Home.route)
                }
                val homeViewModel: HomeViewModel = hiltViewModel(parentEntry)
                AddOrEditScreen(
                    navController,
                    homeViewModel,
                    hiltViewModel(),
                    mainViewModel,
                    index!!
                )
            }
        }
    }
}