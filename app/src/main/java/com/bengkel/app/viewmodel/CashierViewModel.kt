package com.bengkel.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bengkel.app.data.repository.BengkelRepository
import com.bengkel.app.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CashierViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BengkelRepository(application)

    val orders: StateFlow<List<ServiceOrder>> = repository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val invoices: StateFlow<List<Invoice>> = repository.getInvoices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customers: StateFlow<List<User>> = repository.getUsersByRole(UserRole.CUSTOMER)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vehicles: StateFlow<List<Vehicle>> = repository.getVehicles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val services: StateFlow<List<ServiceItem>> = repository.getActiveServices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val spareParts: StateFlow<List<SparePart>> = repository.getSpareParts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<WorkshopSettings> = repository.getWorkshopSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WorkshopSettings())

    private val _createdInvoice = MutableStateFlow<Invoice?>(null)
    val createdInvoice: StateFlow<Invoice?> = _createdInvoice.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    fun registerDirectService(
        customerName: String,
        customerPhone: String,
        licensePlate: String,
        vehicleModel: String,
        selectedServices: List<ServiceItem>,
        complaint: String
    ) {
        viewModelScope.launch {
            val user = User(
                id = "usr_" + System.currentTimeMillis(),
                name = customerName,
                email = customerName.lowercase().replace(" ", "") + "@guest.com",
                phone = customerPhone,
                role = UserRole.CUSTOMER
            )
            repository.saveUser(user)

            val vehicle = Vehicle(
                id = "veh_" + System.currentTimeMillis(),
                userId = user.id,
                customerName = customerName,
                licensePlate = licensePlate,
                brand = vehicleModel.substringBefore(" "),
                type = vehicleModel,
                year = "2023",
                color = "Bawaan"
            )
            repository.saveVehicle(vehicle)

            val order = ServiceOrder(
                customerId = user.id,
                customerName = customerName,
                customerPhone = customerPhone,
                vehicleId = vehicle.id,
                vehiclePlate = licensePlate,
                vehicleModel = vehicleModel,
                serviceIds = selectedServices.map { it.id },
                serviceNames = selectedServices.map { it.name },
                complaint = complaint,
                bookingDate = "Hari Ini",
                bookingTime = "Direct Walk-in",
                estimatedCost = selectedServices.sumOf { it.price },
                status = OrderStatus.CONFIRMED
            )

            repository.createServiceOrder(order)
            _toastMessage.emit("Servis langsung berhasil didaftarkan!")
        }
    }

    fun processPayment(
        orderId: String,
        cashierName: String,
        items: List<OrderItemDetail>,
        method: PaymentMethod
    ) {
        viewModelScope.launch {
            val inv = repository.createInvoice(orderId, cashierName, items, method)
            _createdInvoice.value = inv
            _toastMessage.emit("Pembayaran berhasil! Struk telah diterbitkan.")
        }
    }

    fun updateOrderStatus(orderId: String, status: OrderStatus, notes: String = "") {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status, notes)
            _toastMessage.emit("Status pengerjaan diperbarui.")
        }
    }

    fun clearCreatedInvoice() {
        _createdInvoice.value = null
    }
}

