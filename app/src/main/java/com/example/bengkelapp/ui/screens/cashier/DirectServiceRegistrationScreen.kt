package com.example.bengkelapp.ui.screens.cashier

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.bengkelapp.ui.theme.AccentOrange
import com.example.bengkelapp.ui.theme.AppBackground
import com.example.bengkelapp.ui.theme.NavyBlue
import com.example.bengkelapp.ui.theme.TextSecondary
import com.example.bengkelapp.viewmodel.CashierViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DirectServiceRegistrationScreen(
    viewModel: CashierViewModel,
    onNavigateBack: () -> Unit
) {
    val services by viewModel.services.collectAsState()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var licensePlate by remember { mutableStateOf("") }
    var vehicleModel by remember { mutableStateOf("") }
    var complaint by remember { mutableStateOf("") }

    val selectedServices = remember { mutableStateListOf<ServiceItem>() }

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
            Text("Pendaftaran Servis Langsung", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyBlue)
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Informasi Pelanggan & Kendaraan", fontWeight = FontWeight.Bold, color = NavyBlue)

                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Nama Pelanggan") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = { customerPhone = it },
                    label = { Text("Nomor Telepon / WhatsApp") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = licensePlate,
                        onValueChange = { licensePlate = it },
                        label = { Text("Nomor Polisi (Plat)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = vehicleModel,
                        onValueChange = { vehicleModel = it },
                        label = { Text("Merek / Model Motor") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = complaint,
                    onValueChange = { complaint = it },
                    label = { Text("Keluhan / Catatan Gejala Kendaraan") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        }

        // Services Selection Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Pilih Layanan Servis", fontWeight = FontWeight.Bold, color = NavyBlue)

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

                Spacer(modifier = Modifier.height(8.dp))

                val totalEst = selectedServices.sumOf { it.price }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Estimasi Biaya Jasa:", fontSize = 13.sp, color = TextSecondary)
                    Text(currencyFormat.format(totalEst), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyBlue)
                }
            }
        }

        Button(
            onClick = {
                if (customerName.isNotBlank() && licensePlate.isNotBlank() && selectedServices.isNotEmpty()) {
                    viewModel.registerDirectService(
                        customerName = customerName,
                        customerPhone = customerPhone,
                        licensePlate = licensePlate,
                        vehicleModel = vehicleModel,
                        selectedServices = selectedServices,
                        complaint = complaint
                    )
                    onNavigateBack()
                }
            },
            enabled = customerName.isNotBlank() && licensePlate.isNotBlank() && selectedServices.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
        ) {
            Text("Daftarkan & Masukkan Ke Antrean Servis", fontWeight = FontWeight.Bold)
        }
    }
}
