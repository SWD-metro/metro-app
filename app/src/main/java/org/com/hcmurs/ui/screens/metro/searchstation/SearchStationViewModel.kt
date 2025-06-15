package org.com.hcmurs.ui.screens.searchstation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.com.hcmurs.repositories.station.Station
import org.com.hcmurs.repositories.station.StationRepository
import javax.inject.Inject

sealed interface StationUiState {
    data object Loading : StationUiState
    data class Success(val stations: List<Station>) : StationUiState
    data class Error(val message: String) : StationUiState
}

@HiltViewModel
class SearchStationViewModel @Inject constructor(
    private val stationRepository: StationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<StationUiState>(StationUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchAllStations()
    }

    private fun fetchAllStations() {
        viewModelScope.launch {
            _uiState.value = StationUiState.Loading
            try {
                val response = stationRepository.getAllStations()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = StationUiState.Success(response.body()!!.data)
                } else {
                    _uiState.value = StationUiState.Error("Không thể tải danh sách nhà ga.")
                }
            } catch (e: Exception) {
                _uiState.value = StationUiState.Error("Đã xảy ra lỗi mạng: ${e.message}")
            }
        }
    }
}