package com.bengkel.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bengkel.app.model.UserRole
import com.bengkel.app.ui.components.BengkelBottomBar
import com.bengkel.app.ui.components.BengkelTopBar
import com.bengkel.app.ui.screens.admin.*
import com.bengkel.app.ui.screens.auth.ForgotPasswordScreen
import com.bengkel.app.ui.screens.auth.LoginScreen
import com.bengkel.app.ui.screens.auth.RegisterScreen
import com.bengkel.app.ui.screens.cashier.CashierDashboardScreen
import com.bengkel.app.ui.screens.cashier.CashierPaymentScreen
import com.bengkel.app.ui.screens.cashier.DirectServiceRegistrationScreen
import com.bengkel.app.ui.screens.common.UserProfileScreen
import com.bengkel.app.ui.screens.customer.*
import com.bengkel.app.ui.screens.splash.OnboardingScreen
import com.bengkel.app.ui.screens.splash.SplashScreen
import com.bengkel.app.ui.theme.BengkelAppTheme
import com.bengkel.app.viewmodel.AdminViewModel
import com.bengkel.app.viewmodel.AuthViewModel
import com.bengkel.app.viewmodel.CashierViewModel
import com.bengkel.app.viewmodel.CustomerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BengkelAppTheme {
                BengkelMainApp()
            }
        }
    }
}

@Composable
fun BengkelMainApp() {
    val context = LocalContext.current
    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel()
    val adminViewModel: AdminViewModel = viewModel()
    val cashierViewModel: CashierViewModel = viewModel()
    val customerViewModel: CustomerViewModel = viewModel()

    val currentUser by authViewModel.currentUser.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "splash"

    // Toast listener from viewmodels
    LaunchedEffect(Unit) {
        adminViewModel.toastMessage.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(Unit) {
        cashierViewModel.toastMessage.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(Unit) {
        customerViewModel.toastMessage.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    val isAuthOrSplashRoute = currentRoute in listOf(
        "splash", "onboarding", "login", "register", "forgot_password"
    )

    val topBarTitle = when {
        currentRoute.startsWith("admin_dashboard") -> "Bengkel App - Admin"
        currentRoute.startsWith("admin_services") -> "Manajemen Layanan"
        currentRoute.startsWith("admin_orders") -> "Kelola Pesanan Servis"
        currentRoute.startsWith("admin_spare_parts") -> "Stok Suku Cadang"
        currentRoute.startsWith("admin_reports") -> "Laporan & Transaksi"
        currentRoute.startsWith("admin_cashiers") -> "Kelola Akun Kasir"
        currentRoute.startsWith("admin_customers") -> "Data Pelanggan"
        currentRoute.startsWith("admin_vehicles") -> "Data Kendaraan"
        currentRoute.startsWith("admin_settings") -> "Pengaturan Bengkel"
        currentRoute.startsWith("cashier_dashboard") -> "Kasir Bengkel"
        currentRoute.startsWith("cashier_direct_reg") -> "Pendaftaran Servis Direct"
        currentRoute.startsWith("cashier_payment") -> "Proses Pembayaran"
        currentRoute.startsWith("customer_dashboard") -> "Beranda Pelanggan"
        currentRoute.startsWith("customer_services") -> "Layanan Servis"
        currentRoute.startsWith("book_service") -> "Booking Servis Online"
        currentRoute.startsWith("customer_history") -> "Riwayat Servis"
        currentRoute.startsWith("common_profile") -> "Profil Saya"
        else -> "Bengkel App"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (!isAuthOrSplashRoute) {
                BengkelTopBar(
                    title = topBarTitle,
                    canNavigateBack = currentRoute in listOf(
                        "cashier_direct_reg", "admin_cashiers", "admin_customers", "admin_vehicles", "admin_settings"
                    ) || currentRoute.startsWith("cashier_payment"),
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        },
        bottomBar = {
            if (!isAuthOrSplashRoute && currentUser != null) {
                BengkelBottomBar(
                    role = currentUser!!.role,
                    currentRoute = currentRoute,
                    onNavigate = { targetRoute ->
                        navController.navigate(targetRoute) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding)
        ) {
            // Splash & Auth Routes
            composable("splash") {
                SplashScreen(
                    currentUser = currentUser,
                    isOnboardingCompleted = authViewModel.isOnboardingCompleted(),
                    onNavigateToOnboarding = { navController.navigate("onboarding") { popUpTo("splash") { inclusive = true } } },
                    onNavigateToLogin = { navController.navigate("login") { popUpTo("splash") { inclusive = true } } },
                    onNavigateToDashboard = { role ->
                        val startRoute = when (role) {
                            UserRole.ADMIN -> "admin_dashboard"
                            UserRole.KASIR -> "cashier_dashboard"
                            UserRole.CUSTOMER -> "customer_dashboard"
                        }
                        navController.navigate(startRoute) { popUpTo("splash") { inclusive = true } }
                    }
                )
            }

            composable("onboarding") {
                OnboardingScreen(
                    onFinishOnboarding = { authViewModel.setOnboardingCompleted() },
                    onNavigateToLogin = { navController.navigate("login") { popUpTo("onboarding") { inclusive = true } } },
                    onNavigateToRegister = { navController.navigate("register") }
                )
            }

            composable("login") {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = { role ->
                        val startRoute = when (role) {
                            UserRole.ADMIN -> "admin_dashboard"
                            UserRole.KASIR -> "cashier_dashboard"
                            UserRole.CUSTOMER -> "customer_dashboard"
                        }
                        navController.navigate(startRoute) { popUpTo("login") { inclusive = true } }
                    },
                    onNavigateToRegister = { navController.navigate("register") },
                    onNavigateToForgotPassword = { navController.navigate("forgot_password") }
                )
            }

            composable("register") {
                RegisterScreen(
                    viewModel = authViewModel,
                    onRegisterSuccess = { navController.navigate("customer_dashboard") { popUpTo("register") { inclusive = true } } },
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }

            composable("forgot_password") {
                ForgotPasswordScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Admin Routes
            composable("admin_dashboard") {
                AdminDashboardScreen(
                    viewModel = adminViewModel,
                    onNavigateToCashiers = { navController.navigate("admin_cashiers") },
                    onNavigateToCustomers = { navController.navigate("admin_customers") },
                    onNavigateToVehicles = { navController.navigate("admin_vehicles") },
                    onNavigateToServices = { navController.navigate("admin_services") },
                    onNavigateToOrders = { navController.navigate("admin_orders") },
                    onNavigateToSpareParts = { navController.navigate("admin_spare_parts") },
                    onNavigateToReports = { navController.navigate("admin_reports") },
                    onNavigateToSettings = { navController.navigate("admin_settings") }
                )
            }

            composable("admin_services") {
                ServiceManagementScreen(viewModel = adminViewModel)
            }

            composable("admin_orders") {
                ServiceOrderManagementScreen(viewModel = adminViewModel)
            }

            composable("admin_spare_parts") {
                SparePartManagementScreen(viewModel = adminViewModel)
            }

            composable("admin_reports") {
                ReportsScreen(viewModel = adminViewModel)
            }

            composable("admin_cashiers") {
                CashierManagementScreen(viewModel = adminViewModel)
            }

            composable("admin_customers") {
                CustomerManagementScreen(viewModel = adminViewModel)
            }

            composable("admin_vehicles") {
                VehicleManagementScreen(viewModel = adminViewModel)
            }

            composable("admin_settings") {
                WorkshopSettingsScreen(viewModel = adminViewModel)
            }

            // Cashier Routes
            composable("cashier_dashboard") {
                CashierDashboardScreen(
                    viewModel = cashierViewModel,
                    onNavigateToDirectReg = { navController.navigate("cashier_direct_reg") },
                    onNavigateToOrders = { navController.navigate("cashier_orders") },
                    onNavigateToPayment = { orderId -> navController.navigate("cashier_payment/$orderId") },
                    onNavigateToHistory = { navController.navigate("cashier_history") }
                )
            }

            composable("cashier_orders") {
                ServiceOrderManagementScreen(viewModel = adminViewModel)
            }

            composable("cashier_direct_reg") {
                DirectServiceRegistrationScreen(
                    viewModel = cashierViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "cashier_payment/{orderId}",
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                CashierPaymentScreen(
                    orderId = orderId,
                    viewModel = cashierViewModel,
                    cashierName = currentUser?.name ?: "Kasir",
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("cashier_history") {
                ReportsScreen(viewModel = adminViewModel)
            }

            // Customer Routes
            composable("customer_dashboard") {
                CustomerHomeScreen(
                    currentUser = currentUser,
                    viewModel = customerViewModel,
                    onNavigateToBookService = { navController.navigate("book_service") },
                    onNavigateToServices = { navController.navigate("customer_services") },
                    onNavigateToHistory = { navController.navigate("customer_history") },
                    onNavigateToProfile = { navController.navigate("common_profile") }
                )
            }

            composable("customer_services") {
                CustomerServicesScreen(
                    viewModel = customerViewModel,
                    onNavigateToBookService = { navController.navigate("book_service") }
                )
            }

            composable("book_service") {
                BookServiceScreen(
                    viewModel = customerViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onBookingSuccess = {
                        navController.navigate("customer_history") {
                            popUpTo("book_service") { inclusive = true }
                        }
                    }
                )
            }

            composable("customer_history") {
                CustomerHistoryScreen(viewModel = customerViewModel)
            }

            // Shared / Common Profile Route
            composable("common_profile") {
                UserProfileScreen(
                    currentUser = currentUser,
                    authViewModel = authViewModel,
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

