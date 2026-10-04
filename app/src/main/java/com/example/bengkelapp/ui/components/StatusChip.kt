package com.example.bengkelapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bengkelapp.model.OrderStatus
import com.example.bengkelapp.model.PaymentStatus
import com.example.bengkelapp.ui.theme.*

@Composable
fun OrderStatusChip(
    status: OrderStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status) {
        OrderStatus.PENDING_CONFIRMATION -> StatusPending.copy(alpha = 0.15f) to StatusPending
        OrderStatus.CONFIRMED -> StatusConfirmed.copy(alpha = 0.15f) to StatusConfirmed
        OrderStatus.WAITING_WORK -> StatusPending.copy(alpha = 0.2f) to StatusPending
        OrderStatus.IN_PROGRESS -> StatusInProcess.copy(alpha = 0.15f) to StatusInProcess
        OrderStatus.WAITING_PAYMENT -> StatusPending.copy(alpha = 0.25f) to StatusPending
        OrderStatus.COMPLETED -> StatusSuccess.copy(alpha = 0.15f) to StatusSuccess
        OrderStatus.CANCELLED -> StatusCancelled.copy(alpha = 0.15f) to StatusCancelled
    }

    Text(
        text = status.displayName,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = textColor,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
fun PaymentStatusChip(
    status: PaymentStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status) {
        PaymentStatus.UNPAID -> StatusCancelled.copy(alpha = 0.15f) to StatusCancelled
        PaymentStatus.PENDING -> StatusPending.copy(alpha = 0.15f) to StatusPending
        PaymentStatus.PAID -> StatusSuccess.copy(alpha = 0.15f) to StatusSuccess
        PaymentStatus.CANCELLED -> StatusCancelled.copy(alpha = 0.15f) to StatusCancelled
    }

    Text(
        text = status.displayName,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = textColor,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
