import 'package:flutter/material.dart';

import '../features/auth/login_screen.dart';
import '../features/auth/register_screen.dart';
import '../features/role_guard.dart';

class AppRouter {
  static const String login = '/login';
  static const String register = '/register';
  static const String dashboard = '/dashboard';
  static const String bookings = '/bookings';
  static const String services = '/services';
  static const String transactions = '/transactions';

  static final Map<String, WidgetBuilder> routes = {
    login: (context) => const LoginScreen(),
    register: (context) => const RegisterScreen(),
  };
  static const Set<String> protectedRoutes = {
    dashboard,
    bookings,
    services,
    transactions,
  };

  static Route<dynamic> onGenerateRoute(RouteSettings settings) {
    final routeName = settings.name ?? login;
    if (!protectedRoutes.contains(routeName)) {
      return MaterialPageRoute(builder: (_) => const LoginScreen());
    }

    return MaterialPageRoute(
      builder: (context) => RoleGuard(
        routeName: routeName,
      ),
    );
  }
}
