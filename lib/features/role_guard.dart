import 'package:flutter/material.dart';

import '../app/app_router.dart';
import '../data/local/app_database.dart';
import '../data/models/user_model.dart';
import 'auth/login_screen.dart';
import 'bookings/booking_screen.dart';
import 'dashboard/dashboard_screen.dart';
import 'services/service_screen.dart';
import 'transactions/transaction_screen.dart';

class RoleGuard extends StatefulWidget {
  const RoleGuard({super.key, required this.routeName});

  final String routeName;

  static bool canAccessRoute(UserModel user, String routeName) {
    if (!user.isAdmin && !user.isCashier && !user.isCustomer) return false;

    switch (routeName) {
      case AppRouter.dashboard:
        return true;
      case AppRouter.bookings:
      case AppRouter.services:
        return true;
      case AppRouter.transactions:
        return user.isAdmin || user.isCashier;
      default:
        return false;
    }
  }

  @override
  State<RoleGuard> createState() => _RoleGuardState();
}

class _RoleGuardState extends State<RoleGuard> {
  bool _isLoading = true;
  UserModel? _user;
  Object? _loadError;

  @override
  void initState() {
    super.initState();
    _loadSession();
  }

  Future<void> _loadSession() async {
    try {
      final userJson = await AppDatabase.instance.getUserSession();
      if (!mounted) return;

      final user = userJson == null ? null : UserModel.fromLocalMap(userJson);
      setState(() {
        _user = user?.token.isNotEmpty == true ? user : null;
        _isLoading = false;
      });
    } catch (error) {
      if (!mounted) return;
      setState(() {
        _loadError = error;
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    }

    if (_loadError != null) {
      return const Scaffold(
        body: Center(
          child: Text('Sesi tidak dapat dibaca. Silakan mulai ulang aplikasi.'),
        ),
      );
    }

    if (_user == null) {
      return const LoginScreen();
    }

    if (!RoleGuard.canAccessRoute(_user!, widget.routeName)) {
      return Scaffold(
        appBar: AppBar(title: const Text('Akses ditolak')),
        body: Center(
          child: Padding(
            padding: const EdgeInsets.all(24),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                const Icon(Icons.lock_outline_rounded, size: 48),
                const SizedBox(height: 12),
                const Text(
                  'Anda tidak memiliki akses ke menu ini.',
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 16),
                FilledButton(
                  onPressed: () =>
                      Navigator.of(context).pushNamedAndRemoveUntil(
                        AppRouter.dashboard,
                        (route) => false,
                      ),
                  child: const Text('Kembali ke dashboard'),
                ),
              ],
            ),
          ),
        ),
      );
    }

    switch (widget.routeName) {
      case AppRouter.bookings:
        return BookingScreen(user: _user!);
      case AppRouter.services:
        return ServiceScreen(user: _user!);
      case AppRouter.transactions:
        return TransactionScreen(user: _user!);
      case AppRouter.dashboard:
      default:
        return DashboardScreen(user: _user!);
    }
  }
}
