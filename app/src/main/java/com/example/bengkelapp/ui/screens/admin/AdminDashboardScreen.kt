package com.example.bengkelapp.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bengkelapp.model.OrderStatus
import com.example.bengkelapp.model.ServiceOrder
import com.example.bengkelapp.ui.components.OrderStatusChip
import com.example.bengkelapp.ui.components.RevenueChartCard
import com.example.bengkelapp.ui.components.StatCard
import com.example.bengkelapp.ui.theme.*
import com.example.bengkelapp.viewmodel.AdminViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel,
    onNavigateToCashiers: () -> Unit,
    onNavigateToCustomers: () -> Unit,
    onNavigateToVehicles: () -> Unit,
    onNavigateToServices: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToSpareParts: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val summary by viewModel.reportSummary.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header Title
        item {
            Column {
                Text(
                    text = "Dashboard Pengelola",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )
                Text(
                    text = "Ringkasan performa dan manajemen operasional bengkel",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // Row 1 Stat Cards: Revenue & Transactions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Total Pendapatan",
                    value = currencyFormat.format(summary.totalRevenue),
                    icon = Icons.Default.MonetizationOn,
                    iconBgColor = StatusSuccess,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Total Transaksi",
                    value = "${summary.totalTransactions} Tx",
                    icon = Icons.Default.ReceiptLong,
                    iconBgColor = NavyBlue,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Row 2 Stat Cards: Customers, Cashiers, Vehicles
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Pelanggan",
                    value = "${summary.totalCustomers} Orang",
                    icon = Icons.Default.People,
                    iconBgColor = AccentOrange,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Kasir",
                    value = "${summary.totalCashiers} Akun",
                    icon = Icons.Default.Badge,
                    iconBgColor = StatusConfirmed,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Kendaraan",
                    value = "${summary.totalVehicles} Unit",
                    icon = Icons.Default.DirectionsCar,
                    iconBgColor = StatusInProcess,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Row 3 Stat Cards: Order Status Summary
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Pesanan Baru",
                    value = "${summary.pendingOrdersCount}",
                    icon = Icons.Default.HourglassTop,
                    iconBgColor = StatusPending,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Dalam Pengerjaan",
                    value = "${summary.inProgressOrdersCount}",
                    icon = Icons.Default.Build,
                    iconBgColor = StatusInProcess,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Servis Selesai",
                    value = "${summary.completedOrdersCount}",
                    icon = Icons.Default.CheckCircle,
                    iconBgColor = StatusSuccess,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Revenue Weekly Chart
        item {
            RevenueChartCard(dailyIncomes = summary.dailyIncomes)
        }

        // Quick Navigation Menu Grid
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Menu Manajemen Utama",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        AdminMenuIcon(title = "Kasir", icon = Icons.Default.Badge, onClick = onNavigateToCashiers)
                        AdminMenuIcon(title = "Pelanggan", icon = Icons.Default.People, onClick = onNavigateToCustomers)
                        AdminMenuIcon(title = "Kendaraan", icon = Icons.Default.DirectionsCar, onClick = onNavigateToVehicles)
                        AdminMenuIcon(title = "Layanan", icon = Icons.Default.Build, onClick = onNavigateToServices)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        AdminMenuIcon(title = "Pesanan", icon = Icons.Default.Assignment, onClick = onNavigateToOrders)
                        AdminMenuIcon(title = "Stok", icon = Icons.Default.Inventory, onClick = onNavigateToSpareParts)
                        AdminMenuIcon(title = "Laporan", icon = Icons.Default.BarChart, onClick = onNavigateToReports)
                        AdminMenuIcon(title = "Pengaturan", icon = Icons.Default.Settings, onClick = onNavigateToSettings)
                    }
                }
            }
        }

        // Recent Orders Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pesanan & Servis Terbaru",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )
                TextButton(onClick = onNavigateToOrders) {
                    Text(text = "Lihat Semua", color = AccentOrange, fontSize = 13.sp)
                }
            }
        }

        // Recent Orders List
        items(orders.take(5)) { order ->
            RecentOrderCard(order = order, onSelect = onNavigateToOrders)
        }
    }
}

@Composable
fun AdminMenuIcon(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        IconButton(
            onClick = onClick,
            colors = IconButtonDefaults.iconButtonColors(containerColor = NavyBlue.copy(alpha = 0.08f)),
            modifier = Modifier.size(50.dp)
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = NavyBlue)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
    }
}

@Composable
fun RecentOrderCard(
    order: ServiceOrder,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onSelect
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = order.orderNumber,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )
                Text(
                    text = "${order.customerName} • ${order.vehiclePlate} (${order.vehicleModel})",
                    fontSize = 12.sp,
                    color = TextPrimary
                )
                Text(
                    text = order.serviceNames.joinToString(", "),
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
            OrderStatusChip(status = order.status)
        }
    }
}
