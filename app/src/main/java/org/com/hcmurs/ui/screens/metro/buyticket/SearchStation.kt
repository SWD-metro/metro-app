package org.com.hcmurs.ui.screens.searchstation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.com.hcmurs.ui.components.AppBottomNavigationBar
import org.com.hcmurs.ui.theme.AppDarkGray
import org.com.hcmurs.ui.theme.AppLightGray
import org.com.hcmurs.ui.theme.AppMediumGray
import org.com.hcmurs.ui.theme.AppWhite
import org.com.hcmurs.ui.theme.BlueDark
import org.com.hcmurs.Screen

// Model đại diện cho một ga
data class Station(
    val id: String,
    val name: String,
    val address: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchStationScreen(
    navController: NavHostController,
    focusField: String? = null // Tham số mới để xác định trường cần focus
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var fromStationText by remember { mutableStateOf("") }
    var toStationText by remember { mutableStateOf("") }

    val fromFocusRequester = remember { FocusRequester() }
    val toFocusRequester = remember { FocusRequester() }

    // Thực hiện focus khi Composable được hiển thị và focusField được truyền vào
    LaunchedEffect(focusField) {
        when (focusField) {
            "from" -> fromFocusRequester.requestFocus()
            "to" -> toFocusRequester.requestFocus()
        }
    }

    val allStations = remember {
        listOf(
            Station("BT", "Ga Bến Thành", "Quận 1, TP.HCM"),
            Station("NHTT", "Ga Nhà Hát Thành Phố", "Quận 1, TP.HCM"),
            Station("BS", "Ga Ba Son", "Quận 1, TP.HCM"),
            Station("VT", "Ga Văn Thánh", "Quận Bình Thạnh, TP.HCM"),
            Station("TSN", "Ga Tân Sơn Nhất", "Quận Tân Bình, TP.HCM"),
            Station("LX", "Ga Lăng Cha Cả", "Quận Tân Bình, TP.HCM"),
            Station("ND", "Ga Nguyễn Du", "Quận 1, TP.HCM"),
            Station("CH", "Ga Chợ Lớn", "Quận 5, TP.HCM")
        )
    }

    val searchHistory = remember { mutableStateListOf(
        "Ga Bến Thành",
        "Ga Nhà Hát Thành Phố",
        "Ga Văn Thánh"
    )}

    var showFromSuggestions by remember { mutableStateOf(false) }
    var showToSuggestions by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(
                navController = navController,
                currentRoute = currentRoute
            )
        },
        containerColor = AppLightGray
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Phần tiêu đề tùy chỉnh (thay thế cho topbar)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp) // Thêm padding dưới nếu cần
            ) {
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AppWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Điểm đi:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppDarkGray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = fromStationText,
                        onValueChange = {
                            fromStationText = it
                            showFromSuggestions = true
                            showToSuggestions = false
                        },
                        label = { Text("Ga khởi hành...") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "From", tint = BlueDark) },
                        trailingIcon = {
                            if (fromStationText.isNotEmpty()) {
                                IconButton(onClick = { fromStationText = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = AppMediumGray)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(fromFocusRequester), // Gắn FocusRequester
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BlueDark,
                            unfocusedBorderColor = AppMediumGray.copy(alpha = 0.5f),
                            focusedLabelColor = BlueDark,
                            unfocusedLabelColor = AppMediumGray,
                            cursorColor = BlueDark
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    if (showFromSuggestions) {
                        val filteredFromSuggestions = allStations.filter {
                            it.name.contains(fromStationText, ignoreCase = true) ||
                                    it.address.contains(fromStationText, ignoreCase = true)
                        }
                        if (filteredFromSuggestions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            filteredFromSuggestions.forEach { station ->
                                StationSuggestionItem(station) {
                                    fromStationText = station.name
                                    showFromSuggestions = false
                                }
                            }
                        } else if (fromStationText.isNotEmpty()) {
                            Text("Không tìm thấy ga nào.", color = AppMediumGray, modifier = Modifier.padding(top = 8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Điểm đến:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppDarkGray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = toStationText,
                        onValueChange = {
                            toStationText = it
                            showToSuggestions = true
                            showFromSuggestions = false
                        },
                        label = { Text("Ga đến...") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "To", tint = BlueDark) },
                        trailingIcon = {
                            if (toStationText.isNotEmpty()) {
                                IconButton(onClick = { toStationText = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = AppMediumGray)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(toFocusRequester), // Gắn FocusRequester
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BlueDark,
                            unfocusedBorderColor = AppMediumGray.copy(alpha = 0.5f),
                            focusedLabelColor = BlueDark,
                            unfocusedLabelColor = AppMediumGray,
                            cursorColor = BlueDark
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    if (showToSuggestions) {
                        val filteredToSuggestions = allStations.filter {
                            it.name.contains(toStationText, ignoreCase = true) ||
                                    it.address.contains(toStationText, ignoreCase = true)
                        }
                        if (filteredToSuggestions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            filteredToSuggestions.forEach { station ->
                                StationSuggestionItem(station) {
                                    toStationText = station.name
                                    showToSuggestions = false
                                }
                            }
                        } else if (toStationText.isNotEmpty()) {
                            Text("Không tìm thấy ga nào.", color = AppMediumGray, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AppWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Lịch sử tìm kiếm / Gợi ý:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppDarkGray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    searchHistory.forEach { historyItem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // Khi chọn từ lịch sử, đặt vào ô đi
                                    fromStationText = historyItem
                                    showFromSuggestions = false
                                    showToSuggestions = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "History",
                                tint = AppMediumGray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = historyItem, fontSize = 15.sp, color = AppDarkGray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Hàng chứa hai button "Quay lại" và "Hoàn tất"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { navController.popBackStack() }, // Quay lại trang trước
                    modifier = Modifier
                        .weight(1f) // Chiếm phần còn lại của không gian
                        .height(56.dp)
                        .padding(end = 8.dp), // Khoảng cách với button bên phải
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppMediumGray.copy(alpha = 0.8f), // Màu xám cho button "Quay lại"
                        contentColor = AppWhite
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = AppWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Quay lại",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        if (fromStationText.isNotEmpty() && toStationText.isNotEmpty()) {
                            // Lưu các ga đã chọn vào SavedStateHandle của backstack entry trước đó (BuyTicketScreen)
                            navController.previousBackStackEntry?.savedStateHandle?.set("selectedFromStation", fromStationText)
                            navController.previousBackStackEntry?.savedStateHandle?.set("selectedToStation", toStationText)

                            // Thêm vào history để duy trì thứ tự nếu muốn
                            if (!searchHistory.contains(fromStationText)) searchHistory.add(0, fromStationText)
                            if (!searchHistory.contains(toStationText)) searchHistory.add(0, toStationText)
                            while (searchHistory.size > 5) searchHistory.removeLast() // Giới hạn lịch sử

                            navController.popBackStack() // Quay lại màn hình BuyTicketScreen
                        } else {
                            // Hiển thị thông báo yêu cầu nhập đủ điểm đi/đến
                            // Có thể dùng Snackbar hoặc Toast
                        }
                    },
                    modifier = Modifier
                        .weight(1f) // Chiếm phần còn lại của không gian
                        .height(56.dp)
                        .padding(start = 8.dp), // Khoảng cách với button bên trái
                    enabled = fromStationText.isNotEmpty() && toStationText.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BlueDark,
                        disabledContainerColor = BlueDark.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Hoàn tất",
                        color = AppWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Hoàn tất", tint = AppWhite)
                }
            }
        }
    }
}

@Composable
fun StationSuggestionItem(station: Station, onClick: (Station) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(station) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = AppMediumGray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = station.name, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = AppDarkGray)
            Text(text = station.address, fontSize = 12.sp, color = AppMediumGray)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun PreviewSearchStationScreen() {
    val navController = rememberNavController()
    SearchStationScreen(navController = navController)
}