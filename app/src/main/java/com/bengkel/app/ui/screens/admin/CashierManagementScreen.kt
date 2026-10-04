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
import com.bengkel.app.model.User
import com.bengkel.app.model.UserRole
import com.bengkel.app.ui.theme.AccentOrange
import com.bengkel.app.ui.theme.AppBackground
import com.bengkel.app.ui.theme.NavyBlue
import com.bengkel.app.ui.theme.TextSecondary
import com.bengkel.app.viewmodel.AdminViewModel

@Composable
fun CashierManagementScreen(
    viewModel: AdminViewModel
) {
    val cashiers by viewModel.cashiers.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedCashier by remember { mutableStateOf<User?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedCashier = null
                    showAddDialog = true
                },
                containerColor = AccentOrange,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Kasir")
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
                    text = "Manajemen Akun Kasir",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )
                Text(
                    text = "Tambah dan kelola hak akses akun petugas kasir bengkel",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (cashiers.isEmpty()) {
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
                            Text("Belum ada akun kasir terdaftar.", color = TextSecondary)
                        }
                    }
                }
            } else {
                items(cashiers) { cashier ->
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
                                Text(cashier.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NavyBlue)
                                Text(cashier.email, fontSize = 13.sp, color = TextSecondary)
                                Text(cashier.phone, fontSize = 12.sp, color = TextSecondary)
                            }

                            Row {
                                IconButton(onClick = {
                                    selectedCashier = cashier
                                    showAddDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = NavyBlue)
                                }
                                IconButton(onClick = {
                                    viewModel.deleteUser(cashier.id)
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
        var name by remember { mutableStateOf(selectedCashier?.name ?: "") }
        var email by remember { mutableStateOf(selectedCashier?.email ?: "") }
        var phone by remember { mutableStateOf(selectedCashier?.phone ?: "") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(if (selectedCashier == null) "Tambah Akun Kasir" else "Edit Akun Kasir") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Kasir") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Login") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Nomor Telepon") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank() && email.isNotBlank()) {
                            val user = User(
                                id = selectedCashier?.id ?: "",
                                name = name,
                                email = email,
                                phone = phone,
                                role = UserRole.KASIR
                            )
                            viewModel.saveUser(user)
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

