package com.example.bengkelapp.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bengkelapp.model.ServiceItem
import com.example.bengkelapp.model.Vehicle
import com.example.bengkelapp.ui.theme.*
import com.example.bengkelapp.viewmodel.CustomerViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookServiceScreen(
    viewModel: CustomerViewModel,
    onNavigateBack: () -> Unit,
    onBookingSuccess: () -> Unit
) {
    val myVehicles by viewModel.myVehicles.collectAsState()
    val services by viewModel.availableServices.collectAsState()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    var selectedVehicle by remember { mutableStateOf<Vehicle?>(myVehicles.firstOrNull()) }
    val selectedServices = remember { mutableStateListOf<ServiceItem>() }

    var bookingDate by remember { mutableStateOf("2025-03-01") }
    var bookingTime by remember { mutableStateOf("09:00 WIB") }
    var complaint by remember { mutableStateOf("") }

    var showAddVehicleDialog by remember { mutableStateOf(false) }

    LaunchedEffect(myVehicles) {
        if (selectedVehicle == null && myVehicles.isNotEmpty()) {
            selectedVehicle = myVehicles.first()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
            }
            Text("Booking Servis Online", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyBlue)
        }

        // Vehicle Selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
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
                    Text("1. Pilih Kendaraan", fontWeight = FontWeight.Bold, color = NavyBlue)
                    TextButton(onClick = { showAddVehicleDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("Tambah Motor Baru", fontSize = 12.sp, color = AccentOrange)
                    }
                }

                if (myVehicles.isEmpty()) {
                    Text("Belum ada kendaraan terdaftar. Silakan tambahkan motor Anda.", fontSize = 12.sp, color = TextSecondary)
                } else {
                    myVehicles.forEach { veh ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("${veh.brand} ${veh.type}", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                Text(veh.licensePlate, fontSize = 12.sp, color = TextSecondary)
                            }
                            RadioButton(
                                selected = selectedVehicle?.id == veh.id,
                                onClick = { selectedVehicle = veh }
                            )
                        }
                    }
                }
            }
        }

        // Service Selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("2. Pilih Layanan Jasa Servis", fontWeight = FontWeight.Bold, color = NavyBlue)

                services.forEach { service ->
                    val isChecked = selectedServices.contains(service)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(service.name, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Text(currencyFormat.format(service.price), fontSize = 12.sp, color = AccentOrange, fontWeight = FontWeight.Bold)
                        }

                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                if (checked) selectedServices.add(service) else selectedServices.remove(service)
                            }
                        )
                    }
                    Divider()
                }
            }
        }

        // Booking Date & Complaints
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("3. Jadwal & Catatan Keluhan", fontWeight = FontWeight.Bold, color = NavyBlue)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = bookingDate,
                        onValueChange = { bookingDate = it },
                        label = { Text("Tanggal Servis") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = bookingTime,
                        onValueChange = { bookingTime = it },
                        label = { Text("Jam Kedatangan") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = complaint,
                    onValueChange = { complaint = it },
                    label = { Text("Keluhan Kendaraan / Catatan Tambahan") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        }

        // Total Cost Summary
        val totalEst = selectedServices.sumOf { it.price }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NavyBlue)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Estimasi Biaya Jasa:", color = Color.White, fontSize = 14.sp)
                Text(currencyFormat.format(totalEst), color = AccentOrange, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Button(
            onClick = {
                val vehicle = selectedVehicle
                if (vehicle != null && selectedServices.isNotEmpty()) {
                    viewModel.bookOnlineService(
                        vehicle = vehicle,
                        selectedServices = selectedServices,
                        bookingDate = bookingDate,
                        bookingTime = bookingTime,
                        complaint = complaint,
                        damagePhotoUrl = null
                    )
                    onBookingSuccess()
                }
            },
            enabled = selectedVehicle != null && selectedServices.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
        ) {
            Text("Kirim Pesanan Booking Servis", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }

    // Add Vehicle Dialog
    if (showAddVehicleDialog) {
        var plate by remember { mutableStateOf("") }
        var brand by remember { mutableStateOf("Honda") }
        var type by remember { mutableStateOf("") }
        var year by remember { mutableStateOf("2022") }
        var color by remember { mutableStateOf("Hitam") }

        AlertDialog(
            onDismissRequest = { showAddVehicleDialog = false },
            title = { Text("Tambah Kendaraan Baru") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = plate, onValueChange = { plate = it }, label = { Text("Plat Nomor (B 1234 ABC)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = brand, onValueChange = { brand = it }, label = { Text("Merek (Honda, Yamaha, dll)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Tipe / Model (Vario 125)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = year, onValueChange = { year = it }, label = { Text("Tahun Kendaraan") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Warna Kendaraan") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (plate.isNotBlank() && type.isNotBlank()) {
                            viewModel.saveVehicle(plate, brand, type, year, color, "")
                            showAddVehicleDialog = false
                        }
                    }
                ) {
                    Text("Simpan Motor")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddVehicleDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
