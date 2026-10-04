import 'package:flutter/material.dart';

import '../../app/app_router.dart';
import '../../data/models/user_model.dart';
import '../../services/auth_service.dart';

class DashboardScreen extends StatelessWidget {
  const DashboardScreen({super.key, required this.user});

  final UserModel user;

  @override
  Widget build(BuildContext context) {
    final isAdmin = user.isAdmin;
    final isCashier = user.isCashier;
    final roleTitle = isAdmin
        ? 'Admin'
        : isCashier
        ? 'Kasir'
        : 'Pelanggan';
    final menuItems = <_DashboardMenuItem>[
      if (isAdmin || isCashier)
        const _DashboardMenuItem(
          title: 'Booking',
          description: 'Lihat dan proses booking servis.',
          icon: Icons.event_available_rounded,
          route: AppRouter.bookings,
        ),
      const _DashboardMenuItem(
        title: 'Layanan',
        description: 'Lihat katalog layanan bengkel.',
        icon: Icons.miscellaneous_services_rounded,
        route: AppRouter.services,
      ),
      if (user.isCustomer)
        const _DashboardMenuItem(
          title: 'Booking Saya',
          description: 'Lihat booking yang terkait dengan akun Anda.',
          icon: Icons.event_note_rounded,
          route: AppRouter.bookings,
        ),
      if (isAdmin || isCashier)
        const _DashboardMenuItem(
          title: 'Transaksi',
          description: 'Lihat transaksi dan status pembayaran.',
          icon: Icons.receipt_long_rounded,
          route: AppRouter.transactions,
        ),
    ];

    return Scaffold(
      appBar: AppBar(
        title: Text('Dashboard $roleTitle'),
        actions: [
          IconButton(
            tooltip: 'Keluar',
            onPressed: () => _logout(context),
            icon: const Icon(Icons.logout_rounded),
          ),
        ],
      ),
      body: ListView(
        padding: const EdgeInsets.all(18),
        children: [
          Text(
            'Halo, ${user.name.isNotEmpty ? user.name : user.username}',
            style: Theme.of(
              context,
            ).textTheme.headlineSmall?.copyWith(fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 8),
          Text(
            isAdmin
                ? 'Kelola operasional bengkel Anda.'
                : isCashier
                ? 'Kelola booking dan transaksi pelanggan.'
                : 'Lihat layanan dan booking servis Anda.',
            style: const TextStyle(color: Colors.grey),
          ),
          const SizedBox(height: 24),
          Text(
            'Menu',
            style: Theme.of(
              context,
            ).textTheme.titleLarge?.copyWith(fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 8),
          for (final item in menuItems)
            _ModuleTile(
              title: item.title,
              description: item.description,
              icon: item.icon,
              onTap: () => Navigator.of(context).pushNamed(item.route),
            ),
        ],
      ),
    );
  }

  Future<void> _logout(BuildContext context) async {
    var remoteLogoutFailed = false;
    try {
      await AuthService().logout();
    } catch (_) {
      remoteLogoutFailed = true;
    }

    if (!context.mounted) return;
    Navigator.of(
      context,
    ).pushNamedAndRemoveUntil(AppRouter.login, (route) => false);
    if (remoteLogoutFailed) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text(
            'Sesi di perangkat sudah dihapus, tetapi server gagal dihubungi.',
          ),
        ),
      );
    }
  }
}

class _DashboardMenuItem {
  const _DashboardMenuItem({
    required this.title,
    required this.description,
    required this.icon,
    required this.route,
  });

  final String title;
  final String description;
  final IconData icon;
  final String route;
}

class _ModuleTile extends StatelessWidget {
  const _ModuleTile({
    required this.title,
    required this.description,
    required this.icon,
    this.onTap,
  });

  final String title;
  final String description;
  final IconData icon;
  final VoidCallback? onTap;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: ListTile(
        onTap: onTap,
        leading: Icon(icon, color: Theme.of(context).colorScheme.primary),
        title: Text(title),
        subtitle: Text(description),
        trailing: const Icon(Icons.arrow_forward_ios_rounded, size: 16),
      ),
    );
  }
}
