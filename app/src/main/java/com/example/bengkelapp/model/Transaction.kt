package com.example.bengkelapp.model

enum class PaymentStatus(val displayName: String) {
    UNPAID("Belum Dibayar"),
    PENDING("Menunggu Pembayaran"),
    PAID("Dibayar"),
    CANCELLED("Dibatalkan")
}

enum class PaymentMethod(val displayName: String) {
    CASH("Tunai"),
    MIDTRANS("Midtrans (Online)"),
    TRANSFER("Transfer Bank")
}

data class OrderItemDetail(
    val name: String,
    val type: String, // "SERVICE" or "PART"
    val qty: Int,
    val price: Double
) {
    val total: Double get() = qty * price
}

data class Invoice(
    val id: String = "",
    val invoiceNumber: String = "",
    val orderId: String = "",
    val customerName: String = "",
    val cashierName: String = "",
    val vehiclePlate: String = "",
    val vehicleModel: String = "",
    val servicesCost: Double = 0.0,
    val partsCost: Double = 0.0,
    val totalAmount: Double = 0.0,
    val paymentStatus: PaymentStatus = PaymentStatus.UNPAID,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val createdAt: String = "",
    val items: List<OrderItemDetail> = emptyList()
)
