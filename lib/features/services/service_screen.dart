import 'package:flutter/material.dart';

import '../../data/models/service_model.dart';
import '../../data/models/user_model.dart';
import '../../services/bengkel_service.dart';

class ServiceScreen extends StatefulWidget {
  const ServiceScreen({super.key, this.user});

  final UserModel? user;

  @override
  State<ServiceScreen> createState() => _ServiceScreenState();
}

class _ServiceScreenState extends State<ServiceScreen> {
  late Future<List<ServiceModel>> _servicesFuture;

  @override
  void initState() {
    super.initState();
    _servicesFuture = _loadServices();
  }

  Future<List<ServiceModel>> _loadServices() async {
    return BengkelService().fetchServices();
  }

  void _retryLoading() {
    setState(() => _servicesFuture = _loadServices());
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Layanan'),
      ),
      body: FutureBuilder<List<ServiceModel>>(
        future: _servicesFuture,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }

          if (snapshot.hasError) {
            return Center(
              child: Padding(
                padding: const EdgeInsets.all(24),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const Text('Layanan gagal dimuat. Periksa koneksi dan server API.'),
                    const SizedBox(height: 12),
                    FilledButton(
                      onPressed: _retryLoading,
                      child: const Text('Coba lagi'),
                    ),
                  ],
                ),
              ),
            );
          }

          final services = snapshot.data ?? const <ServiceModel>[];

          return ListView.separated(
            padding: const EdgeInsets.all(16),
            itemCount: services.length,
            separatorBuilder: (_, _) => const SizedBox(height: 12),
            itemBuilder: (context, index) {
              final service = services[index];
              return Card(
                child: ListTile(
                  leading: CircleAvatar(
                    backgroundColor: Theme.of(context).colorScheme.primaryContainer,
                    child: const Icon(Icons.handyman_rounded),
                  ),
                  title: Text(service.name),
                  subtitle: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const SizedBox(height: 6),
                      Text(service.category),
                      if (service.description.isNotEmpty) ...[
                        const SizedBox(height: 4),
                        Text(service.description, maxLines: 2, overflow: TextOverflow.ellipsis),
                      ],
                    ],
                  ),
                  trailing: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    crossAxisAlignment: CrossAxisAlignment.end,
                    children: [
                      Text(
                        'Rp ${service.price.toStringAsFixed(0).replaceAllMapped(RegExp(r'\B(?=(\d{3})+(?!\d))'), (match) => '.')} ',
                        style: const TextStyle(fontWeight: FontWeight.bold),
                      ),
                      Text('${service.durationMinutes} menit'),
                    ],
                  ),
                ),
              );
            },
          );
        },
      ),
    );
  }
}
