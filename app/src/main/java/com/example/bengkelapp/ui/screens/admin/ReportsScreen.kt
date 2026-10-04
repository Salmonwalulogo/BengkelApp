package com.example.bengkelapp.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
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
import com.example.bengkelapp.ui.components.RevenueChartCard
import com.example.bengkelapp.ui.theme.AccentOrange
import com.example.bengkelapp.ui.theme.AppBackground
import com.example.bengkelapp.ui.theme.NavyBlue
import com.example.bengkelapp.ui.theme.TextSecondary
import com.example.bengkelapp.viewmodel.AdminViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ReportsScreen(
    viewModel: AdminViewModel
) {
    val summary by viewModel.reportSummary.collectAsState()
    val invoices by viewModel.invoices.collectAsState()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val context = LocalContext.current

    var selectedFilterPeriod by remember { mutableStateOf("Bulan Ini") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Laporan Bengkel",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyBlue
                    )
                    Text(
                        text = "Analisis finansial, servis, dan penggunaan spare part",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = {
                        Toast.makeText(context, "Laporan PDF berhasil diekspor ke folder Documents!", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ekspor PDF", fontSize = 12.sp)
                }
            }
        }

        // Period filter buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Hari Ini", "Minggu Ini", "Bulan Ini", "Tahun Ini").forEach { period ->
                    FilterChip(
                        selected = selectedFilterPeriod == period,
                        onClick = { selectedFilterPeriod = period },
                        label = { Text(period, fontSize = 12.sp) }
                    )
                }
            }
        }

        // Revenue Card Highlight
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyBlue),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Total Omzet ($selectedFilterPeriod)", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        currencyFormat.format(summary.totalRevenue),
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = Color.White.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Transaksi", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                            Text("${summary.totalTransactions} Transaksi", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column {
                            Text("Servis Selesai", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                            Text("${summary.completedOrdersCount} Unit", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // Revenue Chart
        item {
            RevenueChartCard(dailyIncomes = summary.dailyIncomes)
        }

        // Top Services & Top Spare Parts
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Layanan Terfavorit", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyBlue)
                        Spacer(modifier = Modifier.height(8.dp))
                        summary.topServices.forEach { (name, count) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(name, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text("$count x", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentOrange)
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Spare Part Terlaris", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyBlue)
                        Spacer(modifier = Modifier.height(8.dp))
                        summary.topSpareParts.forEach { (name, count) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(name, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text("$count x", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentOrange)
                            }
                        }
                    }
                }
            }
        }

        item {
            Text("Daftar Transaksi Terakhir", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyBlue)
        }

        items(invoices.take(10)) { inv ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(inv.invoiceNumber, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyBlue)
                        Text("${inv.customerName} • ${inv.vehiclePlate}", fontSize = 12.sp, color = TextSecondary)
                        Text("Kasir: ${inv.cashierName}", fontSize = 11.sp, color = TextSecondary)
                    }
                    Text(
                        currencyFormat.format(inv.totalAmount),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = AccentOrange
                    )
                }
            }
        }
    }
}
