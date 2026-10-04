package com.example.bengkelapp.data.remote

import com.example.bengkelapp.model.*
import retrofit2.Response
import retrofit2.http.*

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val phone: String, val password: String, val passwordConfirmation: String)
data class AuthResponse(val success: Boolean, val message: String?, val token: String?, val user: User?)
data class ApiResponse<T>(val success: Boolean, val message: String?, val data: T?)

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    @GET("auth/user")
    suspend fun getCurrentUser(): Response<ApiResponse<User>>

    @GET("services")
    suspend fun getServices(): Response<ApiResponse<List<ServiceItem>>>

    @POST("services")
    suspend fun createService(@Body service: ServiceItem): Response<ApiResponse<ServiceItem>>

    @GET("vehicles")
    suspend fun getVehicles(): Response<ApiResponse<List<Vehicle>>>

    @POST("vehicles")
    suspend fun createVehicle(@Body vehicle: Vehicle): Response<ApiResponse<Vehicle>>

    @GET("orders")
    suspend fun getOrders(): Response<ApiResponse<List<ServiceOrder>>>

    @POST("orders")
    suspend fun createOrder(@Body order: ServiceOrder): Response<ApiResponse<ServiceOrder>>

    @PUT("orders/{id}/status")
    suspend fun updateOrderStatus(@Path("id") orderId: String, @Query("status") status: String): Response<ApiResponse<ServiceOrder>>

    @GET("spare-parts")
    suspend fun getSpareParts(): Response<ApiResponse<List<SparePart>>>

    @POST("spare-parts")
    suspend fun createSparePart(@Body part: SparePart): Response<ApiResponse<SparePart>>

    @GET("invoices")
    suspend fun getInvoices(): Response<ApiResponse<List<Invoice>>>

    @POST("invoices")
    suspend fun createInvoice(@Body invoice: Invoice): Response<ApiResponse<Invoice>>

    @GET("settings")
    suspend fun getSettings(): Response<ApiResponse<WorkshopSettings>>

    @POST("settings")
    suspend fun updateSettings(@Body settings: WorkshopSettings): Response<ApiResponse<WorkshopSettings>>
}
