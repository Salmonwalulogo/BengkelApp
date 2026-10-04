package com.example.bengkelapp.ui.screens.cashier

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bengkelapp.model.*
import com.example.bengkelapp.ui.components.StrukPdfGenerator
import com.example.bengkelapp.ui.theme.*
import com.example.bengkelapp.viewmodel.CashierViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashierPaymentScreen(
    orderId: String,
    viewModel: CashierViewModel,
    cashierName: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val orders by viewModel.orders.collectAsState()
    val spareParts by viewModel.spareParts.collectAsState()
    val services by viewModel.services.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val createdInvoice by viewModel.createdInvoice.collectAsState()

    val targetOrder = orders.find { it.id == orderId }
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    val invoiceItems = remember { mutableStateListOf<OrderItemDetail>() }
    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.CASH) }
    var showAddPartDialog by remember { mutableStateOf(false) }

    LaunchedEffect(targetOrder) {
        if (targetOrder != null && invoiceItems.isEmpty()) {
            targetOrder.serviceNames.forEach { sName ->
                val matched = services.find { it.name == sName }
                val price = matched?.price ?: 75000.0
                invoiceItems.add(
                    OrderItemDetail(
                        name = sName,
                        type = "SERVICE",
                        qty = 1,
                        price = price
                    )
                )
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearCreatedInvoice()
        }
    }

    if (targetOrder == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Pesanan tidak ditemukan", color = TextSecondary)
        }
        return
    }

    val totalServices = invoiceItems.filter { it.type == "SERVICE" }.sumOf { it.total }
    val totalParts = invoiceItems.filter { it.type == "PART" }.sumOf { it.total }
    val grandTotal = totalServices + totalParts

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
            Text("Kasir - Kasir Pembayaran", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyBlue)
        }

        if (createdInvoice != null) {
            // Receipt Success View
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = StatusSuccess,
                        modifier = Modifier.size(56.dp)
                    )
                    Text("Pembayaran Berhasil Dilakukan!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = StatusSuccess)
                    Text("No. Struk: ${createdInvoice?.invoiceNumber}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = NavyBlue)

                    Text(
                        "Total Biaya: ${currencyFormat.format(createdInvoice?.totalAmount ?: 0.0)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyBlue
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val inv = createdInvoice
                            if (inv != null) {
                                StrukPdfGenerator.generateAndSaveReceiptPdf(context, inv, settings)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cetak / Simpan Struk PDF", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Selesai & Kembali", color = NavyBlue)
                    }
                }
            }
        } else {
            // Invoice & Item Breakdown Form
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Detail Pesanan #${targetOrder.orderNumber}", fontWeight = FontWeight.Bold, color = NavyBlue)
                    Text("Pelanggan : ${targetOrder.customerName} (${targetOrder.customerPhone})", fontSize = 13.sp)
                    Text("Kendaraan : ${targetOrder.vehicleModel} - ${targetOrder.vehiclePlate}", fontSize = 13.sp)
                    Text("Keluhan   : ${targetOrder.complaint}", fontSize = 13.sp, color = TextSecondary)
                }
            }

            // Rincian Item (Jasa + Suku Cadang)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Rincian Biaya (Jasa & Spare Part)", fontWeight = FontWeight.Bold, color = NavyBlue)
                        TextButton(onClick = { showAddPartDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Tambah Part", fontSize = 12.sp, color = AccentOrange)
                        }
                    }

                    invoiceItems.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AssistChip(
                                        onClick = {},
                                        label = { Text(if (item.type == "SERVICE") "JASA" else "PART", fontSize = 10.sp) },
                                        modifier = Modifier.height(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(item.name, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                }
                                Text("${item.qty} x ${currencyFormat.format(item.price)}", fontSize = 11.sp, color = TextSecondary)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(currencyFormat.format(item.total), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                IconButton(onClick = { invoiceItems.removeAt(index) }) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                        Divider()
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal Jasa:", fontSize = 13.sp, color = TextSecondary)
                        Text(currencyFormat.format(totalServices), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal Suku Cadang:", fontSize = 13.sp, color = TextSecondary)
                        Text(currencyFormat.format(totalParts), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Divider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("TOTAL BAYAR:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyBlue)
                        Text(currencyFormat.format(grandTotal), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = AccentOrange)
                    }
                }
            }

            // Payment Method Selection Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Metode Pembayaran", fontWeight = FontWeight.Bold, color = NavyBlue)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PaymentMethod.entries.forEach { method ->
                            FilterChip(
                                selected = selectedPaymentMethod == method,
                                onClick = { selectedPaymentMethod = method },
                                label = { Text(method.displayName, fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    if (invoiceItems.isNotEmpty()) {
                        viewModel.processPayment(
                            orderId = targetOrder.id,
                            cashierName = cashierName,
                            items = invoiceItems.toList(),
                            method = selectedPaymentMethod
                        )
                    }
                },
                enabled = invoiceItems.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess)
            ) {
                Text("Proses Pembayaran & Cetak Struk", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    // Add Spare Part Dialog
    if (showAddPartDialog) {
        var selectedPart by remember { mutableStateOf<SparePart?>(null) }
        var qty by remember { mutableIntStateOf(1) }

        AlertDialog(
            onDismissRequest = { showAddPartDialog = false },
            title = { Text("Tambah Suku Cadang ke Tagihan") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Pilih suku cadang yang diganti saat pengerjaan:", fontSize = 12.sp, color = TextSecondary)

                    spareParts.forEach { part ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(part.name, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Text("Stok: ${part.stock} • ${currencyFormat.format(part.sellPrice)}", fontSize = 11.sp, color = TextSecondary)
                            }
                            RadioButton(
                                selected = selectedPart == part,
                                onClick = { selectedPart = part }
                            )
                        }
                    }

                    if (selectedPart != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Jumlah (Qty):", fontSize = 13.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedButton(onClick = { if (qty > 1) qty-- }) { Text("-") }
                                Text(" $qty ", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                                OutlinedButton(onClick = { if (qty < (selectedPart?.stock ?: 1)) qty++ }) { Text("+") }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val part = selectedPart
                        if (part != null) {
                            invoiceItems.add(
                                OrderItemDetail(
                                    name = part.name,
                                    type = "PART",
                                    qty = qty,
                                    price = part.sellPrice
                                )
                            )
                            showAddPartDialog = false
                        }
                    },
                    enabled = selectedPart != null
                ) {
                    Text("Tambahkan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPartDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
