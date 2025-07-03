// In file: app/src/main/java/org/com/hcmurs/repositories/StationRepository.kt
package org.com.hcmurs.repositories.station

// Import Service từ đúng package của bạn
import org.com.hcmurs.repositories.station.StationsService
import org.com.hcmurs.repositories.station.StationsResponse
import retrofit2.Response
import retrofit2.http.GET
import javax.inject.Inject

// Interface định nghĩa các hành động
interface StationRepository {
    suspend fun getAllStations(): Response<StationsResponse>
}

// Class triển khai, và đây chính là nơi thể hiện sự liên quan
class StationRepositoryImpl @Inject constructor(
    // 1. StationRepository YÊU CẦU một StationsService để hoạt động
    private val stationsService: StationsService
) : StationRepository {

    override suspend fun getAllStations(): Response<StationsResponse> {
        // 2. Khi được gọi, nó SỬ DỤNG stationsService để thực hiện lời gọi mạng
        return stationsService.getAllStations()
    }
}