package com.bengkel.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.bengkel.app.model.UserRole
import com.bengkel.app.ui.theme.AccentOrange
import com.bengkel.app.ui.theme.NavyBlue

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    // Admin Items
    object AdminDashboard : BottomNavItem("admin_dashboard", "Dashboard", Icons.Default.Dashboard)
    object AdminServices : BottomNavItem("admin_services", "Layanan", Icons.Default.Build)
    object AdminOrders : BottomNavItem("admin_orders", "Pesanan", Icons.Default.Assignment)
    object AdminSpareParts : BottomNavItem("admin_spare_parts", "Stok", Icons.Default.Inventory)
    object AdminReports : BottomNavItem("admin_reports", "Laporan", Icons.Default.BarChart)

    // Cashier Items
    object CashierDashboard : BottomNavItem("cashier_dashboard", "Dashboard", Icons.Default.Dashboard)
    object CashierOrders : BottomNavItem("cashier_orders", "Pesanan", Icons.Default.Assignment)
    object CashierNewTx : BottomNavItem("cashier_direct_reg", "Transaksi", Icons.Default.AddShoppingCart)
    object CashierHistory : BottomNavItem("cashier_history", "Riwayat", Icons.Default.History)

    // Customer Items
    object CustomerHome : BottomNavItem("customer_dashboard", "Beranda", Icons.Default.Home)
    object CustomerServices : BottomNavItem("customer_services", "Layanan", Icons.Default.Build)
    object CustomerBook : BottomNavItem("book_service", "Pesan Servis", Icons.Default.AddCircle)
    object CustomerHistory : BottomNavItem("customer_history", "Riwayat", Icons.Default.History)
    object CustomerProfile : BottomNavItem("common_profile", "Profil", Icons.Default.Person)
}

@Composable
fun BengkelBottomBar(
    role: UserRole,
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = when (role) {
        UserRole.ADMIN -> listOf(
            BottomNavItem.AdminDashboard,
            BottomNavItem.AdminServices,
            BottomNavItem.AdminOrders,
            BottomNavItem.AdminSpareParts,
            BottomNavItem.AdminReports
        )
        UserRole.KASIR -> listOf(
            BottomNavItem.CashierDashboard,
            BottomNavItem.CashierOrders,
            BottomNavItem.CashierNewTx,
            BottomNavItem.CashierHistory,
            BottomNavItem.CustomerProfile
        )
        UserRole.CUSTOMER -> listOf(
            BottomNavItem.CustomerHome,
            BottomNavItem.CustomerServices,
            BottomNavItem.CustomerBook,
            BottomNavItem.CustomerHistory,
            BottomNavItem.CustomerProfile
        )
    }

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = { Text(text = item.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentOrange,
                    selectedTextColor = AccentOrange,
                    indicatorColor = AccentOrange.copy(alpha = 0.15f),
                    unselectedIconColor = NavyBlue.copy(alpha = 0.6f),
                    unselectedTextColor = NavyBlue.copy(alpha = 0.6f)
                )
            )
        }
    }
}

