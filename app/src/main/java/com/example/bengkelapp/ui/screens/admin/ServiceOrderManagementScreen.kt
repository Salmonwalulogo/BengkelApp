package com.example.bengkelapp.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
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
import com.example.bengkelapp.ui.theme.AccentOrange
import com.example.bengkelapp.ui.theme.AppBackground
import com.example.bengkelapp.ui.theme.NavyBlue
import com.example.bengkelapp.ui.theme.TextSecondary
import com.example.bengkelapp.viewmodel.AdminViewModel

@Composable
fun ServiceOrderManagementScreen(
    viewModel: AdminViewModel
) {
    val orders by viewModel.orders.collectAsState()
    var selectedFilter by remember { mutableStateOf<OrderStatus?>(null) }
    var selectedOrderForUpdate by remember { mutableStateOf<ServiceOrder?>(null) }

    val filteredOrders = if (selectedFilter == null) orders else orders.filter { it.status == selectedFilter }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Manajemen Pesanan Servis",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = NavyBlue
            )
            Text(
                text = "Konfirmasi pemesanan online, atur pengerjaan, dan perbarui status",
                fontSize = 12.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Filter Chips
            ScrollableTabRow(
                selectedTabIndex = if (selectedFilter == null) 0 else OrderStatus.values().indexOf(selectedFilter) + 1,
                edgePadding = 0.dp,
                containerColor = Color.Transparent,
                divider = {}
            ) {
                Tab(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    text = { Text("Semua (${orders.size})") }
                )
                OrderStatus.values().forEach { st ->
                    val count = orders.count { it.status == st }
                    Tab(
                        selected = selectedFilter == st,
                        onClick = { selectedFilter = st },
                        text = { Text("${st.displayName} ($count)") }
                    )
                }
            }
        }

        if (filteredOrders.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Tidak ada pesanan servis untuk filter ini.", color = TextSecondary)
                    }
                }
            }
        } else {
            items(filteredOrders) { order ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(order.orderNumber, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyBlue)
                            OrderStatusChip(status = order.status)
                        }

                        Text("Pelanggan: ${order.customerName} (${order.customerPhone})", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("Kendaraan: ${order.vehiclePlate} • ${order.vehicleModel}", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = NavyBlue)
                        Text("Jadwal: ${order.bookingDate} (${order.bookingTime})", fontSize = 12.sp, color = TextSecondary)
                        Text("Layanan: ${order.serviceNames.joinToString(", ")}", fontSize = 12.sp, color = AccentOrange)

                        if (order.complaint.isNotBlank()) {
                            Text("Keluhan: ${order.complaint}", fontSize = 12.sp, color = TextSecondary)
                        }

                        Divider(modifier = Modifier.padding(vertical = 4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { selectedOrderForUpdate = order },
                                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Ubah Status / Catatan", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedOrderForUpdate != null) {
        val order = selectedOrderForUpdate!!
        var nextStatus by remember { mutableStateOf(order.status) }
        var notes by remember { mutableStateOf(order.notes) }

        AlertDialog(
            onDismissRequest = { selectedOrderForUpdate = null },
            title = { Text("Pembaruan Status #${order.orderNumber}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Pilih Status Pengerjaan Terbaru:", fontSize = 13.sp, fontWeight = FontWeight.Bold)

                    OrderStatus.values().forEach { st ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = nextStatus == st,
                                onClick = { nextStatus = st }
                            )
                            Text(st.displayName, fontSize = 13.sp)
                        }
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Catatan Mekanik / Bengkel") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateOrderStatus(order.id, nextStatus, notes)
                        selectedOrderForUpdate = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
                ) {
                    Text("Simpan Status")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedOrderForUpdate = null }) {
                    Text("Batal")
                }
            }
        )
    }
}
