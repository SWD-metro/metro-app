package org.com.hcmurs.ui.screens.metro.route

import androidx.compose.foundation.lazy.items
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Paint
import android.preference.PreferenceManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import org.com.hcmurs.R
import org.com.hcmurs.model.Station
import org.com.hcmurs.ui.components.common.CommonTopBar
import org.com.hcmurs.utils.initialView
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@Composable
fun RouteScreen(
    navController: NavController,
    metroStationViewModel: MetroStationViewModel = hiltViewModel<MetroStationViewModel>()
) {
    val metroStations by metroStationViewModel.stations.collectAsState()
    val stationPoints by metroStationViewModel.stationGeoPoints.collectAsState()

    val isLoading by metroStationViewModel.isLoading.collectAsState()
    var isCalculatingRoute by remember { mutableStateOf(false) }
    val error by metroStationViewModel.error.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    var hasLocationPermission by remember { mutableStateOf(false) }
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var currentCenter by remember { mutableStateOf<GeoPoint?>(null) }

    var isBottomCardExpanded by remember { mutableStateOf(false) }
    var showStartStationDialog by remember { mutableStateOf(false) }
    var showEndStationDialog by remember { mutableStateOf(false) }
    var selectedStartStation by remember { mutableStateOf<Station?>(null) }
    var selectedEndStation by remember { mutableStateOf<Station?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
    }

    LaunchedEffect(metroStations) {
        if (metroStations.isNotEmpty()) {

            Log.d("RouteScreen", "Metro stations loaded: ${metroStations.size}")

            // Only set values if they haven't been set by the user
            if (selectedStartStation == null) {
                selectedStartStation = metroStations.first()
            }
            if (selectedEndStation == null) {
                selectedEndStation = metroStations.last()
            }
        }
    }

    LaunchedEffect(Unit) {
        hasLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        metroStationViewModel.stations
    }

    Scaffold(
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Show loading indicator if needed
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            // Show error if needed
            error?.let {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: $it")
                }
            }

            // Only recreate overlays when data changes
            val mapUpdateKey by remember(metroStations, selectedStartStation, selectedEndStation) {
                mutableLongStateOf(System.currentTimeMillis())
            }

            AndroidView(
                factory = { context ->
                    // Initialize OSMdroid configuration
                    Configuration.getInstance().load(
                        context,
                        PreferenceManager.getDefaultSharedPreferences(context)
                    )

                    // Create and configure the map view
                    MapView(context).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(14.0)

                        controller.setCenter(initialView)

                        // Add a marker at the center
                        val marker = Marker(this)
                        marker.position = initialView
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        marker.title = "Ho Chi Minh City"
                        overlays.add(marker)

                        // Store reference for FAB
                        mapView = this

                        // Add map event listener to track current center
                        addMapListener(object : MapListener {
                            override fun onScroll(event: ScrollEvent?): Boolean {
                                currentCenter = mapCenter as GeoPoint
                                Log.d(
                                    "RouteScreen",
                                    "Current Center - Lat: ${currentCenter?.latitude}, Lng: ${currentCenter?.longitude}"
                                )
                                return true
                            }

                            override fun onZoom(event: ZoomEvent?): Boolean {
                                currentCenter = mapCenter as GeoPoint
                                Log.d(
                                    "RouteScreen",
                                    "Current Center - Lat: ${currentCenter?.latitude}, Lng: ${currentCenter?.longitude}, Zoom: ${event?.zoomLevel}"
                                )
                                return true
                            }
                        })
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { mapView ->
                    if (mapUpdateKey > 0) {
                        val currentZoom = mapView.zoomLevelDouble
                        mapView.overlays.clear()
                        val polyline = Polyline().apply {
                            stationPoints.forEach { addPoint(it) }
                            outlinePaint.strokeWidth = when {
                                currentZoom < 13 -> 8f
                                currentZoom < 16 -> 12f
                                else -> 16f
                            }
                            outlinePaint.color = "#FF1976D2".toColorInt() // Material Blue
                            outlinePaint.strokeCap = Paint.Cap.ROUND
                            outlinePaint.isAntiAlias = true
                        }
                        mapView.overlays.add(polyline)
                        stationPoints.forEachIndexed { index, stationPoint ->

                            val metroStationAtPoint = metroStations.find { it.location.distanceToAsDouble(stationPoint) < 100 }
                            val isStartOrEndStation = metroStationAtPoint?.isTerminal == true

                            val metroMarker = Marker(mapView).apply {
                                position = stationPoint
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                                title = "Metro Station ${index + 1}"

                                val iconRes = if (isStartOrEndStation) R.drawable.ic_point else R.drawable.ic_point
                                val metroIcon = ContextCompat.getDrawable(context, iconRes)?.mutate()
                                metroIcon?.let { drawable ->
                                    val iconSize = when {
                                        currentZoom < 12 -> 64
                                        currentZoom < 15 -> 96
                                        currentZoom < 18 -> 128
                                        else -> 160
                                    }
                                    val bitmap = android.graphics.Bitmap.createBitmap(
                                        iconSize, iconSize, android.graphics.Bitmap.Config.ARGB_8888
                                    )
                                    val canvas = android.graphics.Canvas(bitmap)
                                    drawable.setBounds(0, 0, iconSize, iconSize)
                                    drawable.draw(canvas)
                                    icon = bitmap.toDrawable(context.resources)
                                }

                                // Metro stations are always visible and clickable
                                val metroStationAtPoint = metroStations.find { station ->
                                    GeoPoint(station.latitude, station.longitude).distanceToAsDouble(stationPoint) < 100
                                }

                                setOnMarkerClickListener { marker, mapView ->
                                    metroStationAtPoint?.let { clickedStation ->
                                        // Toggle between setting as start or end
                                        if (selectedStartStation != clickedStation) {
                                            selectedStartStation = clickedStation
                                        } else if (selectedEndStation != clickedStation) {
                                            selectedEndStation = clickedStation
                                        }
                                        mapView.invalidate()
                                    }
                                    true
                                }
                            }
                            mapView.overlays.add(metroMarker)
                        }

                        // Add user location marker if permission is granted
                        if (hasLocationPermission) {
                            // You can add user location logic here
                            // val userLocationMarker = ...
                        }

                        // Refresh the map
                        mapView.invalidate()
                    }
                }
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isBottomCardExpanded = !isBottomCardExpanded }
                    .animateContentSize() // Add this for animation
                    .align(Alignment.BottomCenter)
                    .padding(16.dp), // Add padding here to lift it from the edges
                shape = RoundedCornerShape(16.dp), // More rounded corners
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp), // Increased elevation for prominence
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)) // Use a slightly elevated surface color
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        "Thông tin chuyến đi",
                        style = MaterialTheme.typography.titleLarge, // Larger title
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp)) // Added space

                    if (isCalculatingRoute) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp), // Larger loading indicator
                                strokeWidth = 3.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Đang tính toán lộ trình...", style = MaterialTheme.typography.bodyMedium)
                        }
                    } else if (selectedStartStation != null && selectedEndStation != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround, // Changed to SpaceAround for better distribution
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "Thời gian",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "25 phút",
                                    style = MaterialTheme.typography.headlineSmall, // Larger and bolder
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "Khoảng cách",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "12.5 km",
                                    style = MaterialTheme.typography.headlineSmall, // Larger and bolder
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    } else {
                        Text(
                            "Vui lòng chọn ga đi và ga đến để xem thông tin chuyến đi.", // More descriptive message
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    // Optional: Expanded content if needed
                    if (isBottomCardExpanded) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Chi tiết lộ trình sẽ hiển thị ở đây khi tính toán xong.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }


            // Floating Action Button to reset map to initial view
            FloatingActionButton(
                onClick = {
                    mapView?.let { map ->
                        val initialPoint = initialView
                        map.controller.animateTo(initialPoint, 14.0, 1000L)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp, 16.dp, 16.dp, 100.dp),
                containerColor = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Reset to initial view",
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }
        }

        if (showStartStationDialog) {
            AlertDialog(
                onDismissRequest = { showStartStationDialog = false },
                title = { Text("Chọn ga đi") },
                text = {
                    LazyColumn {
                        items(metroStations) { station ->
                            ListItem(
                                headlineContent = { Text(station.name) },
                                modifier = Modifier.clickable {
                                    selectedStartStation = station
                                    showStartStationDialog = false
                                }
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showStartStationDialog = false }) {
                        Text("Hủy")
                    }
                }
            )
        }

        if (showEndStationDialog) {
            AlertDialog(
                onDismissRequest = { showEndStationDialog = false },
                title = { Text("Chọn ga đến") },
                text = {
                    LazyColumn {
                        items(metroStations) { station ->
                            ListItem(
                                headlineContent = { Text(station.name) },
                                modifier = Modifier.clickable {
                                    selectedEndStation = station
                                    showEndStationDialog = false
                                }
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showEndStationDialog = false }) {
                        Text("Hủy")
                    }
                }
            )
        }
    }

    // Handle lifecycle events for the map view
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    Configuration.getInstance().load(
                        context,
                        PreferenceManager.getDefaultSharedPreferences(context)
                    )
                }

                Lifecycle.Event.ON_PAUSE -> {
                    // Save the map state if needed
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Preview(showBackground = true, apiLevel = 35)
@Composable
fun RouteScreenPreview() {
    // This preview will not show the map, but it allows you to see the layout structure
    RouteScreen(
        navController = NavController(LocalContext.current),
        metroStationViewModel = hiltViewModel<MetroStationViewModel>()
    )
}