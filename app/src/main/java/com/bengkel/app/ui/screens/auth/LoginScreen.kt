package com.bengkel.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bengkel.app.model.UserRole
import com.bengkel.app.ui.theme.AccentOrange
import com.bengkel.app.ui.theme.NavyBlue
import com.bengkel.app.ui.theme.TextSecondary
import com.bengkel.app.viewmodel.AuthUiState
import com.bengkel.app.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: (UserRole) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("admin@bengkel.com") }
    var password by remember { mutableStateOf("admin123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showConfigDialog by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            val user = (uiState as AuthUiState.Success).user
            onLoginSuccess(user.role)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = { showConfigDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Server Config",
                    tint = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // App Logo & Header
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(AccentOrange),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(42.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Selamat Datang!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = NavyBlue
        )
        Text(
            text = "Masuk ke akun Bengkel App Anda",
            fontSize = 14.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Error message card
        if (uiState is AuthUiState.Error) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = (uiState as AuthUiState.Error).message,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 13.sp
                )
            }
        }

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = null)
                }
            },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onNavigateToForgotPassword) {
                Text(text = "Lupa Password?", fontSize = 13.sp, color = AccentOrange)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Login Button
        Button(
            onClick = { viewModel.login(email, password) },
            enabled = uiState !is AuthUiState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
        ) {
            if (uiState is AuthUiState.Loading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(text = "Masuk", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Role Login Selector for quick testing
        Card(
            colors = CardDefaults.cardColors(containerColor = NavyBlue.copy(alpha = 0.05f)),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Akses Cepat Mode Demo Peran:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilterChip(
                        selected = email == "admin@bengkel.com",
                        onClick = {
                            email = "admin@bengkel.com"
                            password = "admin"
                        },
                        label = { Text("Admin") }
                    )
                    FilterChip(
                        selected = email == "kasir1@bengkel.com",
                        onClick = {
                            email = "kasir1@bengkel.com"
                            password = "kasir"
                        },
                        label = { Text("Kasir") }
                    )
                    FilterChip(
                        selected = email == "customer@bengkel.com",
                        onClick = {
                            email = "customer@bengkel.com"
                            password = "cust"
                        },
                        label = { Text("Pelanggan") }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Belum punya akun pelanggan? ", fontSize = 13.sp, color = TextSecondary)
            TextButton(onClick = onNavigateToRegister) {
                Text(text = "Daftar Sekarang", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AccentOrange)
            }
        }
    }

    // Config Dialog for Backend URL & Mock mode toggle
    if (showConfigDialog) {
        var tempUrl by remember { mutableStateOf(viewModel.getBaseUrl()) }
        var tempMock by remember { mutableStateOf(viewModel.isMockMode()) }

        AlertDialog(
            onDismissRequest = { showConfigDialog = false },
            title = { Text("Pengaturan Backend & API") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Konfigurasi server Laravel REST API atau aktifkan mode offline mock database.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Mode Offline / Mock Data", fontSize = 14.sp)
                        Switch(
                            checked = tempMock,
                            onCheckedChange = { tempMock = it }
                        )
                    }
                    if (!tempMock) {
                        OutlinedTextField(
                            value = tempUrl,
                            onValueChange = { tempUrl = it },
                            label = { Text("BASE_URL Laravel API") },
                            placeholder = { Text("http://10.0.2.2:8000/api/") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.setMockMode(tempMock)
                    viewModel.setBaseUrl(tempUrl)
                    showConfigDialog = false
                }) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfigDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

