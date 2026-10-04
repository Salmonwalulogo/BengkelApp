package com.bengkel.app.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bengkel.app.model.WorkshopSettings
import com.bengkel.app.ui.theme.AppBackground
import com.bengkel.app.ui.theme.NavyBlue
import com.bengkel.app.ui.theme.TextSecondary
import com.bengkel.app.viewmodel.AdminViewModel

@Composable
fun WorkshopSettingsScreen(
    viewModel: AdminViewModel
) {
    val currentSettings by viewModel.settings.collectAsState()

    var name by remember(currentSettings) { mutableStateOf(currentSettings.name) }
    var address by remember(currentSettings) { mutableStateOf(currentSettings.address) }
    var phone by remember(currentSettings) { mutableStateOf(currentSettings.phone) }
    var operatingHours by remember(currentSettings) { mutableStateOf(currentSettings.operatingHours) }
    var serviceInfo by remember(currentSettings) { mutableStateOf(currentSettings.serviceInfo) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Pengaturan Bengkel",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = NavyBlue
        )
        Text(
            text = "Atur identitas bengkel, alamat, kontak, dan jam operasional pada struk dan aplikasi",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Bengkel") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Alamat Lengkap Bengkel") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Nomor Telepon / WhatsApp") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = operatingHours,
                    onValueChange = { operatingHours = it },
                    label = { Text("Jam Operasional") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = serviceInfo,
                    onValueChange = { serviceInfo = it },
                    label = { Text("Informasi Layanan / Struk Footer") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val newSettings = WorkshopSettings(
                            name = name,
                            address = address,
                            phone = phone,
                            operatingHours = operatingHours,
                            serviceInfo = serviceInfo
                        )
                        viewModel.saveSettings(newSettings)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
                ) {
                    Text("Simpan Pengaturan Bengkel", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

