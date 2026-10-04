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
import com.bengkel.app.model.SparePart
import com.bengkel.app.ui.theme.*
import com.bengkel.app.viewmodel.AdminViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SparePartManagementScreen(
    viewModel: AdminViewModel
) {
    val spareParts by viewModel.spareParts.collectAsState()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    var searchQuery by remember { mutableStateOf("") }
    var showLowStockOnly by remember { mutableStateOf(false) }

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedPart by remember { mutableStateOf<SparePart?>(null) }
    var showStockAdjustmentDialog by remember { mutableStateOf<SparePart?>(null) }

    val filteredParts = spareParts.filter { part ->
        val matchesQuery = part.name.contains(searchQuery, ignoreCase = true) ||
                part.code.contains(searchQuery, ignoreCase = true) ||
                part.supplier.contains(searchQuery, ignoreCase = true)
        val matchesLowStock = if (showLowStockOnly) part.isLowStock else true
        matchesQuery && matchesLowStock
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedPart = null
                    showAddDialog = true
                },
                containerColor = AccentOrange,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Barang")
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
                    text = "Inventaris Suku Cadang",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )
                Text(
                    text = "Kelola stok spare part, harga beli/jual, dan peringatan stok menipis",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari nama, kode barang, atau supplier...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = showLowStockOnly,
                        onClick = { showLowStockOnly = !showLowStockOnly },
                        label = { Text("Stok Menipis (${spareParts.count { it.isLowStock }})") },
                        leadingIcon = {
                            if (showLowStockOnly) Icon(Icons.Default.Warning, contentDescription = null, tint = StatusCancelled)
                        }
                    )

                    Text(
                        text = "Total Items: ${filteredParts.size}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            if (filteredParts.isEmpty()) {
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
                            Text("Tidak ada suku cadang ditemukan.", color = TextSecondary)
                        }
                    }
                }
            } else {
                items(filteredParts) { part ->
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
                                    Text(part.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyBlue)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("[${part.code}]", fontSize = 11.sp, color = TextSecondary)
                                }

                                Text(
                                    "Beli: ${currencyFormat.format(part.buyPrice)} â€¢ Jual: ${currencyFormat.format(part.sellPrice)}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Stok: ${part.stock} ${part.unit}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (part.isLowStock) StatusCancelled else StatusSuccess
                                    )

                                    if (part.isLowStock) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            color = StatusCancelled.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                "STOK MENIPIS (Min: ${part.minStock})",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = StatusCancelled,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = {
                                    showStockAdjustmentDialog = part
                                }) {
                                    Icon(Icons.Default.SwapVert, contentDescription = "Tambah/Kurang Stok", tint = AccentOrange)
                                }
                                IconButton(onClick = {
                                    selectedPart = part
                                    showAddDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = NavyBlue)
                                }
                                IconButton(onClick = {
                                    viewModel.deleteSparePart(part.id)
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
        var code by remember { mutableStateOf(selectedPart?.code ?: "") }
        var name by remember { mutableStateOf(selectedPart?.name ?: "") }
        var buyPriceStr by remember { mutableStateOf(selectedPart?.buyPrice?.toInt()?.toString() ?: "") }
        var sellPriceStr by remember { mutableStateOf(selectedPart?.sellPrice?.toInt()?.toString() ?: "") }
        var stockStr by remember { mutableStateOf(selectedPart?.stock?.toString() ?: "10") }
        var minStockStr by remember { mutableStateOf(selectedPart?.minStock?.toString() ?: "5") }
        var unit by remember { mutableStateOf(selectedPart?.unit ?: "Pcs") }
        var supplier by remember { mutableStateOf(selectedPart?.supplier ?: "") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(if (selectedPart == null) "Tambah Suku Cadang" else "Edit Suku Cadang") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = code,
                            onValueChange = { code = it },
                            label = { Text("Kode Barang") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text("Satuan") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Spare Part") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = buyPriceStr,
                            onValueChange = { buyPriceStr = it },
                            label = { Text("Harga Beli (Rp)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = sellPriceStr,
                            onValueChange = { sellPriceStr = it },
                            label = { Text("Harga Jual (Rp)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = stockStr,
                            onValueChange = { stockStr = it },
                            label = { Text("Stok Awal") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = minStockStr,
                            onValueChange = { minStockStr = it },
                            label = { Text("Stok Min.") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = supplier,
                        onValueChange = { supplier = it },
                        label = { Text("Supplier / Vendor") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            val part = SparePart(
                                id = selectedPart?.id ?: "",
                                code = code,
                                name = name,
                                buyPrice = buyPriceStr.toDoubleOrNull() ?: 0.0,
                                sellPrice = sellPriceStr.toDoubleOrNull() ?: 0.0,
                                stock = stockStr.toIntOrNull() ?: 0,
                                minStock = minStockStr.toIntOrNull() ?: 5,
                                unit = unit.ifEmpty { "Pcs" },
                                supplier = supplier
                            )
                            viewModel.saveSparePart(part)
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

    if (showStockAdjustmentDialog != null) {
        val part = showStockAdjustmentDialog!!
        var qtyChangeStr by remember { mutableStateOf("1") }
        var isIncoming by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { showStockAdjustmentDialog = null },
            title = { Text("Catat Perubahan Stok: ${part.name}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Stok Saat Ini: ${part.stock} ${part.unit}", fontWeight = FontWeight.Bold, color = NavyBlue)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FilterChip(
                            selected = isIncoming,
                            onClick = { isIncoming = true },
                            label = { Text("Barang Masuk (+)") }
                        )
                        FilterChip(
                            selected = !isIncoming,
                            onClick = { isIncoming = false },
                            label = { Text("Barang Keluar (-)") }
                        )
                    }

                    OutlinedTextField(
                        value = qtyChangeStr,
                        onValueChange = { qtyChangeStr = it },
                        label = { Text("Jumlah Quantity") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val qty = qtyChangeStr.toIntOrNull() ?: 0
                        val delta = if (isIncoming) qty else -qty
                        viewModel.updateSparePartStock(part.id, delta)
                        showStockAdjustmentDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
                ) {
                    Text("Update Stok")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStockAdjustmentDialog = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

