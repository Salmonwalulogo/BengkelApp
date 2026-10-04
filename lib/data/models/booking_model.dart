class BookingModel {
  const BookingModel({
    this.id,
    this.userId,
    this.vehicleId,
    this.serviceId,
    required this.customerName,
    required this.serviceName,
    required this.status,
    this.notes = '',
    this.totalPrice = 0,
  });

  final int? id;
  final int? userId;
  final int? vehicleId;
  final int? serviceId;
  final String customerName;
  final String serviceName;
  final String status;
  final String notes;
  final double totalPrice;

  factory BookingModel.fromJson(Map<String, dynamic> json) {
    return BookingModel(
      id: json['id'] is int ? json['id'] : int.tryParse('${json['id']}'),
      userId: json['user_id'] is int ? json['user_id'] : int.tryParse('${json['user_id']}'),
      vehicleId: json['vehicle_id'] is int ? json['vehicle_id'] : int.tryParse('${json['vehicle_id']}'),
      serviceId: json['service_id'] is int ? json['service_id'] : int.tryParse('${json['service_id']}'),
      customerName: (json['customer_name'] ?? json['user_name'] ?? 'Pelanggan').toString(),
      serviceName: (json['service_name'] ?? json['service'] ?? 'Layanan').toString(),
      status: (json['status'] ?? 'pending').toString(),
      notes: (json['notes'] ?? '').toString(),
      totalPrice: double.tryParse('${json['total_price'] ?? json['total'] ?? 0}') ?? 0,
    );
  }
}
