package com.example.bengkelapp.model

data class Vehicle(
    val id: String = "",
    val userId: String = "",
    val customerName: String = "",
    val licensePlate: String = "", // Nomor Polisi (misal: B 1234 ABC)
    val brand: String = "",        // Merek (misal: Honda, Yamaha, Toyota)
    val type: String = "",         // Tipe/Model (misal: Vario 125, Avanza)
    val year: String = "",         // Tahun Kendaraan
    val color: String = "",        // Warna
    val frameNumber: String = ""   // Nomor Rangka (opsional)
)
