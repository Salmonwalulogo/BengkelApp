package com.example.bengkelapp.model

data class ServiceItem(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val estimatedMinutes: Int = 30,
    val imageUrl: String? = null,
    val isActive: Boolean = true
)
