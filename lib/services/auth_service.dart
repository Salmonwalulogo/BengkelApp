import 'dart:convert';

import 'package:http/http.dart' as http;

import '../data/local/app_database.dart';
import '../data/models/user_model.dart';
import 'api_client.dart';

class AuthService {
  static const String _loginPath = '/login';
  static const String _registerPath = '/register';
  static const String _mePath = '/me';
  static const String _logoutPath = '/logout';
  static const Duration _requestTimeout = Duration(seconds: 20);

  Future<UserModel?> login({
    required String email,
    required String password,
  }) async {
    final response = await http
        .post(
          ApiClient.buildUri(_loginPath),
          headers: const {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
          },
          body: jsonEncode({'email': email, 'password': password}),
        )
        .timeout(_requestTimeout);

    if (response.statusCode != 200 && response.statusCode != 201) {
      return null;
    }

    final data = _decodeBody(response.body);
    final token = _responseToken(data);
    if (token.isEmpty) return null;
    final user = UserModel.fromApiJson(data, token: token);

    await AppDatabase.instance.saveUserSession(user.toLocalMap());
    return user;
  }

  Future<UserModel?> register({
    required String name,
    required String email,
    required String password,
    required String username,
    required String role,
  }) async {
    final response = await http
        .post(
          ApiClient.buildUri(_registerPath),
          headers: const {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
          },
          body: jsonEncode({
            'name': name,
            'email': email,
            'password': password,
            'username': username,
            'role': role,
          }),
        )
        .timeout(_requestTimeout);

    if (response.statusCode != 200 && response.statusCode != 201) {
      return null;
    }

    final data = _decodeBody(response.body);
    final token = _responseToken(data);
    if (token.isEmpty) return null;
    final user = UserModel.fromApiJson(data, token: token);

    await AppDatabase.instance.saveUserSession(user.toLocalMap());
    return user;
  }

  Future<UserModel?> getCurrentUser() async {
    final localSession = await AppDatabase.instance.getUserSession();
    if (localSession == null) return null;

    final token = (localSession['token'] ?? '').toString();
    if (token.isEmpty) return null;

    final response = await http
        .get(
          ApiClient.buildUri(_mePath),
          headers: {
            'Accept': 'application/json',
            'Authorization': ApiClient.buildAuthorizationHeader(token),
          },
        )
        .timeout(_requestTimeout);

    if (response.statusCode != 200) return null;

    final data = _decodeBody(response.body);
    final user = UserModel.fromApiJson(data, token: token);
    await AppDatabase.instance.saveUserSession(user.toLocalMap());
    return user;
  }

  Future<void> logout() async {
    final session = await AppDatabase.instance.getUserSession();
    final token = (session?['token'] ?? '').toString();

    try {
      if (token.isNotEmpty) {
        final response = await http
            .post(
              ApiClient.buildUri(_logoutPath),
              headers: {
                'Accept': 'application/json',
                'Authorization': ApiClient.buildAuthorizationHeader(token),
              },
            )
            .timeout(_requestTimeout);
        if (response.statusCode >= 400) {
          throw Exception('Logout request failed: ${response.statusCode}');
        }
      }
    } finally {
      await AppDatabase.instance.clearUserSession();
    }
  }

  Map<String, dynamic> _decodeBody(String body) {
    final decoded = jsonDecode(body);
    if (decoded is! Map<String, dynamic>) {
      throw const FormatException('The server returned an invalid JSON object.');
    }
    return decoded;
  }

  String _responseToken(Map<String, dynamic> response) {
    final data = response['data'];
    if (data is Map) {
      return (data['token'] ?? response['token'] ?? '').toString();
    }
    return (response['token'] ?? '').toString();
  }
}
