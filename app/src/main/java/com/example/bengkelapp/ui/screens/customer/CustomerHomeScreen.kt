package com.example.bengkelapp.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bengkelapp.model.OrderStatus
import com.example.bengkelapp.model.User
import com.example.bengkelapp.ui.components.OrderStatusChip
import com.example.bengkelapp.ui.theme.*
import com.example.bengkelapp.viewmodel.CustomerViewModel

@Composable
fun CustomerHomeScreen(
    currentUser: User?,
    viewModel: CustomerViewModel,
    onNavigateToBookService: () -> Unit,
    onNavigateToServices: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val myVehicles by viewModel.myVehicles.collectAsState()
    val myOrders by viewModel.myOrders.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    val activeOrder = myOrders.find {
        it.status != OrderStatus.COMPLETED && it.status != OrderStatus.CANCELLED
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyBlue)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Selamat Datang,",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = currentUser?.name ?: "Pelanggan Setia",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Servis kendaraan Anda secara berkala untuk performa terbaik.",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Active Order Progress Banner (if exists)
        if (activeOrder != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Status Servis Aktif",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = NavyBlue
                            )
                            OrderStatusChip(status = activeOrder.status)
                        }

                        Divider()

                        Text(
                            text = "${activeOrder.vehicleModel} (${activeOrder.vehiclePlate})",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )

                        Text(
                            text = "Layanan: ${activeOrder.serviceNames.joinToString(", ")}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        if (activeOrder.notes.isNotBlank()) {
                            Text(
                                text = "Catatan Mekanik: ${activeOrder.notes}",
                                fontSize = 12.sp,
                                color = AccentOrange
                            )
                        }

                        Button(
                            onClick = onNavigateToHistory,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
                        ) {
                            Text("Lihat Detail Servis")
                        }
                    }
                }
            }
        }

        // Quick Action Grid
        item {
            Text(
                text = "Layanan Cepat",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = NavyBlue
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    onClick = onNavigateToBookService,
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(AccentOrange.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = null, tint = AccentOrange)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Booking Servis", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyBlue)
                    }
                }

                Card(
                    onClick = onNavigateToServices,
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(NavyBlue.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Build, contentDescription = null, tint = NavyBlue)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Daftar Jasa", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyBlue)
                    }
                }
            }
        }

        // My Registered Vehicles
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kendaraan Saya (${myVehicles.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )
                TextButton(onClick = onNavigateToBookService) {
                    Text("+ Tambah", color = AccentOrange, fontSize = 13.sp)
                }
            }
        }

        if (myVehicles.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("Belum ada kendaraan terdaftar. Daftarkan kendaraan Anda saat memesan servis.", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        } else {
            items(myVehicles) { vehicle ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.TwoWheeler, contentDescription = null, tint = NavyBlue, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("${vehicle.brand} ${vehicle.type}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${vehicle.licensePlate} • ${vehicle.color} (${vehicle.year})", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}
