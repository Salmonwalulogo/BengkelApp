package com.bengkel.app.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bengkel.app.ui.theme.AccentOrange
import com.bengkel.app.ui.theme.NavyBlue
import com.bengkel.app.ui.theme.TextSecondary

data class OnboardingItem(
    val title: String,
    val description: String,
    val icon: ImageVector
)

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val items = listOf(
        OnboardingItem(
            title = "Servis Tanpa Antre",
            description = "Pesan jadwal servis kendaraan Anda secara online dengan mudah, cepat, dan transparan.",
            icon = Icons.Default.DirectionsCar
        ),
        OnboardingItem(
            title = "Layanan Terpercaya",
            description = "Pantau status pengerjaan servis secara langsung dari HP Anda kapan saja.",
            icon = Icons.Default.Build
        ),
        OnboardingItem(
            title = "Transaksi & Struk Digital",
            description = "Dapatkan rincian biaya yang akurat beserta buktikan pembayaran transparan.",
            icon = Icons.Default.ReceiptLong
        )
    )

    var pageIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Skip Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = {
                onFinishOnboarding()
                onNavigateToLogin()
            }) {
                Text(text = "Lewati", color = TextSecondary)
            }
        }

        // Onboarding Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(NavyBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = items[pageIndex].icon,
                    contentDescription = null,
                    tint = AccentOrange,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = items[pageIndex].title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = NavyBlue,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = items[pageIndex].description,
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Page Indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items.indices.forEach { index ->
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (index == pageIndex) 24.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (index == pageIndex) AccentOrange else NavyBlue.copy(alpha = 0.2f))
                    )
                }
            }
        }

        // Action Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (pageIndex < items.size - 1) {
                Button(
                    onClick = { pageIndex++ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
                ) {
                    Text(text = "Lanjut", modifier = Modifier.padding(vertical = 6.dp))
                }
            } else {
                Button(
                    onClick = {
                        onFinishOnboarding()
                        onNavigateToLogin()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
                ) {
                    Text(text = "Masuk ke Aplikasi", modifier = Modifier.padding(vertical = 6.dp))
                }

                OutlinedButton(
                    onClick = {
                        onFinishOnboarding()
                        onNavigateToRegister()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Daftar Akun Baru", color = NavyBlue, modifier = Modifier.padding(vertical = 6.dp))
                }
            }
        }
    }
}

