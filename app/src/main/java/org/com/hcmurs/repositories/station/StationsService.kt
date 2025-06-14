package org.com.hcmurs.repositories.station

import retrofit2.Response
import retrofit2.http.GET

// Đảm bảo bạn đã có các data class này.
// Nếu chưa, bạn cần định nghĩa chúng dựa trên cấu trúc JSON của API của bạn.
// Ví dụ: StationResponse, Station, StationCreateRequest, StationUpdateRequest, CheckLineResponse, v.v.
// Tôi sẽ giả định rằng StationResponse đã được định nghĩa.
// Đối với POST và PUT, tôi sẽ dùng Station làm ví dụ cho request body, nhưng bạn có thể cần các data class riêng cho request (ví dụ: StationCreateRequest)
// Đối với /exists và /check-line, tôi sẽ giả định các response data class phù hợp (ví dụ: ExistsResponse, CheckLineResponse)

interface StationsService {

//    @DELETE("/api/stations/{id}")
//    suspend fun deleteStation(@Path("id") id: Int): Response<Unit> // Response<Unit> nếu API trả về 204 No Content hoặc không có body
//
//    @GET("/api/stations/{id}")
//    suspend fun getStationById(@Path("id") id: Int): Response<Station> // Giả sử API trả về 1 Station

    @GET("/api/stations")
    suspend fun getAllStations(): Response<StationsResponse> // Trả về danh sách trạm, dùng StationResponse đã có

//    @GET("/api/stations/{id}/exists")
//    suspend fun checkStationExists(@Path("id") id: Int): Response<ExistsResponse> // Bạn cần tạo data class ExistsResponse
//
//    @GET("/api/stations/search")
//    suspend fun searchStations(@Query("query") query: String): Response<StationsResponse> // Tìm kiếm theo query parameter
//
//    @GET("/api/stations/route/{routeId}")
//    suspend fun getStationsByRouteId(@Path("routeId") routeId: Int): Response<StationsResponse> // Lấy trạm theo routeId
//
//    @GET("/api/stations/check-line")
//    suspend fun checkLine(): Response<CheckLineResponse> // Bạn cần tạo data class CheckLineResponse
//
//    @POST("/api/stations")
//    suspend fun createStation(@Body station: StationCreateRequest): Response<Station> // Tạo mới trạm, dùng StationCreateRequest cho request body
//
//    @PUT("/api/stations/{id}")
//    suspend fun updateStation(
//        @Path("id") id: Int,
//        @Body station: StationUpdateRequest
//    ): Response<Station> // Cập nhật trạm, dùng StationUpdateRequest cho request body
}

// --- Các Data Class ví dụ cần thiết cho StationsService ---
// Bạn CẦN ĐỊNH NGHĨA CÁC DATA CLASS NÀY DỰA TRÊN API THỰC TẾ CỦA BẠN!

// Nếu StationResponse và Station đã được định nghĩa từ trước thì không cần lặp lại
// data class StationResponse(
//     val status: Int,
//     val message: String,
//     val data: List<Station>
// )
//
// data class Station(
//     val stationId: Int,
//     val stationCode: String,
//     val name: String,
//     val address: String,
//     val latitude: Double,
//     val longitude: Double,
//     val sequenceOrder: Int,
//     val status: String,
//     val createdAt: String,
//     val updatedAt: String,
//     val routeId: Int
// )

// Ví dụ cho request body khi tạo mới trạm
//data class StationCreateRequest(
//    val stationCode: String,
//    val name: String,
//    val address: String,
//    val latitude: Double,
//    val longitude: Double,
//    val sequenceOrder: Int,
//    val status: String, // Hoặc enum nếu có
//    val routeId: Int
//)
//
//// Ví dụ cho request body khi cập nhật trạm (có thể giống StationCreateRequest hoặc ít hơn)
//data class StationUpdateRequest(
//    val stationCode: String?, // Có thể là nullable nếu không phải tất cả trường đều bắt buộc cập nhật
//    val name: String?,
//    val address: String?,
//    val latitude: Double?,
//    val longitude: Double?,
//    val sequenceOrder: Int?,
//    val status: String?,
//    val routeId: Int?
//)
//
//// Ví dụ cho response của endpoint /exists
//data class ExistsResponse(
//    val status: Int,
//    val message: String,
//    val exists: Boolean // Hoặc một tên trường khác mà API trả về
//)
//
//// Ví dụ cho response của endpoint /check-line
//data class CheckLineResponse(
//    val status: Int,
//    val message: String,
//    // Thêm các trường dữ liệu cụ thể mà API trả về cho endpoint này
//    val lineStatus: String?,
//    val details: List<String>? // Ví dụ
//)