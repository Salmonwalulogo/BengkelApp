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
import com.bengkel.app.model.ServiceItem
import com.bengkel.app.ui.theme.AccentOrange
import com.bengkel.app.ui.theme.AppBackground
import com.bengkel.app.ui.theme.NavyBlue
import com.bengkel.app.ui.theme.TextSecondary
import com.bengkel.app.viewmodel.AdminViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ServiceManagementScreen(
    viewModel: AdminViewModel
) {
    val services by viewModel.services.collectAsState()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedService by remember { mutableStateOf<ServiceItem?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedService = null
                    showAddDialog = true
                },
                containerColor = AccentOrange,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Layanan")
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
                    text = "Katalog Layanan Bengkel",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )
                Text(
                    text = "Atur jenis jasa servis, deskripsi, harga, dan estimasi pengerjaan",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(services) { item ->
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NavyBlue)
                                Spacer(modifier = Modifier.width(8.dp))
                                if (!item.isActive) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            "Non-Aktif",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(item.description, fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${currencyFormat.format(item.price)} â€¢ Estimasi: ${item.estimatedMinutes} Menit",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentOrange
                            )
                        }

                        Row {
                            IconButton(onClick = {
                                selectedService = item
                                showAddDialog = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = NavyBlue)
                            }
                            IconButton(onClick = {
                                viewModel.deleteService(item.id)
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf(selectedService?.name ?: "") }
        var description by remember { mutableStateOf(selectedService?.description ?: "") }
        var priceStr by remember { mutableStateOf(selectedService?.price?.toInt()?.toString() ?: "") }
        var durationStr by remember { mutableStateOf(selectedService?.estimatedMinutes?.toString() ?: "30") }
        var isActive by remember { mutableStateOf(selectedService?.isActive ?: true) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(if (selectedService == null) "Tambah Layanan" else "Edit Layanan") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Layanan Servis") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Deskripsi Layanan") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Harga / Tarif (Rp)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = durationStr,
                        onValueChange = { durationStr = it },
                        label = { Text("Estimasi Waktu (Menit)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Status Aktif")
                        Switch(checked = isActive, onCheckedChange = { isActive = it })
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p = priceStr.toDoubleOrNull() ?: 0.0
                        val d = durationStr.toIntOrNull() ?: 30
                        if (name.isNotBlank()) {
                            val service = ServiceItem(
                                id = selectedService?.id ?: "",
                                name = name,
                                description = description,
                                price = p,
                                estimatedMinutes = d,
                                isActive = isActive
                            )
                            viewModel.saveService(service)
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

