package com.example.bengkelapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bengkelapp.data.repository.BengkelRepository
import com.example.bengkelapp.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CustomerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BengkelRepository(application)
    private val currentUser = repository.getCurrentUser()

    val myVehicles: StateFlow<List<Vehicle>> = if (currentUser != null) {
        repository.getVehiclesByCustomer(currentUser.id)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } else {
        MutableStateFlow(emptyList())
    }

    val availableServices: StateFlow<List<ServiceItem>> = repository.getActiveServices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myOrders: StateFlow<List<ServiceOrder>> = if (currentUser != null) {
        repository.getOrdersByCustomer(currentUser.id)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } else {
        MutableStateFlow(emptyList())
    }

    val notifications: StateFlow<List<NotificationItem>> = if (currentUser != null) {
        repository.getNotifications(currentUser.id)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } else {
        MutableStateFlow(emptyList())
    }

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    fun saveVehicle(licensePlate: String, brand: String, type: String, year: String, color: String, frameNum: String) {
        val user = currentUser ?: return
        viewModelScope.launch {
            val vehicle = Vehicle(
                userId = user.id,
                customerName = user.name,
                licensePlate = licensePlate,
                brand = brand,
                type = type,
                year = year,
                color = color,
                frameNumber = frameNum
            )
            repository.saveVehicle(vehicle)
            _toastMessage.emit("Kendaraan berhasil ditambahkan")
        }
    }

    fun deleteVehicle(vehicleId: String) {
        viewModelScope.launch {
            repository.deleteVehicle(vehicleId)
            _toastMessage.emit("Kendaraan berhasil dihapus")
        }
    }

    fun bookOnlineService(
        vehicle: Vehicle,
        selectedServices: List<ServiceItem>,
        bookingDate: String,
        bookingTime: String,
        complaint: String,
        damagePhotoUrl: String?
    ) {
        val user = currentUser ?: return
        viewModelScope.launch {
            val estCost = selectedServices.sumOf { it.price }
            val order = ServiceOrder(
                customerId = user.id,
                customerName = user.name,
                customerPhone = user.phone,
                vehicleId = vehicle.id,
                vehiclePlate = vehicle.licensePlate,
                vehicleModel = "${vehicle.brand} ${vehicle.type}",
                serviceIds = selectedServices.map { it.id },
                serviceNames = selectedServices.map { it.name },
                complaint = complaint,
                damagePhotoUrl = damagePhotoUrl,
                bookingDate = bookingDate,
                bookingTime = bookingTime,
                estimatedCost = estCost,
                status = OrderStatus.PENDING_CONFIRMATION
            )
            repository.createServiceOrder(order)
            _toastMessage.emit("Pesanan servis online berhasil terkirim!")
        }
    }

    fun markNotificationRead(notifId: String) {
        viewModelScope.launch {
            repository.markNotificationRead(notifId)
        }
    }

    fun updateProfile(name: String, email: String, phone: String) {
        val user = currentUser ?: return
        viewModelScope.launch {
            val updated = user.copy(name = name, email = email, phone = phone)
            repository.saveUser(updated)
            _toastMessage.emit("Profil berhasil diperbarui")
        }
    }
}
