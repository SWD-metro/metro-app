// In D:\...\searchstation\SearchStationViewModel.kt
package org.com.hcmurs.ui.screens.searchstation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.com.hcmurs.repositories.station.Station
import org.com.hcmurs.repositories.station.StationsResponse
import org.com.hcmurs.repositories.station.StationRepository // Bạn cần tạo hoặc đảm bảo có Repository này
import javax.inject.Inject

/**
 * Sealed interface để đại diện cho các trạng thái của giao diện màn hình tìm kiếm ga.
 */
sealed interface StationUiState {
    data object Loading : StationUiState
    data class Success(val stations: List<Station>) : StationUiState
    data class Error(val message: String) : StationUiState
}

@HiltViewModel
class SearchStationViewModel @Inject constructor(
    private val stationRepository: StationRepository // Hilt sẽ tự động cung cấp Repository này
) : ViewModel() {

    // StateFlow riêng tư để quản lý trạng thái từ bên trong ViewModel
    private val _uiState = MutableStateFlow<StationUiState>(StationUiState.Loading)
    // StateFlow công khai, chỉ cho phép đọc, để giao diện lắng nghe
    val uiState = _uiState.asStateFlow()

    init {
        // Gọi hàm lấy dữ liệu ngay khi ViewModel được tạo
        fetchAllStations()
    }

    /**
     * Lấy danh sách tất cả các nhà ga từ repository và cập nhật UI state.
     */
    private fun fetchAllStations() {
        // Chạy coroutine trong scope của ViewModel để đảm bảo an toàn về vòng đời
        viewModelScope.launch {
            _uiState.value = StationUiState.Loading // Cập nhật trạng thái là đang tải
            try {
                // Gọi API thông qua repository
                val response = stationRepository.getAllStations()
                if (response.isSuccessful && response.body() != null) {
                    // Nếu thành công, cập nhật trạng thái Success với dữ liệu nhận được
                    _uiState.value = StationUiState.Success(response.body()!!.data)
                } else {
                    // Nếu thất bại (ví dụ: server trả lỗi 404, 500), cập nhật trạng thái Error
                    _uiState.value = StationUiState.Error("Không thể tải danh sách nhà ga.")
                }
            } catch (e: Exception) {
                // Nếu có lỗi mạng hoặc lỗi khác, cập nhật trạng thái Error
                _uiState.value = StationUiState.Error("Đã xảy ra lỗi mạng: ${e.message}")
            }
        }
    }
}