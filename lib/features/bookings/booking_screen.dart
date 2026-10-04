import 'package:flutter/material.dart';

import '../../data/models/booking_model.dart';
import '../../data/models/user_model.dart';
import '../../services/bengkel_service.dart';

class BookingScreen extends StatefulWidget {
  const BookingScreen({super.key, this.user});

  final UserModel? user;

  @override
  State<BookingScreen> createState() => _BookingScreenState();
}

class _BookingScreenState extends State<BookingScreen> {
  late Future<List<BookingModel>> _bookingsFuture;

  @override
  void initState() {
    super.initState();
    _bookingsFuture = _loadBookings();
  }

  Future<List<BookingModel>> _loadBookings() async {
    final bookings = await BengkelService().fetchBookings();
    if (widget.user?.isCustomer != true) return bookings;

    final userId = widget.user?.id;
    if (userId == null) return const [];
    return bookings.where((booking) => booking.userId == userId).toList();
  }

  void _retryLoading() {
    setState(() => _bookingsFuture = _loadBookings());
  }

  Future<void> _createBooking() async {
    final complaintController = TextEditingController();
    final bookingDateController = TextEditingController(
      text: DateTime.now().toIso8601String().split('T').first,
    );
    final serviceIdController = TextEditingController();
    final vehicleIdController = TextEditingController();

    final shouldCreate = await showDialog<bool>(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Buat Booking Baru'),
          content: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                TextField(
                  controller: vehicleIdController,
                  decoration: const InputDecoration(labelText: 'ID Motor'),
                  keyboardType: TextInputType.number,
                ),
                const SizedBox(height: 12),
                TextField(
                  controller: serviceIdController,
                  decoration: const InputDecoration(labelText: 'ID Layanan'),
                  keyboardType: TextInputType.number,
                ),
                const SizedBox(height: 12),
                TextField(
                  controller: bookingDateController,
                  decoration: const InputDecoration(labelText: 'Tanggal Booking (YYYY-MM-DD)'),
                ),
                const SizedBox(height: 12),
                TextField(
                  controller: complaintController,
                  decoration: const InputDecoration(labelText: 'Keluhan'),
                  minLines: 2,
                  maxLines: 3,
                ),
              ],
            ),
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context, false),
              child: const Text('Batal'),
            ),
            FilledButton(
              onPressed: () => Navigator.pop(context, true),
              child: const Text('Simpan'),
            ),
          ],
        );
      },
    );

    final vehicleId = int.tryParse(vehicleIdController.text);
    final serviceId = int.tryParse(serviceIdController.text);
    final bookingDate = bookingDateController.text.trim();
    final complaint = complaintController.text.trim();
    complaintController.dispose();
    bookingDateController.dispose();
    serviceIdController.dispose();
    vehicleIdController.dispose();

    if (shouldCreate != true || !mounted) return;
    if (vehicleId == null ||
        vehicleId <= 0 ||
        serviceId == null ||
        serviceId <= 0 ||
        DateTime.tryParse(bookingDate) == null ||
        complaint.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Isi ID motor, ID layanan, tanggal, dan keluhan dengan benar.'),
        ),
      );
      return;
    }

    try {
      await BengkelService().createBooking(
        vehicleId: vehicleId,
        serviceId: serviceId,
        bookingDate: bookingDate,
        complaint: complaint,
      );

      if (!mounted) return;
      _retryLoading();
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Booking berhasil dibuat.')),
      );
    } catch (_) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Booking gagal dibuat. Periksa koneksi dan coba lagi.')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(widget.user?.isCustomer == true ? 'Booking Saya' : 'Booking')),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: _createBooking,
        icon: const Icon(Icons.add_rounded),
        label: const Text('Booking baru'),
      ),
      body: FutureBuilder<List<BookingModel>>(
        future: _bookingsFuture,
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
                    const Text('Booking gagal dimuat. Periksa koneksi dan server API.'),
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

          final bookings = snapshot.data ?? const <BookingModel>[];
          if (bookings.isEmpty) {
            return const Center(child: Text('Belum ada booking.'));
          }

          return ListView.separated(
            padding: const EdgeInsets.all(16),
            itemCount: bookings.length,
            separatorBuilder: (_, _) => const SizedBox(height: 12),
            itemBuilder: (context, index) {
              final booking = bookings[index];
              final statusColor = switch (booking.status.toLowerCase()) {
                'pending' => Colors.orange,
                'confirmed' => Colors.green,
                _ => Colors.blue,
              };

              return Card(
                child: ListTile(
                  leading: CircleAvatar(
                    backgroundColor: Theme.of(context).colorScheme.primaryContainer,
                    child: const Icon(Icons.calendar_month_rounded),
                  ),
                  title: Text(booking.serviceName),
                  subtitle: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const SizedBox(height: 4),
                      Text(booking.customerName),
                      if (booking.notes.isNotEmpty)
                        Text(
                          booking.notes,
                          maxLines: 2,
                          overflow: TextOverflow.ellipsis,
                        ),
                    ],
                  ),
                  trailing: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    crossAxisAlignment: CrossAxisAlignment.end,
                    children: [
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                        decoration: BoxDecoration(
                          color: statusColor.withValues(alpha: 0.15),
                          borderRadius: BorderRadius.circular(999),
                        ),
                        child: Text(
                          booking.status,
                          style: TextStyle(color: statusColor, fontWeight: FontWeight.w600),
                        ),
                      ),
                      const SizedBox(height: 8),
                      Text(_formatPrice(booking.totalPrice)),
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

  String _formatPrice(double price) {
    final formatted = price
        .toStringAsFixed(0)
        .replaceAllMapped(RegExp(r'\B(?=(\d{3})+(?!\d))'), (_) => '.');
    return 'Rp $formatted';
  }
}
