class AppConstants {
  AppConstants._();

  static const String appName = 'Bengkel App';
  static const String apiBaseUrl = String.fromEnvironment(
    'API_BASE_URL',
  );
  static const String localDbName = 'bengkel_app.db';
  static const String defaultRole = 'customer';
}
