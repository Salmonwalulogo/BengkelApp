import 'dart:convert';

import '../data/local/app_database.dart';
import 'api_client.dart';

class SyncService {
  final AppDatabase _database;

  SyncService({AppDatabase? database}) : _database = database ?? AppDatabase.instance;

  Future<void> enqueue(String entity, String action, Map<String, dynamic> payload) async {
    await _database.enqueueSync(entity, action, payload);
  }

  Future<List<Map<String, dynamic>>> pendingItems() async {
    return _database.getPendingSyncItems();
  }

  Future<void> syncPending() async {
    final items = await pendingItems();

    for (final item in items) {
      final id = item['id'] as int?;
      final entity = (item['entity'] ?? '').toString();
      final action = (item['action'] ?? '').toString();
      final payloadString = (item['payload'] ?? '{}').toString();

      if (id == null || entity.isEmpty) continue;

      try {
        final payload = jsonDecode(payloadString) as Map<String, dynamic>? ?? <String, dynamic>{};

        if (entity == 'booking' && action == 'create') {
          await ApiClient.postJson('/bookings', payload);
        } else if (entity == 'transaction' && action == 'create') {
          await ApiClient.postJson('/transactions', payload);
        }

        await _database.updateSyncStatus(id, 'synced');
      } catch (_) {
        await _database.updateSyncStatus(id, 'pending');
      }
    }
  }
}
