package org.com.hcmurs.repositories.station

data class StationsResponse(
    val status: Int,
    val message: String,
    val data: List<Station>
)

data class Station(
    val stationId: Int,
    val stationCode: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val sequenceOrder: Int,
    val status: String,
    val createdAt: String,
    val updatedAt: String,
    val routeId: Int
)