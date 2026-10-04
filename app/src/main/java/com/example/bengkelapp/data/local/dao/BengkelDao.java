package com.example.bengkelapp.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.bengkelapp.data.local.entities.InvoiceEntity;
import com.example.bengkelapp.data.local.entities.NotificationEntity;
import com.example.bengkelapp.data.local.entities.ServiceItemEntity;
import com.example.bengkelapp.data.local.entities.ServiceOrderEntity;
import com.example.bengkelapp.data.local.entities.SparePartEntity;
import com.example.bengkelapp.data.local.entities.UserEntity;
import com.example.bengkelapp.data.local.entities.VehicleEntity;
import com.example.bengkelapp.data.local.entities.WorkshopSettingsEntity;

import java.util.List;

@Dao
public interface BengkelDao {

    // Users
    @Query("SELECT * FROM users")
    List<UserEntity> getAllUsers();

    @Query("SELECT * FROM users WHERE role = :role")
    List<UserEntity> getUsersByRole(String role);

    @Query("SELECT * FROM users WHERE id = :id")
    UserEntity getUserById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(UserEntity user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUsers(List<UserEntity> users);

    @Delete
    void deleteUser(UserEntity user);

    // Vehicles
    @Query("SELECT * FROM vehicles")
    List<VehicleEntity> getAllVehicles();

    @Query("SELECT * FROM vehicles WHERE userId = :userId")
    List<VehicleEntity> getVehiclesByUserId(String userId);

    @Query("SELECT * FROM vehicles WHERE id = :id")
    VehicleEntity getVehicleById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVehicle(VehicleEntity vehicle);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVehicles(List<VehicleEntity> vehicles);

    @Delete
    void deleteVehicle(VehicleEntity vehicle);

    // Services
    @Query("SELECT * FROM services")
    List<ServiceItemEntity> getAllServices();

    @Query("SELECT * FROM services WHERE isActive = 1")
    List<ServiceItemEntity> getActiveServices();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertService(ServiceItemEntity service);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertServices(List<ServiceItemEntity> services);

    @Delete
    void deleteService(ServiceItemEntity service);

    // Spare Parts
    @Query("SELECT * FROM spare_parts")
    List<SparePartEntity> getAllSpareParts();

    @Query("SELECT * FROM spare_parts WHERE stock <= minStock")
    List<SparePartEntity> getLowStockSpareParts();

    @Query("SELECT * FROM spare_parts WHERE id = :id")
    SparePartEntity getSparePartById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSparePart(SparePartEntity part);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSpareParts(List<SparePartEntity> parts);

    @Delete
    void deleteSparePart(SparePartEntity part);

    // Service Orders
    @Query("SELECT * FROM service_orders ORDER BY createdAt DESC")
    List<ServiceOrderEntity> getAllOrders();

    @Query("SELECT * FROM service_orders WHERE customerId = :customerId ORDER BY createdAt DESC")
    List<ServiceOrderEntity> getOrdersByCustomer(String customerId);

    @Query("SELECT * FROM service_orders WHERE id = :id")
    ServiceOrderEntity getOrderById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrder(ServiceOrderEntity order);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrders(List<ServiceOrderEntity> orders);

    // Invoices / Transactions
    @Query("SELECT * FROM invoices ORDER BY createdAt DESC")
    List<InvoiceEntity> getAllInvoices();

    @Query("SELECT * FROM invoices WHERE orderId = :orderId")
    InvoiceEntity getInvoiceByOrderId(String orderId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertInvoice(InvoiceEntity invoice);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertInvoices(List<InvoiceEntity> invoices);

    // Notifications
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    List<NotificationEntity> getNotificationsByUser(String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotification(NotificationEntity notification);

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    void markNotificationAsRead(String id);

    // Workshop Settings
    @Query("SELECT * FROM workshop_settings WHERE id = 1")
    WorkshopSettingsEntity getWorkshopSettings();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveWorkshopSettings(WorkshopSettingsEntity settings);
}
