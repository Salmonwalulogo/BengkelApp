import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;

import '../core/app_constants.dart';
import '../data/local/app_database.dart';

class ApiClient {
  static String buildAuthorizationHeader(String token) => 'Bearer $token';

  static Uri buildUri(String path) {
    final baseUrl = AppConstants.apiBaseUrl.trim().replaceFirst(RegExp(r'/+$'), '');
    final uri = Uri.tryParse('$baseUrl/${path.replaceFirst(RegExp(r'^/+'), '')}');
    if (baseUrl.isEmpty || uri == null || !uri.hasScheme || uri.host.isEmpty) {
      throw StateError(
        'API_BASE_URL is missing or invalid. Configure it with --dart-define=API_BASE_URL=https://your-api.example/api.',
      );
    }
    if (kReleaseMode && uri.scheme != 'https') {
      throw StateError('API_BASE_URL must use HTTPS in release builds.');
    }
    return uri;
  }

  static Future<Map<String, String>> _headers({bool withAuth = true}) async {
    final headers = <String, String>{
      'Accept': 'application/json',
      'Content-Type': 'application/json',
    };

    if (!withAuth) return headers;

    final session = await AppDatabase.instance.getUserSession();
    final token = (session?['token'] ?? '').toString();
    if (token.isNotEmpty) {
      headers['Authorization'] = buildAuthorizationHeader(token);
    }
    return headers;
  }

  static Future<List<dynamic>> getList(String path) async {
    final response = await http
        .get(buildUri(path), headers: await _headers())
        .timeout(const Duration(seconds: 20));

    if (response.statusCode >= 400) {
      throw Exception('Request failed: ${response.statusCode}');
    }

    final decoded = _decodeJson(response.body);
    if (decoded is Map<String, dynamic> && decoded['data'] is List) {
      return decoded['data'] as List<dynamic>;
    }
    if (decoded is List) return decoded;
    throw const FormatException('Expected a JSON list response.');
  }

  static Future<Map<String, dynamic>> postJson(
    String path,
    Map<String, dynamic> payload,
  ) async {
    final response = await http
        .post(
          buildUri(path),
          headers: await _headers(),
          body: jsonEncode(payload),
        )
        .timeout(const Duration(seconds: 20));

    if (response.statusCode >= 400) {
      throw Exception('Request failed: ${response.statusCode}');
    }

    final decoded = _decodeJson(response.body);
    if (decoded is Map<String, dynamic>) return decoded;
    throw const FormatException('Expected a JSON object response.');
  }

  static Object? _decodeJson(String body) {
    try {
      return jsonDecode(body);
    } on FormatException {
      throw const FormatException('The server returned an invalid JSON response.');
    }
  }
}
