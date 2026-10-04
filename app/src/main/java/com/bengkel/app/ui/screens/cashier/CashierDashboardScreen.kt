package com.bengkel.app.ui.screens.cashier

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
import com.bengkel.app.model.OrderStatus
import com.bengkel.app.model.ServiceOrder
import com.bengkel.app.ui.components.OrderStatusChip
import com.bengkel.app.ui.components.StatCard
import com.bengkel.app.ui.theme.*
import com.bengkel.app.viewmodel.CashierViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CashierDashboardScreen(
    viewModel: CashierViewModel,
    onNavigateToDirectReg: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToPayment: (String) -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val orders by viewModel.orders.collectAsState()
    val invoices by viewModel.invoices.collectAsState()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    val todayTotalIncome = invoices.sumOf { it.totalAmount }
    val todayTxCount = invoices.size
    val unpaidOrdersCount = orders.count { it.status == OrderStatus.WAITING_PAYMENT }
    val inProgressCount = orders.count { it.status == OrderStatus.IN_PROGRESS || it.status == OrderStatus.WAITING_WORK }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Dashboard Kasir",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )
                Text(
                    text = "Administrasi pembayaran, pendaftaran langsung, dan cetak struk",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // Quick Direct Service Action Button
        item {
            Button(
                onClick = onNavigateToDirectReg,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
            ) {
                Icon(Icons.Default.AddShoppingCart, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pendaftaran Servis Langsung (Walk-in)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Today's Stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Pendapatan Hari Ini",
                    value = currencyFormat.format(todayTotalIncome),
                    icon = Icons.Default.MonetizationOn,
                    iconBgColor = StatusSuccess,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Transaksi Lunas",
                    value = "$todayTxCount Tx",
                    icon = Icons.Default.ReceiptLong,
                    iconBgColor = NavyBlue,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Belum Dibayar",
                    value = "$unpaidOrdersCount Tagihan",
                    icon = Icons.Default.PendingActions,
                    iconBgColor = StatusPending,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Servis Diproses",
                    value = "$inProgressCount Unit",
                    icon = Icons.Default.Build,
                    iconBgColor = StatusInProcess,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Unpaid Orders Ready For Billing
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pesanan Siap Dibayar / Pembayaran",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )
                TextButton(onClick = onNavigateToOrders) {
                    Text(text = "Lihat Semua", color = AccentOrange, fontSize = 13.sp)
                }
            }
        }

        val unpaidOrders = orders.filter { it.status == OrderStatus.WAITING_PAYMENT || it.status == OrderStatus.IN_PROGRESS }

        if (unpaidOrders.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Tidak ada tagihan yang menunggu pembayaran.", color = TextSecondary, fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(unpaidOrders) { order ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(order.orderNumber, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyBlue)
                            Text("${order.customerName} â€¢ ${order.vehiclePlate}", fontSize = 13.sp, color = TextPrimary)
                            Text(order.serviceNames.joinToString(", "), fontSize = 11.sp, color = TextSecondary)
                        }

                        Button(
                            onClick = { onNavigateToPayment(order.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Bayar", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Recent Completed Transactions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transaksi Pembayaran Terbaru",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )
                TextButton(onClick = onNavigateToHistory) {
                    Text(text = "Riwayat", color = AccentOrange, fontSize = 13.sp)
                }
            }
        }

        items(invoices.take(5)) { inv ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(inv.invoiceNumber, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyBlue)
                        Text("${inv.customerName} â€¢ ${inv.vehiclePlate}", fontSize = 12.sp, color = TextSecondary)
                    }
                    Text(
                        currencyFormat.format(inv.totalAmount),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = StatusSuccess
                    )
                }
            }
        }
    }
}

