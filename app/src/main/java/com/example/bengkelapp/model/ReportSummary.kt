package com.example.bengkelapp.model

data class DailyIncome(
    val day: String,
    val amount: Double
)

data class ReportSummary(
    val totalRevenue: Double = 0.0,
    val totalTransactions: Int = 0,
    val totalCustomers: Int = 0,
    val totalCashiers: Int = 0,
    val totalVehicles: Int = 0,
    val pendingOrdersCount: Int = 0,
    val inProgressOrdersCount: Int = 0,
    val completedOrdersCount: Int = 0,
    val dailyIncomes: List<DailyIncome> = emptyList(),
    val topServices: List<Pair<String, Int>> = emptyList(),
    val topSpareParts: List<Pair<String, Int>> = emptyList()
)
