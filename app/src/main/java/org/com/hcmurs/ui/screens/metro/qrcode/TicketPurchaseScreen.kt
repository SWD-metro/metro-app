package org.com.hcmurs.ui.screens.metro.qrcode


    import android.graphics.Bitmap
    import androidx.compose.foundation.Image
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.verticalScroll
    import androidx.compose.material3.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.asImageBitmap
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.unit.dp
    import org.com.hcmurs.ui.screens.metro.qrcode.TicketInfo
    import org.com.hcmurs.ui.screens.metro.qrcode.QRGenerator
    import java.text.SimpleDateFormat
    import java.util.*

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TicketPurchaseScreen(
        onNavigateToScanner: () -> Unit
    ) {
        var fromStation by remember { mutableStateOf("") }
        var toStation by remember { mutableStateOf("") }
        var passengerName by remember { mutableStateOf("") }
        var ticketType by remember { mutableStateOf("single") }
        var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
        var showTicket by remember { mutableStateOf(false) }

        val context = LocalContext.current
        val qrGenerator = remember { QRGenerator() }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Mua Vé Metro",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (!showTicket) {
                // Form mua vé
                OutlinedTextField(
                    value = fromStation,
                    onValueChange = { fromStation = it },
                    label = { Text("Ga đi") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = toStation,
                    onValueChange = { toStation = it },
                    label = { Text("Ga đến") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = passengerName,
                    onValueChange = { passengerName = it },
                    label = { Text("Tên hành khách") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Dropdown cho loại vé
                var expanded by remember { mutableStateOf(false) }
                val ticketTypes = listOf("single" to "Vé lượt", "return" to "Vé khứ hồi", "monthly" to "Vé tháng")

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = ticketTypes.find { it.first == ticketType }?.second ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Loại vé") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        ticketTypes.forEach { (value, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    ticketType = value
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (fromStation.isNotBlank() && toStation.isNotBlank() && passengerName.isNotBlank()) {
                            val price = when (ticketType) {
                                "single" -> 15000.0
                                "return" -> 28000.0
                                "monthly" -> 500000.0
                                else -> 15000.0
                            }

                            val ticketInfo = TicketInfo(
                                ticketId = UUID.randomUUID().toString(),
                                fromStation = fromStation,
                                toStation = toStation,
                                price = price,
                                purchaseTime = System.currentTimeMillis(),
                                validUntil = System.currentTimeMillis() + (24 * 60 * 60 * 1000), // 24h
                                passengerName = passengerName,
                                ticketType = ticketType
                            )

                            qrBitmap = qrGenerator.generateQRCode(ticketInfo)
                            showTicket = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Mua Vé")
                }
            } else {
                // Hiển thị vé đã mua
                Text(
                    text = "Vé Của Bạn",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                qrBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier.size(200.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Từ: $fromStation")
                        Text("Đến: $toStation")
                        Text("Hành khách: $passengerName")
                        Text("Loại vé: ${ticketType.find { it.toString() == ticketType }?.toString()}")
                        Text("Giá: ${when(ticketType) {
                            "single" -> "15,000"
                            "return" -> "28,000"
                            "monthly" -> "500,000"
                            else -> "15,000"
                        }} VND")
                        Text("Có hiệu lực đến: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(System.currentTimeMillis() + 24*60*60*1000))}")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { showTicket = false }
                    ) {
                        Text("Mua Vé Mới")
                    }

                    Button(
                        onClick = onNavigateToScanner
                    ) {
                        Text("Quét QR")
                    }
                }
            }
        }
    }
