import '../data/models/booking_model.dart';
import '../data/models/service_model.dart';
import '../data/models/transaction_model.dart';
import 'api_client.dart';

class BengkelService {
  Future<List<ServiceModel>> fetchServices() async {
    final items = await ApiClient.getList('/services');
    return items.map((e) => ServiceModel.fromJson(Map<String, dynamic>.from(e as Map))).toList();
  }

  Future<List<BookingModel>> fetchBookings() async {
    final items = await ApiClient.getList('/bookings');
    return items.map((e) => BookingModel.fromJson(Map<String, dynamic>.from(e as Map))).toList();
  }

  Future<List<TransactionModel>> fetchTransactions() async {
    final items = await ApiClient.getList('/transactions');
    return items.map((e) => TransactionModel.fromJson(Map<String, dynamic>.from(e as Map))).toList();
  }

  Future<BookingModel> createBooking({
    required int vehicleId,
    required int serviceId,
    required String bookingDate,
    required String complaint,
  }) async {
    final response = await ApiClient.postJson('/bookings', {
      'vehicle_id': vehicleId,
      'service_id': serviceId,
      'booking_date': bookingDate,
      'complaint': complaint,
      'booking_time': '09:00',
    });

    final data = response['data'] as Map<String, dynamic>? ?? <String, dynamic>{};
    return BookingModel.fromJson(data);
  }

  Future<TransactionModel> createTransaction({
    required List<Map<String, dynamic>> items,
    required double totalPayment,
    required double paidAmount,
    required String paymentMethod,
    String? clientUuid,
  }) async {
    final response = await ApiClient.postJson('/transactions', {
      'items': items,
      'subtotal': totalPayment,
      'total_payment': totalPayment,
      'paid_amount': paidAmount,
      'payment_method': paymentMethod,
      'client_uuid': clientUuid ?? DateTime.now().millisecondsSinceEpoch.toString(),
    });

    final data = response['data'] as Map<String, dynamic>? ?? <String, dynamic>{};
    return TransactionModel.fromJson(data);
  }
}
