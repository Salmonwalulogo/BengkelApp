class TransactionModel {
  const TransactionModel({
    this.id,
    required this.orderCode,
    required this.customerName,
    required this.amount,
    required this.status,
    this.paymentMethod = 'Cash',
  });

  final int? id;
  final String orderCode;
  final String customerName;
  final double amount;
  final String status;
  final String paymentMethod;

  factory TransactionModel.fromJson(Map<String, dynamic> json) {
    return TransactionModel(
      id: json['id'] is int ? json['id'] : int.tryParse('${json['id']}'),
      orderCode: (json['order_code'] ?? json['invoice_code'] ?? 'TRX-${DateTime.now().millisecondsSinceEpoch}').toString(),
      customerName: (json['customer_name'] ?? json['user_name'] ?? 'Pelanggan').toString(),
      amount: double.tryParse('${json['amount'] ?? json['total_price'] ?? 0}') ?? 0,
      status: (json['status'] ?? 'paid').toString(),
      paymentMethod: (json['payment_method'] ?? 'Cash').toString(),
    );
  }
}
