class ServiceModel {
  const ServiceModel({
    this.id,
    required this.name,
    required this.price,
    this.category = 'Umum',
    this.durationMinutes = 60,
    this.description = '',
  });

  final int? id;
  final String name;
  final double price;
  final String category;
  final int durationMinutes;
  final String description;

  factory ServiceModel.fromJson(Map<String, dynamic> json) {
    return ServiceModel(
      id: json['id'] is int ? json['id'] : int.tryParse('${json['id']}'),
      name: (json['name'] ?? json['service_name'] ?? 'Layanan').toString(),
      price: double.tryParse('${json['price'] ?? 0}') ?? 0,
      category: (json['category'] ?? 'Umum').toString(),
      durationMinutes: int.tryParse('${json['duration_minutes'] ?? json['duration'] ?? 60}') ?? 60,
      description: (json['description'] ?? '').toString(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'price': price,
      'category': category,
      'duration_minutes': durationMinutes,
      'description': description,
    };
  }
}
