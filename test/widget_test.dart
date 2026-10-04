// This is a basic Flutter widget test.
//
// To perform an interaction with a widget in your test, use the WidgetTester
// utility in the flutter_test package. For example, you can send tap and scroll
// gestures. You can also use WidgetTester to find child widgets in the widget
// tree, read text, and verify that the values of widget properties are correct.

import 'package:bengkel_app/app/app_router.dart';
import 'package:bengkel_app/data/models/user_model.dart';
import 'package:bengkel_app/features/dashboard/dashboard_screen.dart';
import 'package:bengkel_app/features/role_guard.dart';
import 'package:bengkel_app/main.dart';
import 'package:bengkel_app/services/api_client.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  test('API authorization uses the Bearer scheme', () {
    expect(
      ApiClient.buildAuthorizationHeader('test-token'),
      'Bearer test-token',
    );
  });

  testWidgets('Bengkel app loads login screen', (WidgetTester tester) async {
    await tester.pumpWidget(const BengkelApp());

    expect(find.text('Bengkel App'), findsOneWidget);
    expect(find.text('Masuk'), findsOneWidget);
  });

  testWidgets('customer dashboard shows customer menus and logout', (
    tester,
  ) async {
    await tester.pumpWidget(
      MaterialApp(
        home: DashboardScreen(user: _user(role: 'customer')),
      ),
    );

    expect(find.text('Layanan'), findsOneWidget);
    expect(find.text('Booking Saya'), findsOneWidget);
    expect(find.text('Transaksi'), findsNothing);
    expect(find.byTooltip('Keluar'), findsOneWidget);
  });

  testWidgets('admin dashboard shows operations menus and logout', (
    tester,
  ) async {
    await tester.pumpWidget(
      MaterialApp(
        home: DashboardScreen(user: _user(role: 'admin')),
      ),
    );

    expect(find.text('Booking'), findsOneWidget);
    expect(find.text('Layanan'), findsOneWidget);
    expect(find.text('Transaksi'), findsOneWidget);
    expect(find.byTooltip('Keluar'), findsOneWidget);
  });

  testWidgets('cashier dashboard shows cashier menus and logout', (
    tester,
  ) async {
    await tester.pumpWidget(
      MaterialApp(
        home: DashboardScreen(user: _user(role: 'kasir')),
      ),
    );

    expect(find.text('Booking'), findsOneWidget);
    expect(find.text('Layanan'), findsOneWidget);
    expect(find.text('Transaksi'), findsOneWidget);
    expect(find.byTooltip('Keluar'), findsOneWidget);
  });

  test('customer cannot access transactions, but staff can', () {
    expect(
      RoleGuard.canAccessRoute(_user(role: 'customer'), AppRouter.transactions),
      isFalse,
    );
    expect(
      RoleGuard.canAccessRoute(_user(role: 'admin'), AppRouter.transactions),
      isTrue,
    );
    expect(
      RoleGuard.canAccessRoute(_user(role: 'kasir'), AppRouter.transactions),
      isTrue,
    );
  });

  test('protected routes are resolved through the role guard', () {
    expect(AppRouter.routes.keys, contains(AppRouter.login));
    expect(AppRouter.routes.keys, contains(AppRouter.register));
    expect(
      AppRouter.protectedRoutes.difference(AppRouter.routes.keys.toSet()),
      containsAll({
        AppRouter.dashboard,
        AppRouter.bookings,
        AppRouter.services,
        AppRouter.transactions,
      }),
    );
  });
}

UserModel _user({required String role}) => UserModel(
  id: 1,
  name: 'Test User',
  email: 'test@example.com',
  username: 'test',
  role: role,
  token: 'test-token',
);
