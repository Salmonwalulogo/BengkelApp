package com.bengkel.app.model

enum class UserRole(val displayName: String) {
    ADMIN("Administrator"),
    KASIR("Kasir Bengkel"),
    CUSTOMER("Pelanggan")
}

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: UserRole = UserRole.CUSTOMER,
    val avatarUrl: String? = null,
    val token: String? = null,
    val createdAt: String = ""
)

