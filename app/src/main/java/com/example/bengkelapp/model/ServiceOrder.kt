package com.example.bengkelapp.model

enum class OrderStatus(val displayName: String) {
    PENDING_CONFIRMATION("Menunggu Konfirmasi"),
    CONFIRMED("Dikonfirmasi"),
    WAITING_WORK("Menunggu Pengerjaan"),
    IN_PROGRESS("Sedang Dikerjakan"),
    WAITING_PAYMENT("Menunggu Pembayaran"),
    COMPLETED("Selesai"),
    CANCELLED("Dibatalkan")
}

data class ServiceOrder(
    val id: String = "",
    val orderNumber: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val vehicleId: String = "",
    val vehiclePlate: String = "",
    val vehicleModel: String = "",
    val serviceIds: List<String> = emptyList(),
    val serviceNames: List<String> = emptyList(),
    val complaint: String = "",
    val damagePhotoUrl: String? = null,
    val bookingDate: String = "",
    val bookingTime: String = "",
    val estimatedCost: Double = 0.0,
    val finalCost: Double = 0.0,
    val status: OrderStatus = OrderStatus.PENDING_CONFIRMATION,
    val notes: String = "",
    val createdAt: String = ""
)
