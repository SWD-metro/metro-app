package org.com.hcmurs.ui.screens.stationselection

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.com.hcmurs.Screen
import org.com.hcmurs.Station
import org.com.hcmurs.ui.screens.metro.buyticket.FareMatrixViewModel
import org.com.hcmurs.ui.theme.BluePrimary

// Define colors consistently
private val AppWhite = Color(0xFFFFFFFF)
private val AppLightGray = Color(0xFFF5F5F5)
private val AppMediumGray = Color(0xFFB0B0B0)
private val AppDarkGray = Color(0xFF424242)
private const val TAG = "StationSelectionScreen"

// Enum để theo dõi ô nhập liệu nào đang được active
private enum class ActiveInput { FROM, TO }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationSelectionScreen(
    navController: NavController,
    stationViewModel: StationSelectionViewModel,
    fareMatrixViewModel: FareMatrixViewModel
) {
    val uiState by stationViewModel.uiState.collectAsState()
    val fareMatrixUiState by fareMatrixViewModel.uiState.collectAsState()

    // State cho các đối tượng Station đã chọn
    var selectedEntryStation by remember { mutableStateOf<Station?>(null) }
    var selectedExitStation by remember { mutableStateOf<Station?>(null) }

    // State cho ô nhập liệu nào đang active
    var activeInput by remember { mutableStateOf<ActiveInput?>(ActiveInput.FROM) }

    var isNavigationTriggered by remember { mutableStateOf(false) }

    // Logic xử lý điều hướng sau khi tính giá vé (giữ nguyên)
    LaunchedEffect(fareMatrixUiState) {
        if (!isNavigationTriggered) return@LaunchedEffect
        if (fareMatrixUiState.isLoading) return@LaunchedEffect

        if (fareMatrixUiState.calculatedFare != null) {
            val entryStation = selectedEntryStation
            val exitStation = selectedExitStation
            if (entryStation != null && exitStation != null) {
                Log.d(TAG, "Navigating to CalculatedFareScreen...")
                navController.navigate(
                    Screen.CalculatedFare.createRoute(
                        entryStationId = entryStation.stationId,
                        exitStationId = exitStation.stationId
                    )
                )
            }
        } else if (fareMatrixUiState.errorMessage != null) {
            Log.e(TAG, "Fare calculation failed: ${fareMatrixUiState.errorMessage}")
        }
        isNavigationTriggered = false
    }

    Scaffold(
        containerColor = AppLightGray,
        bottomBar = {
            BottomAppBar(
                containerColor = AppWhite,
                tonalElevation = 8.dp
            ) {
                Button(
                    onClick = {
                        if (selectedEntryStation != null && selectedExitStation != null) {
                            isNavigationTriggered = true
                            fareMatrixViewModel.getFareForStations(
                                selectedEntryStation!!.stationId,
                                selectedExitStation!!.stationId
                            )
                        }
                    },
                    enabled = selectedEntryStation != null && selectedExitStation != null && !fareMatrixUiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)

                ) {
                    if (fareMatrixUiState.isLoading && isNavigationTriggered) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("Tiếp tục", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Tiếp tục")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            StationInputsCard(
                routeName = uiState.selectedRoute?.routeName,
                fromStationName = selectedEntryStation?.name ?: "",
                toStationName = selectedExitStation?.name ?: "",
                activeInput = activeInput,
                onInputFocus = { activeInput = it },
                onSwap = {
                    val tempStation = selectedEntryStation
                    selectedEntryStation = selectedExitStation
                    selectedExitStation = tempStation
                }
            )

            when {
                uiState.isLoadingStations -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BluePrimary)
                    }
                }
                uiState.errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text("Lỗi: ${uiState.errorMessage}", color = Color.Red, textAlign = TextAlign.Center)
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = paddingValues.calculateBottomPadding() + 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.stations, key = { it.stationId }) { station ->
                            StationListItem(
                                station = station,
                                isSelected = station == selectedEntryStation || station == selectedExitStation,
                                onClick = {
                                    when (activeInput) {
                                        ActiveInput.FROM -> {
                                            if (station != selectedExitStation) {
                                                selectedEntryStation = station
                                                activeInput = ActiveInput.TO
                                            }
                                        }
                                        ActiveInput.TO -> {
                                            if (station != selectedEntryStation) {
                                                selectedExitStation = station
                                                activeInput = null
                                            }
                                        }
                                        null -> {
                                            if (station != selectedExitStation) {
                                                selectedEntryStation = station
                                                activeInput = ActiveInput.TO
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StationInputsCard(
    routeName: String?,
    fromStationName: String,
    toStationName: String,
    activeInput: ActiveInput?,
    onInputFocus: (ActiveInput) -> Unit,
    onSwap: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppWhite),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            if (routeName != null) {
                Text(
                    text = "Tuyến: $routeName",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BluePrimary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    ReadOnlyTextField(
                        value = fromStationName,
                        placeholder = "Chọn ga đi",
                        isActive = activeInput == ActiveInput.FROM,
                        onClick = { onInputFocus(ActiveInput.FROM) }
                    )
                    Spacer(Modifier.height(16.dp))
                    ReadOnlyTextField(
                        value = toStationName,
                        placeholder = "Chọn ga đến",
                        isActive = activeInput == ActiveInput.TO,
                        onClick = { onInputFocus(ActiveInput.TO) }
                    )
                }
                // Nút swap
                IconButton(onClick = onSwap, modifier = Modifier.padding(start = 8.dp)) {
                    Icon(Icons.Default.SwapVert, contentDescription = "Đảo chiều", tint = AppDarkGray)
                }
            }
        }
    }
}

@Composable
private fun ReadOnlyTextField(
    value: String,
    placeholder: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isActive) BluePrimary else AppMediumGray.copy(alpha = 0.4f)
    val borderWidth = if (isActive) 2.dp else 1.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.LocationOn,
            contentDescription = null,
            tint = if (isActive) BluePrimary else AppMediumGray
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = value.ifEmpty { placeholder },
            color = if (value.isEmpty()) AppMediumGray else AppDarkGray,
            fontSize = 16.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun StationListItem(
    station: Station,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) BluePrimary.copy(alpha = 0.1f) else AppWhite
    val textColor = if (isSelected) BluePrimary else AppDarkGray

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = station.name,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )
    }
}
