package org.com.hcmurs.ui.screens.metro.order

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.com.hcmurs.repositories.order.OrderInfo
import org.com.hcmurs.repositories.payment.PaymentMethod
import javax.inject.Inject

@HiltViewModel
class OrderInfoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Lấy ticketType từ đối số của navigation
    private val ticketType: String = savedStateHandle.get<String>("ticketType") ?: "Vé lượt"

    // State cho phương thức thanh toán đã chọn
    private val _selectedPaymentMethod = MutableStateFlow<PaymentMethod?>(null)
    val selectedPaymentMethod = _selectedPaymentMethod.asStateFlow()

    // State cho thông tin đơn hàng
    private val _orderInfo = MutableStateFlow<OrderInfo>(generateOrderInfo())
    val orderInfo = _orderInfo.asStateFlow()

    fun onPaymentMethodSelected(method: PaymentMethod) {
        _selectedPaymentMethod.value = method
    }

    private fun generateOrderInfo(): OrderInfo {
        // Di chuyển logic từ màn hình vào ViewModel
        return when (ticketType) {
            "Vé 1 ngày" -> OrderInfo("Vé 1 ngày", "40.000đ", 1, "40.000đ", "24h kể từ thời điểm kích hoạt", "Tự động kích hoạt sau 30 ngày kể từ ngày mua vé")
            "Vé 3 ngày" -> OrderInfo("Vé 3 ngày", "90.000đ", 1, "90.000đ", "72h kể từ thời điểm kích hoạt", "Tự động kích hoạt sau 30 ngày kể từ ngày mua vé")
            "Vé tháng" -> OrderInfo("Vé tháng", "300.000đ", 1, "300.000đ", "30 ngày kể từ thời điểm kích hoạt", "Tự động kích hoạt sau 30 ngày kể từ ngày mua vé")
            // Thêm các loại vé khác ở đây nếu cần
            else -> OrderInfo(ticketType, "N/A", 1, "N/A", "Theo quy định", "Vui lòng xem chi tiết tại quầy vé")
        }
    }
}