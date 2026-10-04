package com.example.bengkelapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bengkelapp.data.repository.BengkelRepository
import com.example.bengkelapp.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BengkelRepository(application)

    val reportSummary: StateFlow<ReportSummary> = repository.getReportSummary()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReportSummary())

    val allUsers: StateFlow<List<User>> = repository.getUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cashiers: StateFlow<List<User>> = repository.getUsersByRole(UserRole.KASIR)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customers: StateFlow<List<User>> = repository.getUsersByRole(UserRole.CUSTOMER)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vehicles: StateFlow<List<Vehicle>> = repository.getVehicles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val services: StateFlow<List<ServiceItem>> = repository.getServices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val spareParts: StateFlow<List<SparePart>> = repository.getSpareParts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<ServiceOrder>> = repository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val invoices: StateFlow<List<Invoice>> = repository.getInvoices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<WorkshopSettings> = repository.getWorkshopSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WorkshopSettings())

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    fun saveUser(user: User) {
        viewModelScope.launch {
            repository.saveUser(user)
            _toastMessage.emit("Data pengguna berhasil disimpan")
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            repository.deleteUser(userId)
            _toastMessage.emit("Pengguna berhasil dihapus")
        }
    }

    fun saveVehicle(vehicle: Vehicle) {
        viewModelScope.launch {
            repository.saveVehicle(vehicle)
            _toastMessage.emit("Data kendaraan berhasil disimpan")
        }
    }

    fun deleteVehicle(vehicleId: String) {
        viewModelScope.launch {
            repository.deleteVehicle(vehicleId)
            _toastMessage.emit("Kendaraan berhasil dihapus")
        }
    }

    fun saveService(item: ServiceItem) {
        viewModelScope.launch {
            repository.saveService(item)
            _toastMessage.emit("Data layanan berhasil disimpan")
        }
    }

    fun deleteService(serviceId: String) {
        viewModelScope.launch {
            repository.deleteService(serviceId)
            _toastMessage.emit("Layanan berhasil dihapus")
        }
    }

    fun saveSparePart(part: SparePart) {
        viewModelScope.launch {
            repository.saveSparePart(part)
            _toastMessage.emit("Suku cadang berhasil disimpan")
        }
    }

    fun updateSparePartStock(partId: String, delta: Int) {
        viewModelScope.launch {
            repository.updateStock(partId, delta)
            _toastMessage.emit("Stok berhasil diperbarui")
        }
    }

    fun deleteSparePart(partId: String) {
        viewModelScope.launch {
            repository.deleteSparePart(partId)
            _toastMessage.emit("Suku cadang berhasil dihapus")
        }
    }

    fun updateOrderStatus(orderId: String, status: OrderStatus, notes: String = "") {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status, notes)
            _toastMessage.emit("Status pesanan diperbarui ke: ${status.displayName}")
        }
    }

    fun saveSettings(newSettings: WorkshopSettings) {
        viewModelScope.launch {
            repository.saveWorkshopSettings(newSettings)
            _toastMessage.emit("Pengaturan bengkel berhasil disimpan")
        }
    }
}
