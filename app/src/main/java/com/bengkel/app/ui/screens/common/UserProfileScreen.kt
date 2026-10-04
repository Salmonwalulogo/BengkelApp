package com.bengkel.app.ui.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bengkel.app.model.User
import com.bengkel.app.ui.theme.*
import com.bengkel.app.viewmodel.AuthViewModel

@Composable
fun UserProfileScreen(
    currentUser: User?,
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    var showEditProfileDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Avatar
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(AccentOrange),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(52.dp)
            )
        }

        Text(
            text = currentUser?.name ?: "Pengguna",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = NavyBlue
        )

        AssistChip(
            onClick = {},
            label = { Text(currentUser?.role?.displayName ?: "Pengguna", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
            colors = AssistChipDefaults.assistChipColors(containerColor = NavyBlue.copy(alpha = 0.1f), labelColor = NavyBlue)
        )

        // Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = NavyBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Email", fontSize = 11.sp, color = TextSecondary)
                        Text(currentUser?.email ?: "-", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Divider()

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = NavyBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Nomor Telepon", fontSize = 11.sp, color = TextSecondary)
                        Text(currentUser?.phone ?: "-", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Divider()

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = NavyBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Terdaftar Sejak", fontSize = 11.sp, color = TextSecondary)
                        Text(currentUser?.createdAt ?: "-", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // App Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Informasi Aplikasi", fontWeight = FontWeight.Bold, color = NavyBlue, fontSize = 14.sp)
                Text("Versi Aplikasi : 1.0.0 (Release)", fontSize = 12.sp, color = TextSecondary)
                Text("Database Local  : Room Database SQLite", fontSize = 12.sp, color = TextSecondary)
                Text("Backend Server  : Laravel REST API (http://10.0.2.2:8000/api/)", fontSize = 12.sp, color = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Logout Button
        Button(
            onClick = {
                authViewModel.logout()
                onLogout()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Keluar dari Akun (Logout)", fontWeight = FontWeight.Bold)
        }
    }
}

