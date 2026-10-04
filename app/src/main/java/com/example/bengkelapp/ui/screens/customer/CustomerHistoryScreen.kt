package com.example.bengkelapp.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.bengkelapp.ui.theme.*
import com.example.bengkelapp.viewmodel.CustomerViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CustomerHistoryScreen(
    viewModel: CustomerViewModel
) {
    val myOrders by viewModel.myOrders.collectAsState()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    var selectedOrderForDetail by remember { mutableStateOf<ServiceOrder?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Aktif, 1: Selesai / Riwayat

    val filteredOrders = if (selectedTab == 0) {
        myOrders.filter { it.status != OrderStatus.COMPLETED && it.status != OrderStatus.CANCELLED }
    } else {
        myOrders.filter { it.status == OrderStatus.COMPLETED || it.status == OrderStatus.CANCELLED }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Riwayat & Status Pesanan Servis", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = NavyBlue)

        TabRow(selectedTabIndex = selectedTab, containerColor = Color.White) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("Servis Aktif (${myOrders.count { it.status != OrderStatus.COMPLETED && it.status != OrderStatus.CANCELLED }})", modifier = Modifier.padding(vertical = 12.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("Riwayat Selesai", modifier = Modifier.padding(vertical = 12.dp), fontWeight = FontWeight.Bold)
            }
        }

        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("Tidak ada pesanan servis dalam kategori ini.", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredOrders) { order ->
                    Card(
                        onClick = { selectedOrderForDetail = order },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(order.orderNumber, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyBlue)
                                OrderStatusChip(status = order.status)
                            }

                            Text("${order.vehicleModel} • ${order.vehiclePlate}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Layanan: ${order.serviceNames.joinToString(", ")}", fontSize = 12.sp, color = TextSecondary)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Jadwal: ${order.bookingDate} (${order.bookingTime})", fontSize = 11.sp, color = TextSecondary)
                                Text(currencyFormat.format(order.finalCost), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AccentOrange)
                            }
                        }
                    }
                }
            }
        }
    }

    // Detail Dialog
    if (selectedOrderForDetail != null) {
        val detail = selectedOrderForDetail!!

        AlertDialog(
            onDismissRequest = { selectedOrderForDetail = null },
            title = { Text("Detail Pesanan #${detail.orderNumber}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Kendaraan : ${detail.vehicleModel} (${detail.vehiclePlate})", fontSize = 13.sp)
                    Text("Pelanggan : ${detail.customerName} (${detail.customerPhone})", fontSize = 13.sp)
                    Text("Keluhan   : ${detail.complaint.ifBlank { "-" }}", fontSize = 13.sp, color = TextSecondary)
                    Text("Layanan   : ${detail.serviceNames.joinToString(", ")}", fontSize = 13.sp)
                    Text("Catatan Bengkel: ${detail.notes.ifBlank { "-" }}", fontSize = 13.sp, color = AccentOrange)
                    Divider()
                    Text("Biaya Estimasi: ${currencyFormat.format(detail.estimatedCost)}", fontSize = 13.sp)
                    Text("Biaya Final/Akhir: ${currencyFormat.format(detail.finalCost)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyBlue)
                }
            },
            confirmButton = {
                Button(onClick = { selectedOrderForDetail = null }) {
                    Text("Tutup")
                }
            }
        )
    }
}
