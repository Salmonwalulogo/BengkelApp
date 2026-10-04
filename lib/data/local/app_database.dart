import 'dart:convert';

import 'package:path/path.dart';
import 'package:sqflite/sqflite.dart';

import '../../core/app_constants.dart';

class AppDatabase {
  AppDatabase._();
  static final AppDatabase instance = AppDatabase._();

  Database? _database;

  Future<Database> get database async {
    if (_database != null) return _database!;
    _database = await _initDatabase();
    return _database!;
  }

  Future<Database> _initDatabase() async {
    final databasesPath = await getDatabasesPath();
    final path = join(databasesPath, AppConstants.localDbName);

    return openDatabase(
      path,
      version: 1,
      onCreate: _onCreate,
    );
  }

  Future<void> _onCreate(Database db, int version) async {
    await db.execute('''
      CREATE TABLE IF NOT EXISTS user_session (
        id INTEGER PRIMARY KEY,
        name TEXT,
        email TEXT,
        username TEXT,
        role TEXT,
        no_hp TEXT,
        photo TEXT,
        token TEXT
      )
    ''');

    await db.execute('''
      CREATE TABLE IF NOT EXISTS sync_queue (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        entity TEXT NOT NULL,
        action TEXT NOT NULL,
        payload TEXT NOT NULL,
        status TEXT NOT NULL DEFAULT 'pending',
        created_at TEXT NOT NULL DEFAULT (datetime('now'))
      )
    ''');
  }

  Future<void> saveUserSession(Map<String, dynamic> userMap) async {
    final db = await database;
    await db.delete('user_session');
    await db.insert('user_session', userMap, conflictAlgorithm: ConflictAlgorithm.replace);
  }

  Future<Map<String, dynamic>?> getUserSession() async {
    final db = await database;
    final rows = await db.query('user_session', limit: 1);
    if (rows.isEmpty) return null;
    return rows.first;
  }

  Future<void> clearUserSession() async {
    final db = await database;
    await db.delete('user_session');
  }

  Future<void> enqueueSync(String entity, String action, Map<String, dynamic> payload) async {
    final db = await database;
    await db.insert('sync_queue', {
      'entity': entity,
      'action': action,
      'payload': payloadToJson(payload),
      'status': 'pending',
      'created_at': DateTime.now().toIso8601String(),
    });
  }

  Future<List<Map<String, dynamic>>> getPendingSyncItems() async {
    final db = await database;
    return db.query('sync_queue', where: 'status = ?', whereArgs: ['pending']);
  }

  Future<void> updateSyncStatus(int id, String status) async {
    final db = await database;
    await db.update('sync_queue', {'status': status}, where: 'id = ?', whereArgs: [id]);
  }

  String payloadToJson(Map<String, dynamic> payload) {
    return jsonEncode(payload);
  }
}
