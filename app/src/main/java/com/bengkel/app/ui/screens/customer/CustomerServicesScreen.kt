package com.bengkel.app.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bengkel.app.ui.theme.*
import com.bengkel.app.viewmodel.CustomerViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CustomerServicesScreen(
    viewModel: CustomerViewModel,
    onNavigateToBookService: () -> Unit
) {
    val services by viewModel.availableServices.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    val filteredServices = services.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text("Daftar Layanan & Jasa Servis", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = NavyBlue)
                Text("Pilih paket perbaikan atau perawatan berkala untuk kendaraan Anda", fontSize = 12.sp, color = TextSecondary)
            }
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari layanan (Servis, Ganti Oli, CVT...)") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        items(filteredServices) { service ->
            Card(
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Build, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(service.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NavyBlue)
                        }
                        Text(currencyFormat.format(service.price), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = AccentOrange)
                    }

                    Text(service.description, fontSize = 13.sp, color = TextSecondary)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Estimasi Durasi: ${service.estimatedMinutes} Menit", fontSize = 12.sp, color = TextSecondary)

                        Button(
                            onClick = onNavigateToBookService,
                            colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Pesan Servis Ini", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

