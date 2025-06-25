//package org.com.hcmurs.ui.screens.metro.qrcode
//
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.setValue
//import org.com.hcmurs.ui.screens.metro.qrcode.TicketPurchaseScreen
//
//enum class MetroScreen {
//    Purchase,
//    Scanner
//}
//
//@Composable
//fun MetroTicketApp() {
//    var currentScreen by remember { mutableStateOf(MetroScreen.Purchase) }
//
//    when (currentScreen) {
//        MetroScreen.Purchase -> TicketPurchaseScreen(
//            onNavigateToScanner = {
//                currentScreen = MetroScreen.Scanner
//            }
//        )
//        MetroScreen.Scanner -> QRScannerScreen(
//            onBackPressed = {
//                currentScreen = MetroScreen.Purchase
//            }
//        )
//    }
//}