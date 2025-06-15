package org.com.hcmurs.ui.screens.searchstation

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.com.hcmurs.repositories.station.Station
import org.com.hcmurs.ui.components.AppBottomNavigationBar
import org.com.hcmurs.ui.components.topNav.CustomTopAppBar
import org.com.hcmurs.ui.theme.AppDarkGray
import org.com.hcmurs.ui.theme.AppLightGray
import org.com.hcmurs.ui.theme.AppMediumGray
import org.com.hcmurs.ui.theme.AppWhite
import org.com.hcmurs.ui.theme.BlueDark

enum class ActiveInput {
    FROM, TO
}

@Composable
fun SearchStationScreen(
    navController: NavHostController,
    viewModel: SearchStationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var fromStationText by remember { mutableStateOf("") }
    var toStationText by remember { mutableStateOf("") }
    var activeInput by remember { mutableStateOf<ActiveInput?>(null) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Chọn ga",
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color(0xFF1565C0)
                        )
                    }
                }
            )
        },
        bottomBar = {
            AppBottomNavigationBar(
                navController = navController,
                currentRoute = currentRoute
            )
        },
        containerColor = AppLightGray) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StationSelectionInputs(
                fromStationText = fromStationText,
                toStationText = toStationText,
                activeInput = activeInput,
                onInputFocus = { focusedInput -> activeInput = focusedInput }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.weight(1f)) {
                StationList(
                    uiState = uiState,
                    onStationSelected = { selectedStation ->
                        when (activeInput) {
                            ActiveInput.FROM -> fromStationText = selectedStation.name
                            ActiveInput.TO -> toStationText = selectedStation.name
                            null -> {  }
                        }
                        activeInput = null
                    }
                )
            }


            ActionButtons(
                navController = navController,
                isCompleteEnabled = fromStationText.isNotEmpty() && toStationText.isNotEmpty()
            ) {
                navController.previousBackStackEntry?.savedStateHandle?.set("selectedFromStation", fromStationText)
                navController.previousBackStackEntry?.savedStateHandle?.set("selectedToStation", toStationText)
                navController.popBackStack()
            }
        }
    }
}

@Composable
private fun StationSelectionInputs(
    fromStationText: String,
    toStationText: String,
    activeInput: ActiveInput?,
    onInputFocus: (ActiveInput) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ReadOnlyTextField(
                label = "Điểm đi",
                value = fromStationText,
                placeholder = "Chọn ga khởi hành...",
                isActive = activeInput == ActiveInput.FROM,
                onClick = { onInputFocus(ActiveInput.FROM) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            ReadOnlyTextField(
                label = "Điểm đến",
                value = toStationText,
                placeholder = "Chọn ga đến...",
                isActive = activeInput == ActiveInput.TO,
                onClick = { onInputFocus(ActiveInput.TO) }
            )
        }
    }
}

@Composable
private fun ReadOnlyTextField(
    label: String,
    value: String,
    placeholder: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column {
        Text(text = label, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = AppDarkGray)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(
                    width = if (isActive) 2.dp else 1.dp,
                    color = if (isActive) BlueDark else AppMediumGray.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = BlueDark)
                Spacer(modifier = Modifier.width(8.dp))
                val textToShow = value.ifEmpty { placeholder }
                val color = if (value.isEmpty()) AppMediumGray else AppDarkGray
                Text(text = textToShow, color = color, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun BoxScope.StationList(
    uiState: StationUiState,
    onStationSelected: (Station) -> Unit
) {
    when (uiState) {
        is StationUiState.Loading -> {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        is StationUiState.Error -> {
            Text(
                text = uiState.message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        is StationUiState.Success -> {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                item {
                    Text("Vui lòng chọn một nhà ga", fontWeight = FontWeight.SemiBold, color = AppDarkGray)
                }
                items(uiState.stations, key = { it.stationId }) { station ->
                    StationListItem(station = station, onClick = { onStationSelected(station) })
                }
            }
        }
        else -> {}
    }
}

@Composable
private fun StationListItem(station: Station, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = AppMediumGray)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(station.name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(station.address, fontSize = 12.sp, color = AppMediumGray)
            }
        }
    }
}

@Composable
private fun ActionButtons(
    navController: NavHostController,
    isCompleteEnabled: Boolean,
    onComplete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.weight(1f).height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppMediumGray.copy(alpha = 0.8f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Quay lại", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = onComplete,
            modifier = Modifier.weight(1f).height(56.dp),
            enabled = isCompleteEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = BlueDark,
                disabledContainerColor = BlueDark.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Hoàn tất", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Hoàn tất")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchStationScreen() {
    val navController = rememberNavController()
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ReadOnlyTextField(
            label = "Điểm đi",
            value = "",
            placeholder = "Chọn ga khởi hành...",
            isActive = true,
            onClick = {}
        )
        val sampleStation = Station(1, "BT", "Ga Bến Thành", "Quận 1, TP.HCM", 0.0, 0.0, 1, "ACTIVE", "", "", 1)
        StationListItem(station = sampleStation, onClick = {})
    }
}