package org.com.hcmurs.utils

object TranslationHelper {
    
    fun getLocalizedTicketName(originalName: String, language: String): String {
        return when (language) {
            "en" -> when {
                originalName.contains("Vé đơn") -> "Vé đơn"
                originalName.contains("Vé 1 ngày") -> "Vé 1 ngày"
                originalName.contains("Vé 3 ngày") -> "Vé 3 ngày"
                originalName.contains("Vé tuần") -> "Vé tuần"
                originalName.contains("Vé tháng") -> "Vé tháng"
                originalName.contains("sinh viên") -> "sinh viên"
                else -> originalName
            }
            else -> originalName
        }
    }

    fun getLocalizedValidity(validity: String, language: String): String {
        return when (language) {
            "en" -> when (validity) {
                "Vé 1 ngày" -> "24h kể từ thời điểm kích hoạt"
                "Vé 3 ngày" -> "72h kể từ thời điểm kích hoạt"
                "Vé tuần" -> "7 ngày kể từ thời điểm kích hoạt"
                "Vé tháng" -> "30 ngày kể từ thời điểm kích hoạt"
                "Vé đơn" -> "Sử dụng một lần"
                else -> "Theo quy định"
            }
            else -> when (validity) {
                "Vé 1 ngày" -> "24h kể từ thời điểm kích hoạt"
                "Vé 3 ngày" -> "72h kể từ thời điểm kích hoạt"
                "Vé tuần" -> "7 ngày kể từ thời điểm kích hoạt"
                "Vé tháng" -> "30 ngày kể từ thời điểm kích hoạt"
                "Vé đơn" -> "Sử dụng một lần"
                else -> "Theo quy định"
            }
        }
    }

    fun getLocalizedNote(ticketName: String, language: String): String {
        return when (language) {
            "en" -> when (ticketName) {
                "Vé 1 ngày", "Vé 3 ngày", "Vé tuần", "Vé tháng" -> "Tự động kích hoạt sau 30 ngày kể từ ngày mua."
                "Vé sinh viên" -> "Tự động kích hoạt sau 30 ngày. Chỉ dành cho HSSV có thẻ hợp lệ."
                else -> "Vui lòng xem chi tiết tại quầy vé."
            }
            else -> when (ticketName) {
                "Vé 1 ngày", "Vé 3 ngày", "Vé tuần", "Vé tháng" -> "Tự động kích hoạt sau 30 ngày kể từ ngày mua."
                "Vé sinh viên" -> "Tự động kích hoạt sau 30 ngày. Chỉ dành cho HSSV có thẻ hợp lệ."
                else -> "Vui lòng xem chi tiết tại quầy vé."
            }
        }
    }

    fun getLocalizedDescription(ticketName: String, language: String): String {
        return when (language) {
            "en" -> when (ticketName) {
                "Vé 1 ngày" -> "Vé cho phép sử dụng tất cả các tuyến Metro trong 24 giờ."
                "Vé 3 ngày" -> "Vé cho phép sử dụng tất cả các tuyến Metro trong 3 ngày."
                "Vé tuần" -> "Sử dụng không giới hạn tất cả các tuyến Metro trong 7 ngày."
                "Vé tháng" -> "Sử dụng không giới hạn tất cả các tuyến Metro trong 1 tháng."
                "Vé sinh viên" -> "Vé ưu đãi cho học sinh, sinh viên sử dụng trong 1 tháng."
                else -> "Detailed ticket information."
            }
            else -> when (ticketName) {
                "Vé 1 ngày" -> "Vé cho phép sử dụng tất cả các tuyến Metro trong 24 giờ."
                "Vé 3 ngày" -> "Vé cho phép sử dụng tất cả các tuyến Metro trong 3 ngày."
                "Vé tuần" -> "Sử dụng không giới hạn tất cả các tuyến Metro trong 7 ngày."
                "Vé tháng" -> "Sử dụng không giới hạn tất cả các tuyến Metro trong 1 tháng."
                "Vé sinh viên" -> "Vé ưu đãi cho học sinh, sinh viên sử dụng trong 1 tháng."
                else -> "Thông tin chi tiết về vé."
            }
        }
    }
}
