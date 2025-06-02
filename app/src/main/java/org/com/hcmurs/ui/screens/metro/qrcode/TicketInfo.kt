package org.com.hcmurs.ui.screens.metro.qrcode

data class TicketInfo(
    val ticketId: String,
    val fromStation: String,
    val toStation: String,
    val price: Double,
    val purchaseTime: Long,
    val validUntil: Long,
    val passengerName: String,
    val ticketType: String // "single", "return", "monthly"
)
