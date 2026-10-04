package com.bengkel.app.model

data class WorkshopSettings(
    val name: String = "Bengkel App Otomotif",
    val address: String = "Jl. Raya Utama No. 123, Jakarta Selatan",
    val phone: String = "0812-3456-7890",
    val logoUrl: String? = null,
    val operatingHours: String = "Senin - Sabtu: 08.00 - 17.00 WIB",
    val serviceInfo: String = "Melayani Servis Rutin, Perbaikan Mesin, Ganti Oli, dan Suku Cadang Original."
)

