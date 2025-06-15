package org.com.hcmurs.repositories.payment
import androidx.compose.runtime.Composable

data class PaymentMethod(
    val id: String,
    val name: String,
    val icon: @Composable () -> Unit
)