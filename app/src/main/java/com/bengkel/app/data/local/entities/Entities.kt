package com.bengkel.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String,
    val avatarUrl: String? = null,
    val createdAt: String
)

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val customerName: String,
    val licensePlate: String,
    val brand: String,
    val type: String,
    val year: String,
    val color: String,
    val frameNumber: String
)

@Entity(tableName = "services")
data class ServiceItemEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val estimatedMinutes: Int,
    val imageUrl: String? = null,
    val isActive: Boolean
)

@Entity(tableName = "spare_parts")
data class SparePartEntity(
    @PrimaryKey val id: String,
    val code: String,
    val name: String,
    val buyPrice: Double,
    val sellPrice: Double,
    val stock: Int,
    val minStock: Int,
    val unit: String,
    val supplier: String,
    val updatedAt: String
)

@Entity(tableName = "service_orders")
data class ServiceOrderEntity(
    @PrimaryKey val id: String,
    val orderNumber: String,
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val vehicleId: String,
    val vehiclePlate: String,
    val vehicleModel: String,
    val serviceIdsJson: String,
    val serviceNamesJson: String,
    val complaint: String,
    val damagePhotoUrl: String? = null,
    val bookingDate: String,
    val bookingTime: String,
    val estimatedCost: Double,
    val finalCost: Double,
    val status: String,
    val notes: String,
    val createdAt: String
)

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey val id: String,
    val invoiceNumber: String,
    val orderId: String,
    val customerName: String,
    val cashierName: String,
    val vehiclePlate: String,
    val vehicleModel: String,
    val servicesCost: Double,
    val partsCost: Double,
    val totalAmount: Double,
    val paymentStatus: String,
    val paymentMethod: String,
    val createdAt: String,
    val itemsJson: String
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String,
    val isRead: Boolean,
    val createdAt: String
)

@Entity(tableName = "workshop_settings")
data class WorkshopSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val address: String,
    val phone: String,
    val logoUrl: String? = null,
    val operatingHours: String,
    val serviceInfo: String
)

