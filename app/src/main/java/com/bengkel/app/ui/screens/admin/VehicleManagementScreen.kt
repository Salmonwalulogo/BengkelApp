package com.bengkel.app.ui.screens.admin

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
import com.bengkel.app.model.Vehicle
import com.bengkel.app.ui.theme.AccentOrange
import com.bengkel.app.ui.theme.AppBackground
import com.bengkel.app.ui.theme.NavyBlue
import com.bengkel.app.ui.theme.TextSecondary
import com.bengkel.app.viewmodel.AdminViewModel

@Composable
fun VehicleManagementScreen(
    viewModel: AdminViewModel
) {
    val vehicles by viewModel.vehicles.collectAsState()
    val customers by viewModel.customers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedVehicle by remember { mutableStateOf<Vehicle?>(null) }

    val filteredVehicles = vehicles.filter {
        it.licensePlate.contains(searchQuery, ignoreCase = true) ||
                it.customerName.contains(searchQuery, ignoreCase = true) ||
                it.brand.contains(searchQuery, ignoreCase = true) ||
                it.type.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedVehicle = null
                    showAddDialog = true
                },
                containerColor = AccentOrange,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.DirectionsCar, contentDescription = "Tambah Kendaraan")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackground)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Manajemen Kendaraan",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )
                Text(
                    text = "Daftar unit motor/mobil pelanggan dan detail spesifikasinya",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari no polisi, pemilik, atau merek...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            if (filteredVehicles.isEmpty()) {
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
                            Text("Tidak ada kendaraan ditemukan.", color = TextSecondary)
                        }
                    }
                }
            } else {
                items(filteredVehicles) { vehicle ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(vehicle.licensePlate, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NavyBlue)
                                Text("${vehicle.brand} ${vehicle.type} (${vehicle.year})", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text("Pemilik: ${vehicle.customerName} â€¢ Warna: ${vehicle.color}", fontSize = 12.sp, color = TextSecondary)
                                if (vehicle.frameNumber.isNotBlank()) {
                                    Text("No. Rangka: ${vehicle.frameNumber}", fontSize = 11.sp, color = TextSecondary)
                                }
                            }

                            Row {
                                IconButton(onClick = {
                                    selectedVehicle = vehicle
                                    showAddDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = NavyBlue)
                                }
                                IconButton(onClick = {
                                    viewModel.deleteVehicle(vehicle.id)
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var ownerName by remember { mutableStateOf(selectedVehicle?.customerName ?: "") }
        var plate by remember { mutableStateOf(selectedVehicle?.licensePlate ?: "") }
        var brand by remember { mutableStateOf(selectedVehicle?.brand ?: "") }
        var type by remember { mutableStateOf(selectedVehicle?.type ?: "") }
        var year by remember { mutableStateOf(selectedVehicle?.year ?: "2022") }
        var color by remember { mutableStateOf(selectedVehicle?.color ?: "") }
        var frameNum by remember { mutableStateOf(selectedVehicle?.frameNumber ?: "") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(if (selectedVehicle == null) "Tambah Kendaraan" else "Edit Kendaraan") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = ownerName,
                        onValueChange = { ownerName = it },
                        label = { Text("Nama Pemilik") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = plate,
                        onValueChange = { plate = it },
                        label = { Text("Nomor Polisi (Plat)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = brand,
                            onValueChange = { brand = it },
                            label = { Text("Merek") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = type,
                            onValueChange = { type = it },
                            label = { Text("Tipe/Model") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = year,
                            onValueChange = { year = it },
                            label = { Text("Tahun") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = color,
                            onValueChange = { color = it },
                            label = { Text("Warna") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = frameNum,
                        onValueChange = { frameNum = it },
                        label = { Text("Nomor Rangka (Opsional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (plate.isNotBlank() && brand.isNotBlank()) {
                            val v = Vehicle(
                                id = selectedVehicle?.id ?: "",
                                userId = selectedVehicle?.userId ?: "usr_cust1",
                                customerName = ownerName.ifEmpty { "Pelanggan" },
                                licensePlate = plate,
                                brand = brand,
                                type = type,
                                year = year,
                                color = color,
                                frameNumber = frameNum
                            )
                            viewModel.saveVehicle(v)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

