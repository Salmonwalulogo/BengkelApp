package com.bengkel.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bengkel.app.model.DailyIncome
import com.bengkel.app.ui.theme.AccentOrange
import com.bengkel.app.ui.theme.NavyBlue
import com.bengkel.app.ui.theme.TextPrimary
import com.bengkel.app.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun RevenueChartCard(
    dailyIncomes: List<DailyIncome>,
    modifier: Modifier = Modifier
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Grafik Pendapatan Mingguan",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Ringkasan pemasukan 7 hari terakhir",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            val maxAmount = dailyIncomes.maxOfOrNull { it.amount }?.coerceAtLeast(100000.0) ?: 1000000.0

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                dailyIncomes.forEach { income ->
                    val barHeightRatio = (income.amount / maxAmount).toFloat().coerceIn(0.1f, 1f)
                    val formattedPrice = currencyFormat.format(income.amount)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (income.amount >= 1000000) "${(income.amount / 1000000).toInt()}M" else "${(income.amount / 1000).toInt()}k",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .fillMaxHeight(barHeightRatio)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(if (income.day == "Sabtu" || income.day == "Minggu") AccentOrange else NavyBlue)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = income.day.take(3),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}

