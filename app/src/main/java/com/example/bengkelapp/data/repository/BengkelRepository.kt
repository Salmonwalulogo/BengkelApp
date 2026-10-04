package com.example.bengkelapp.data.repository

import android.content.Context
import com.example.bengkelapp.data.local.AppDatabase
import com.example.bengkelapp.data.local.entities.*
import com.example.bengkelapp.data.pref.SessionManager
import com.example.bengkelapp.data.remote.ApiConfig
import com.example.bengkelapp.data.remote.LoginRequest
import com.example.bengkelapp.data.remote.RegisterRequest
import com.example.bengkelapp.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class BengkelRepository(private val context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val dao = db.bengkelDao()
    private val sessionManager = SessionManager(context)
    private val gson = Gson()

    suspend fun initializeDatabaseIfEmpty() = withContext(Dispatchers.IO) {
        val users = dao.getAllUsers()
        if (users.isEmpty()) {
            seedInitialData()
        }
    }

    private suspend fun seedInitialData() = withContext(Dispatchers.IO) {
        // Initial Users
        val admin = UserEntity("usr_admin", "Admin Bengkel", "admin@bengkel.com", "08123456789", "ADMIN", null, "2025-01-01 08:00")
        val cashier1 = UserEntity("usr_kasir1", "Budi Kasir", "kasir1@bengkel.com", "08129876543", "KASIR", null, "2025-01-01 08:00")
        val customer1 = UserEntity("usr_cust1", "Ahmad Pelanggan", "customer@bengkel.com", "081311223344", "CUSTOMER", null, "2025-01-01 08:00")
        val customer2 = UserEntity("usr_cust2", "Siti Nurhaliza", "siti@bengkel.com", "081355667788", "CUSTOMER", null, "2025-01-02 09:00")

        dao.insertUsers(listOf(admin, cashier1, customer1, customer2))

        // Initial Vehicles
        val v1 = VehicleEntity("v1", "usr_cust1", "Ahmad Pelanggan", "B 1234 ABC", "Honda", "Vario 125", "2022", "Hitam", "MH123456789")
        val v2 = VehicleEntity("v2", "usr_cust1", "Ahmad Pelanggan", "B 5678 XYZ", "Yamaha", "NMAX 155", "2021", "Biru", "MH987654321")
        val v3 = VehicleEntity("v3", "usr_cust2", "Siti Nurhaliza", "B 9999 SITI", "Honda", "BeAT Street", "2023", "Putih", "MH112233445")

        dao.insertVehicles(listOf(v1, v2, v3))

        // Initial Services
        val s1 = ServiceItemEntity("s1", "Servis Ringan", "Pemeriksaan rutin, pembersihan karburator/throttle body, cek rem", 75000.0, 45, null, true)
        val s2 = ServiceItemEntity("s2", "Ganti Oli Mesin", "Penggantian oli mesin dengan oli sintetis berkualitas tinggi", 50000.0, 20, null, true)
        val s3 = ServiceItemEntity("s3", "Servis CVT", "Pembersihan CVT, roler, v-belt, dan pemberian kompon grease", 85000.0, 50, null, true)
        val s4 = ServiceItemEntity("s4", "Tune-Up & Calibrate", "Penyetelan klep, pembersihan busi, reset ECU injection", 120000.0, 60, null, true)
        val s5 = ServiceItemEntity("s5", "Servis Berat / Overhaul", "Bongkar mesin, skir klep, ganti paking, pembersihan kerak karbon", 350000.0, 240, null, true)

        dao.insertServices(listOf(s1, s2, s3, s4, s5))

        // Initial Spare Parts
        val p1 = SparePartEntity("p1", "SP-001", "Oli MPX2 10W-30 (0.8L)", 42000.0, 55000.0, 25, 5, "Botol", "Astra Honda", "2025-02-01")
        val p2 = SparePartEntity("p2", "SP-002", "Oli Yamalube Super Matic (1L)", 48000.0, 62000.0, 18, 5, "Botol", "Yamaha Motor", "2025-02-01")
        val p3 = SparePartEntity("p3", "SP-003", "Kampas Rem Depan Vario", 28000.0, 40000.0, 12, 3, "Set", "AHM Parts", "2025-02-01")
        val p4 = SparePartEntity("p4", "SP-004", "Roller Set CVT NMAX", 50000.0, 75000.0, 3, 5, "Set", "YGP Parts", "2025-02-01")
        val p5 = SparePartEntity("p5", "SP-005", "Busi NGK CPR9EA-9", 18000.0, 28000.0, 30, 10, "Pcs", "NGK Spark", "2025-02-01")

        dao.insertSpareParts(listOf(p1, p2, p3, p4, p5))

        // Initial Service Orders
        val o1 = ServiceOrderEntity(
            id = "ord1",
            orderNumber = "ORD-20250228-001",
            customerId = "usr_cust1",
            customerName = "Ahmad Pelanggan",
            customerPhone = "081311223344",
            vehicleId = "v1",
            vehiclePlate = "B 1234 ABC",
            vehicleModel = "Honda Vario 125",
            serviceIdsJson = gson.toJson(listOf("s1", "s2")),
            serviceNamesJson = gson.toJson(listOf("Servis Ringan", "Ganti Oli Mesin")),
            complaint = "Tarikan mesin terasa berat, ganti oli mesin rutin",
            damagePhotoUrl = null,
            bookingDate = "2025-02-28",
            bookingTime = "09:00 WIB",
            estimatedCost = 125000.0,
            finalCost = 130000.0,
            status = OrderStatus.COMPLETED.name,
            notes = "Mesin sudah kembali halus",
            createdAt = "2025-02-28 08:30"
        )

        val o2 = ServiceOrderEntity(
            id = "ord2",
            orderNumber = "ORD-20250228-002",
            customerId = "usr_cust2",
            customerName = "Siti Nurhaliza",
            customerPhone = "081355667788",
            vehicleId = "v3",
            vehiclePlate = "B 9999 SITI",
            vehicleModel = "Honda BeAT Street",
            serviceIdsJson = gson.toJson(listOf("s3")),
            serviceNamesJson = gson.toJson(listOf("Servis CVT")),
            complaint = "Area CVT gredek saat awal gas",
            damagePhotoUrl = null,
            bookingDate = "2025-02-28",
            bookingTime = "11:00 WIB",
            estimatedCost = 85000.0,
            finalCost = 85000.0,
            status = OrderStatus.IN_PROGRESS.name,
            notes = "Sedang dibersihkan pully dan roler",
            createdAt = "2025-02-28 10:15"
        )

        dao.insertOrders(listOf(o1, o2))

        // Initial Invoices
        val inv1 = InvoiceEntity(
            id = "inv1",
            invoiceNumber = "INV-20250228-001",
            orderId = "ord1",
            customerName = "Ahmad Pelanggan",
            cashierName = "Budi Kasir",
            vehiclePlate = "B 1234 ABC",
            vehicleModel = "Honda Vario 125",
            servicesCost = 125000.0,
            partsCost = 55000.0,
            totalAmount = 180000.0,
            paymentStatus = PaymentStatus.PAID.name,
            paymentMethod = PaymentMethod.CASH.name,
            createdAt = "2025-02-28 09:45",
            itemsJson = gson.toJson(listOf(
                OrderItemDetail("Servis Ringan", "SERVICE", 1, 75000.0),
                OrderItemDetail("Ganti Oli Mesin", "SERVICE", 1, 50000.0),
                OrderItemDetail("Oli MPX2 10W-30 (0.8L)", "PART", 1, 55000.0)
            ))
        )

        dao.insertInvoices(listOf(inv1))

        // Initial Workshop Settings
        val settings = WorkshopSettingsEntity(
            id = 1,
            name = "Bengkel App Otomotif",
            address = "Jl. Raya Utama No. 123, Jakarta Selatan",
            phone = "0812-3456-7890",
            logoUrl = null,
            operatingHours = "Senin - Sabtu: 08.00 - 17.00 WIB",
            serviceInfo = "Melayani Servis Rutin, Perbaikan Mesin, Ganti Oli, dan Suku Cadang Original."
        )

        dao.saveWorkshopSettings(settings)

        // Initial Notification
        val notif1 = NotificationEntity(
            id = "n1",
            userId = "usr_cust1",
            title = "Pesanan Servis Selesai",
            message = "Servis kendaraan B 1234 ABC telah selesai dikerjakan.",
            type = "SUCCESS",
            isRead = false,
            createdAt = "2025-02-28 09:45"
        )

        dao.insertNotification(notif1)
    }

    // --- AUTHENTICATION ---
    suspend fun login(email: String, pass: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            if (sessionManager.isMockMode()) {
                val users = dao.getAllUsers()
                val found = users.find { it.email.equals(email, ignoreCase = true) }
                if (found != null) {
                    val user = User(
                        id = found.id,
                        name = found.name,
                        email = found.email,
                        phone = found.phone,
                        role = UserRole.valueOf(found.role),
                        avatarUrl = found.avatarUrl,
                        token = "mock_token_${found.id}",
                        createdAt = found.createdAt
                    )
                    sessionManager.saveSession(user, user.token)
                    Result.success(user)
                } else {
                    val role = when {
                        email.contains("admin") -> UserRole.ADMIN
                        email.contains("kasir") -> UserRole.KASIR
                        else -> UserRole.CUSTOMER
                    }
                    val newUser = User(
                        id = "usr_" + System.currentTimeMillis(),
                        name = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                        email = email,
                        phone = "08123456789",
                        role = role,
                        createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                    )
                    dao.insertUser(UserEntity(newUser.id, newUser.name, newUser.email, newUser.phone, newUser.role.name, null, newUser.createdAt))
                    sessionManager.saveSession(newUser, "mock_token_" + newUser.id)
                    Result.success(newUser)
                }
            } else {
                val response = ApiConfig.getApiService().login(LoginRequest(email, pass))
                if (response.isSuccessful && response.body()?.success == true) {
                    val auth = response.body()!!
                    val user = auth.user ?: throw Exception("Data user kosong")
                    sessionManager.saveSession(user, auth.token)
                    Result.success(user)
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Login gagal. Periksa email dan password."))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, phone: String, pass: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            if (sessionManager.isMockMode()) {
                val newUser = User(
                    id = "usr_" + System.currentTimeMillis(),
                    name = name,
                    email = email,
                    phone = phone,
                    role = UserRole.CUSTOMER,
                    createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                )
                dao.insertUser(UserEntity(newUser.id, newUser.name, newUser.email, newUser.phone, newUser.role.name, null, newUser.createdAt))
                sessionManager.saveSession(newUser, "mock_token_" + newUser.id)
                Result.success(newUser)
            } else {
                val response = ApiConfig.getApiService().register(RegisterRequest(name, email, phone, pass, pass))
                if (response.isSuccessful && response.body()?.success == true) {
                    val auth = response.body()!!
                    val user = auth.user ?: throw Exception("Data user kosong")
                    sessionManager.saveSession(user, auth.token)
                    Result.success(user)
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Registrasi gagal."))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        sessionManager.clearSession()
    }

    fun getCurrentUser(): User? = sessionManager.getUser()

    // --- USERS MANAGEMENT ---
    fun getUsers(): Flow<List<User>> = flow {
        emit(dao.getAllUsers().map { User(it.id, it.name, it.email, it.phone, UserRole.valueOf(it.role), it.avatarUrl, null, it.createdAt) })
    }.flowOn(Dispatchers.IO)

    fun getUsersByRole(role: UserRole): Flow<List<User>> = flow {
        emit(dao.getUsersByRole(role.name).map { User(it.id, it.name, it.email, it.phone, UserRole.valueOf(it.role), it.avatarUrl, null, it.createdAt) })
    }.flowOn(Dispatchers.IO)

    suspend fun saveUser(user: User) = withContext(Dispatchers.IO) {
        val entity = UserEntity(
            id = user.id.ifEmpty { "usr_" + System.currentTimeMillis() },
            name = user.name,
            email = user.email,
            phone = user.phone,
            role = user.role.name,
            avatarUrl = user.avatarUrl,
            createdAt = user.createdAt.ifEmpty { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()) }
        )
        dao.insertUser(entity)
    }

    suspend fun deleteUser(userId: String) = withContext(Dispatchers.IO) {
        val entity = dao.getUserById(userId)
        if (entity != null) dao.deleteUser(entity)
    }

    // --- VEHICLES MANAGEMENT ---
    fun getVehicles(): Flow<List<Vehicle>> = flow {
        emit(dao.getAllVehicles().map { Vehicle(it.id, it.userId, it.customerName, it.licensePlate, it.brand, it.type, it.year, it.color, it.frameNumber) })
    }.flowOn(Dispatchers.IO)

    fun getVehiclesByCustomer(userId: String): Flow<List<Vehicle>> = flow {
        emit(dao.getVehiclesByUserId(userId).map { Vehicle(it.id, it.userId, it.customerName, it.licensePlate, it.brand, it.type, it.year, it.color, it.frameNumber) })
    }.flowOn(Dispatchers.IO)

    suspend fun saveVehicle(vehicle: Vehicle) = withContext(Dispatchers.IO) {
        val entity = VehicleEntity(
            id = vehicle.id.ifEmpty { "veh_" + System.currentTimeMillis() },
            userId = vehicle.userId,
            customerName = vehicle.customerName,
            licensePlate = vehicle.licensePlate,
            brand = vehicle.brand,
            type = vehicle.type,
            year = vehicle.year,
            color = vehicle.color,
            frameNumber = vehicle.frameNumber
        )
        dao.insertVehicle(entity)
    }

    suspend fun deleteVehicle(vehicleId: String) = withContext(Dispatchers.IO) {
        val v = dao.getVehicleById(vehicleId)
        if (v != null) dao.deleteVehicle(v)
    }

    // --- SERVICES MANAGEMENT ---
    fun getServices(): Flow<List<ServiceItem>> = flow {
        emit(dao.getAllServices().map { ServiceItem(it.id, it.name, it.description, it.price, it.estimatedMinutes, it.imageUrl, it.isActive) })
    }.flowOn(Dispatchers.IO)

    fun getActiveServices(): Flow<List<ServiceItem>> = flow {
        emit(dao.getActiveServices().map { ServiceItem(it.id, it.name, it.description, it.price, it.estimatedMinutes, it.imageUrl, it.isActive) })
    }.flowOn(Dispatchers.IO)

    suspend fun saveService(item: ServiceItem) = withContext(Dispatchers.IO) {
        val entity = ServiceItemEntity(
            id = item.id.ifEmpty { "srv_" + System.currentTimeMillis() },
            name = item.name,
            description = item.description,
            price = item.price,
            estimatedMinutes = item.estimatedMinutes,
            imageUrl = item.imageUrl,
            isActive = item.isActive
        )
        dao.insertService(entity)
    }

    suspend fun deleteService(serviceId: String) = withContext(Dispatchers.IO) {
        val list = dao.getAllServices()
        val match = list.find { it.id == serviceId }
        if (match != null) dao.deleteService(match)
    }

    // --- SPARE PARTS MANAGEMENT ---
    fun getSpareParts(): Flow<List<SparePart>> = flow {
        emit(dao.getAllSpareParts().map { SparePart(it.id, it.code, it.name, it.buyPrice, it.sellPrice, it.stock, it.minStock, it.unit, it.supplier, it.updatedAt) })
    }.flowOn(Dispatchers.IO)

    fun getLowStockSpareParts(): Flow<List<SparePart>> = flow {
        emit(dao.getLowStockSpareParts().map { SparePart(it.id, it.code, it.name, it.buyPrice, it.sellPrice, it.stock, it.minStock, it.unit, it.supplier, it.updatedAt) })
    }.flowOn(Dispatchers.IO)

    suspend fun saveSparePart(part: SparePart) = withContext(Dispatchers.IO) {
        val entity = SparePartEntity(
            id = part.id.ifEmpty { "prt_" + System.currentTimeMillis() },
            code = part.code.ifEmpty { "SP-" + (100..999).random() },
            name = part.name,
            buyPrice = part.buyPrice,
            sellPrice = part.sellPrice,
            stock = part.stock,
            minStock = part.minStock,
            unit = part.unit,
            supplier = part.supplier,
            updatedAt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )
        dao.insertSparePart(entity)
    }

    suspend fun updateStock(partId: String, deltaQuantity: Int) = withContext(Dispatchers.IO) {
        val part = dao.getSparePartById(partId)
        if (part != null) {
            val updatedStock = (part.stock + deltaQuantity).coerceAtLeast(0)
            val updated = part.copy(
                stock = updatedStock,
                updatedAt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            )
            dao.insertSparePart(updated)
        }
    }

    suspend fun deleteSparePart(partId: String) = withContext(Dispatchers.IO) {
        val part = dao.getSparePartById(partId)
        if (part != null) dao.deleteSparePart(part)
    }

    // --- SERVICE ORDERS MANAGEMENT ---
    fun getAllOrders(): Flow<List<ServiceOrder>> = flow {
        emit(dao.getAllOrders().map { mapOrderEntityToModel(it) })
    }.flowOn(Dispatchers.IO)

    fun getOrdersByCustomer(customerId: String): Flow<List<ServiceOrder>> = flow {
        emit(dao.getOrdersByCustomer(customerId).map { mapOrderEntityToModel(it) })
    }.flowOn(Dispatchers.IO)

    suspend fun getOrderById(id: String): ServiceOrder? = withContext(Dispatchers.IO) {
        val entity = dao.getOrderById(id) ?: return@withContext null
        mapOrderEntityToModel(entity)
    }

    private fun mapOrderEntityToModel(it: ServiceOrderEntity): ServiceOrder {
        val serviceIds: List<String> = try {
            gson.fromJson(it.serviceIdsJson, object : TypeToken<List<String>>() {}.type) ?: emptyList()
        } catch (e: Exception) { emptyList() }

        val serviceNames: List<String> = try {
            gson.fromJson(it.serviceNamesJson, object : TypeToken<List<String>>() {}.type) ?: emptyList()
        } catch (e: Exception) { emptyList() }

        return ServiceOrder(
            id = it.id,
            orderNumber = it.orderNumber,
            customerId = it.customerId,
            customerName = it.customerName,
            customerPhone = it.customerPhone,
            vehicleId = it.vehicleId,
            vehiclePlate = it.vehiclePlate,
            vehicleModel = it.vehicleModel,
            serviceIds = serviceIds,
            serviceNames = serviceNames,
            complaint = it.complaint,
            damagePhotoUrl = it.damagePhotoUrl,
            bookingDate = it.bookingDate,
            bookingTime = it.bookingTime,
            estimatedCost = it.estimatedCost,
            finalCost = it.finalCost,
            status = OrderStatus.valueOf(it.status),
            notes = it.notes,
            createdAt = it.createdAt
        )
    }

    suspend fun createServiceOrder(order: ServiceOrder): ServiceOrder = withContext(Dispatchers.IO) {
        val dateStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val count = dao.getAllOrders().size + 1
        val generatedOrderNum = "ORD-$dateStr-" + String.format(Locale.getDefault(), "%03d", count)

        val entity = ServiceOrderEntity(
            id = order.id.ifEmpty { "ord_" + System.currentTimeMillis() },
            orderNumber = generatedOrderNum,
            customerId = order.customerId,
            customerName = order.customerName,
            customerPhone = order.customerPhone,
            vehicleId = order.vehicleId,
            vehiclePlate = order.vehiclePlate,
            vehicleModel = order.vehicleModel,
            serviceIdsJson = gson.toJson(order.serviceIds),
            serviceNamesJson = gson.toJson(order.serviceNames),
            complaint = order.complaint,
            damagePhotoUrl = order.damagePhotoUrl,
            bookingDate = order.bookingDate,
            bookingTime = order.bookingTime,
            estimatedCost = order.estimatedCost,
            finalCost = order.estimatedCost,
            status = order.status.name,
            notes = order.notes,
            createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        )
        dao.insertOrder(entity)

        val notif = NotificationEntity(
            id = "n_" + System.currentTimeMillis(),
            userId = order.customerId,
            title = "Pesanan Servis Berhasil Dibuat",
            message = "Pesanan #${entity.orderNumber} untuk ${order.vehiclePlate} telah masuk sistem.",
            type = "INFO",
            isRead = false,
            createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        )
        dao.insertNotification(notif)

        mapOrderEntityToModel(entity)
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus, notes: String = "") = withContext(Dispatchers.IO) {
        val existing = dao.getOrderById(orderId)
        if (existing != null) {
            val updated = existing.copy(
                status = newStatus.name,
                notes = if (notes.isNotEmpty()) notes else existing.notes
            )
            dao.insertOrder(updated)

            val notifText = when (newStatus) {
                OrderStatus.CONFIRMED -> "Pesanan #${existing.orderNumber} telah dikonfirmasi bengkel."
                OrderStatus.IN_PROGRESS -> "Kendaraan ${existing.vehiclePlate} sedang dikerjakan mekanik."
                OrderStatus.WAITING_PAYMENT -> "Pengerjaan selesai. Tagihan untuk #${existing.orderNumber} siap dibayar."
                OrderStatus.COMPLETED -> "Servis selesai. Terima kasih telah mempercayai layanan kami!"
                OrderStatus.CANCELLED -> "Pesanan #${existing.orderNumber} telah dibatalkan."
                else -> "Status pesanan #${existing.orderNumber} telah diperbarui: ${newStatus.displayName}"
            }
            dao.insertNotification(NotificationEntity(
                id = "n_" + System.currentTimeMillis(),
                userId = existing.customerId,
                title = "Pembaruan Status Servis",
                message = notifText,
                type = if (newStatus == OrderStatus.COMPLETED) "SUCCESS" else "INFO",
                isRead = false,
                createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            ))
        }
    }

    // --- TRANSACTIONS & INVOICES MANAGEMENT ---
    fun getInvoices(): Flow<List<Invoice>> = flow {
        emit(dao.getAllInvoices().map { mapInvoiceEntityToModel(it) })
    }.flowOn(Dispatchers.IO)

    suspend fun getInvoiceByOrderId(orderId: String): Invoice? = withContext(Dispatchers.IO) {
        val entity = dao.getInvoiceByOrderId(orderId) ?: return@withContext null
        mapInvoiceEntityToModel(entity)
    }

    private fun mapInvoiceEntityToModel(it: InvoiceEntity): Invoice {
        val items: List<OrderItemDetail> = try {
            gson.fromJson(it.itemsJson, object : TypeToken<List<OrderItemDetail>>() {}.type) ?: emptyList()
        } catch (e: Exception) { emptyList() }

        return Invoice(
            id = it.id,
            invoiceNumber = it.invoiceNumber,
            orderId = it.orderId,
            customerName = it.customerName,
            cashierName = it.cashierName,
            vehiclePlate = it.vehiclePlate,
            vehicleModel = it.vehicleModel,
            servicesCost = it.servicesCost,
            partsCost = it.partsCost,
            totalAmount = it.totalAmount,
            paymentStatus = PaymentStatus.valueOf(it.paymentStatus),
            paymentMethod = PaymentMethod.valueOf(it.paymentMethod),
            createdAt = it.createdAt,
            items = items
        )
    }

    suspend fun createInvoice(
        orderId: String,
        cashierName: String,
        items: List<OrderItemDetail>,
        paymentMethod: PaymentMethod
    ): Invoice = withContext(Dispatchers.IO) {
        val order = dao.getOrderById(orderId)
        val dateStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val count = dao.getAllInvoices().size + 1
        val invNumber = "INV-$dateStr-" + String.format(Locale.getDefault(), "%03d", count)

        val serviceTotal = items.filter { it.type == "SERVICE" }.sumOf { it.total }
        val partsTotal = items.filter { it.type == "PART" }.sumOf { it.total }
        val grandTotal = serviceTotal + partsTotal

        val entity = InvoiceEntity(
            id = "inv_" + System.currentTimeMillis(),
            invoiceNumber = invNumber,
            orderId = orderId,
            customerName = order?.customerName ?: "Pelanggan",
            cashierName = cashierName,
            vehiclePlate = order?.vehiclePlate ?: "-",
            vehicleModel = order?.vehicleModel ?: "-",
            servicesCost = serviceTotal,
            partsCost = partsTotal,
            totalAmount = grandTotal,
            paymentStatus = PaymentStatus.PAID.name,
            paymentMethod = paymentMethod.name,
            createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
            itemsJson = gson.toJson(items)
        )

        dao.insertInvoice(entity)

        if (order != null) {
            val updatedOrder = order.copy(
                status = OrderStatus.COMPLETED.name,
                finalCost = grandTotal
            )
            dao.insertOrder(updatedOrder)
        }

        items.filter { it.type == "PART" }.forEach { item ->
            val allParts = dao.getAllSpareParts()
            val matchedPart = allParts.find { it.name.contains(item.name, ignoreCase = true) }
            if (matchedPart != null) {
                updateStock(matchedPart.id, -item.qty)
            }
        }

        mapInvoiceEntityToModel(entity)
    }

    // --- WORKSHOP SETTINGS ---
    fun getWorkshopSettings(): Flow<WorkshopSettings> = flow {
        val it = dao.getWorkshopSettings()
        val settings = if (it == null) WorkshopSettings()
        else WorkshopSettings(it.name, it.address, it.phone, it.logoUrl, it.operatingHours, it.serviceInfo)
        emit(settings)
    }.flowOn(Dispatchers.IO)

    suspend fun saveWorkshopSettings(settings: WorkshopSettings) = withContext(Dispatchers.IO) {
        val entity = WorkshopSettingsEntity(
            id = 1,
            name = settings.name,
            address = settings.address,
            phone = settings.phone,
            logoUrl = settings.logoUrl,
            operatingHours = settings.operatingHours,
            serviceInfo = settings.serviceInfo
        )
        dao.saveWorkshopSettings(entity)
    }

    // --- NOTIFICATIONS ---
    fun getNotifications(userId: String): Flow<List<NotificationItem>> = flow {
        emit(dao.getNotificationsByUser(userId).map { NotificationItem(it.id, it.userId, it.title, it.message, it.type, it.isRead, it.createdAt) })
    }.flowOn(Dispatchers.IO)

    suspend fun markNotificationRead(id: String) = withContext(Dispatchers.IO) {
        dao.markNotificationAsRead(id)
    }

    // --- REPORTS & DASHBOARD METRICS ---
    fun getReportSummary(): Flow<ReportSummary> = flow {
        val users = dao.getAllUsers()
        val vehicles = dao.getAllVehicles()
        val orders = dao.getAllOrders()
        val invoices = dao.getAllInvoices()

        val totalRevenue = invoices.sumOf { it.totalAmount }
        val totalTransactions = invoices.size
        val totalCustomers = users.count { it.role == UserRole.CUSTOMER.name }
        val totalCashiers = users.count { it.role == UserRole.KASIR.name }
        val totalVehicles = vehicles.size

        val pending = orders.count { it.status == OrderStatus.PENDING_CONFIRMATION.name || it.status == OrderStatus.CONFIRMED.name }
        val inProgress = orders.count { it.status == OrderStatus.WAITING_WORK.name || it.status == OrderStatus.IN_PROGRESS.name }
        val completed = orders.count { it.status == OrderStatus.COMPLETED.name }

        val dailyIncomes = listOf(
            DailyIncome("Senin", 450000.0),
            DailyIncome("Selasa", 620000.0),
            DailyIncome("Rabu", 380000.0),
            DailyIncome("Kamis", 890000.0),
            DailyIncome("Jumat", 750000.0),
            DailyIncome("Sabtu", 1200000.0),
            DailyIncome("Minggu", 950000.0)
        )

        val summary = ReportSummary(
            totalRevenue = totalRevenue,
            totalTransactions = totalTransactions,
            totalCustomers = totalCustomers,
            totalCashiers = totalCashiers,
            totalVehicles = totalVehicles,
            pendingOrdersCount = pending,
            inProgressOrdersCount = inProgress,
            completedOrdersCount = completed,
            dailyIncomes = dailyIncomes,
            topServices = listOf("Servis Ringan" to 24, "Ganti Oli Mesin" to 38, "Servis CVT" to 15),
            topSpareParts = listOf("Oli MPX2" to 35, "Busi NGK" to 20, "Kampas Rem" to 12)
        )

        emit(summary)
    }.flowOn(Dispatchers.IO)
}
