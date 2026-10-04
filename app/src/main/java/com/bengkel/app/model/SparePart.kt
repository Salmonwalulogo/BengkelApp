package com.bengkel.app.model

data class SparePart(
    val id: String = "",
    val code: String = "",
    val name: String = "",
    val buyPrice: Double = 0.0,
    val sellPrice: Double = 0.0,
    val stock: Int = 0,
    val minStock: Int = 5,
    val unit: String = "Pcs",
    val supplier: String = "",
    val updatedAt: String = ""
) {
    val isLowStock: Boolean
        get() = stock <= minStock
}

data class SparePartTransaction(
    val id: String = "",
    val sparePartId: String = "",
    val sparePartName: String = "",
    val type: String = "IN", // "IN" (Masuk) / "OUT" (Keluar)
    val quantity: Int = 0,
    val notes: String = "",
    val date: String = ""
)

