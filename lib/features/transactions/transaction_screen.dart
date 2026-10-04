import 'package:flutter/material.dart';

import '../../data/models/transaction_model.dart';
import '../../data/models/user_model.dart';
import '../../services/bengkel_service.dart';

class TransactionScreen extends StatefulWidget {
  const TransactionScreen({super.key, this.user});

  final UserModel? user;

  @override
  State<TransactionScreen> createState() => _TransactionScreenState();
}

class _TransactionScreenState extends State<TransactionScreen> {
  late Future<List<TransactionModel>> _transactionsFuture;

  @override
  void initState() {
    super.initState();
    _transactionsFuture = _loadTransactions();
  }

  Future<List<TransactionModel>> _loadTransactions() async {
    return BengkelService().fetchTransactions();
  }

  void _retryLoading() {
    setState(() => _transactionsFuture = _loadTransactions());
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Transaksi')),
      body: FutureBuilder<List<TransactionModel>>(
        future: _transactionsFuture,
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
                    const Text('Transaksi gagal dimuat. Periksa koneksi dan server API.'),
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

          final transactions = snapshot.data ?? const <TransactionModel>[];

          if (transactions.isEmpty) {
            return const Center(child: Text('Belum ada transaksi.'));
          }

          return ListView.separated(
            padding: const EdgeInsets.all(16),
            itemCount: transactions.length,
            separatorBuilder: (_, _) => const SizedBox(height: 12),
            itemBuilder: (context, index) {
              final transaction = transactions[index];
              final statusColor = transaction.status.toLowerCase() == 'paid' || transaction.status.toLowerCase() == 'completed'
                  ? Colors.green
                  : Colors.orange;

              return Card(
                child: ListTile(
                  leading: CircleAvatar(
                    backgroundColor: Theme.of(context).colorScheme.secondaryContainer,
                    child: const Icon(Icons.receipt_long_rounded),
                  ),
                  title: Text(transaction.orderCode),
                  subtitle: Text(transaction.customerName),
                  trailing: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    crossAxisAlignment: CrossAxisAlignment.end,
                    children: [
                      Text(
                        'Rp ${transaction.amount.toStringAsFixed(0).replaceAllMapped(RegExp(r'\B(?=(\d{3})+(?!\d))'), (match) => '.')} ',
                        style: const TextStyle(fontWeight: FontWeight.bold),
                      ),
                      const SizedBox(height: 6),
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                        decoration: BoxDecoration(
                          color: statusColor.withValues(alpha: 0.15),
                          borderRadius: BorderRadius.circular(999),
                        ),
                        child: Text(
                          transaction.status,
                          style: TextStyle(color: statusColor, fontWeight: FontWeight.w600),
                        ),
                      ),
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
