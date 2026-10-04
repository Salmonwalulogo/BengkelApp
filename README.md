# Bengkel App

Aplikasi Flutter untuk operasional bengkel, dengan role Admin, Kasir, dan Pelanggan. Saat ini project menyediakan target Android dan membutuhkan backend REST API yang dapat diakses melalui HTTPS untuk build rilis.

## Menjalankan aplikasi

Pastikan Flutter SDK terpasang, lalu jalankan:

```powershell
flutter pub get
flutter run --dart-define=API_BASE_URL=http://10.0.2.2:8000/api
```

URL `10.0.2.2` hanya untuk Android Emulator yang mengakses server API di komputer host. Ganti dengan URL server yang dapat diakses perangkat fisik bila diperlukan.

## Demo di HP fisik tanpa kabel USB

HP dan komputer server harus terhubung ke Wi-Fi/LAN yang sama. Jalankan backend agar menerima koneksi dari jaringan lokal (bukan hanya `localhost`), izinkan port API di firewall komputer, lalu gunakan alamat IP lokal komputer, misalnya:

```powershell
flutter build apk --debug --dart-define=API_BASE_URL=http://192.168.1.10:8000/api
```

Ganti `192.168.1.10` dengan alamat IPv4 komputer server. APK debug mengizinkan HTTP untuk demo lokal; pengaturan ini hanya ada pada manifest debug dan tidak berlaku untuk build release. Pastikan backend menerima alamat tersebut dan menyediakan endpoint API yang didokumentasikan di bawah.

APK berada di `build/app/outputs/flutter-apk/app-debug.apk`. Kirim file APK ke HP melalui Drive, email, atau cara berbagi file lain, lalu buka file di HP dan izinkan instalasi dari sumber tersebut jika Android memintanya. Setelah terpasang, demo dapat berjalan tanpa kabel USB selama HP bisa menjangkau server API. Alamat `10.0.2.2` tidak berlaku untuk HP fisik.

Jika HP dan server tidak berada di jaringan yang sama, gunakan server API publik melalui HTTPS. Jangan membuka port backend ke internet hanya untuk demo lokal.

## Kontrak backend

Semua endpoint selain autentikasi menggunakan token `Authorization: Bearer <token>`.

| Method | Endpoint | Kegunaan |
| --- | --- | --- |
| `POST` | `/login` | Masuk dengan email dan password |
| `POST` | `/register` | Membuat akun pelanggan |
| `GET` | `/me` | Membaca profil akun aktif |
| `POST` | `/logout` | Mengakhiri sesi server |
| `GET` | `/services` | Membaca katalog layanan |
| `GET` | `/bookings` | Membaca booking |
| `POST` | `/bookings` | Membuat booking |
| `GET` | `/transactions` | Membaca transaksi |
| `POST` | `/transactions` | Membuat transaksi |

Endpoint daftar harus mengembalikan array JSON langsung atau objek dengan properti `data` berupa array. Login/registrasi harus mengembalikan token dan data user; objek respons boleh berada di dalam properti `data`. Data booking untuk pelanggan harus dibatasi oleh backend berdasarkan token pengguna. Filter di aplikasi hanya untuk tampilan dan bukan pengganti otorisasi server.

## Role dan menu

- **Admin:** Booking, Layanan, Transaksi.
- **Kasir:** Booking, Layanan, Transaksi.
- **Pelanggan:** Katalog Layanan dan Booking Saya.

Rute fitur juga diperiksa berdasarkan role di aplikasi. Backend tetap wajib menerapkan otorisasi di setiap endpoint.

## Build Android

Build debug untuk pengujian:

```powershell
flutter build apk --debug --dart-define=API_BASE_URL=http://10.0.2.2:8000/api
```

Build Play Store menggunakan HTTPS dan signing key milik organisasi. Jangan menyimpan key atau password ke source control. Signing dapat diberikan sebagai environment variable:

```powershell
$env:BENGKEL_KEYSTORE_PATH = "C:\secure\bengkel-release.jks"
$env:BENGKEL_KEYSTORE_PASSWORD = "<ambil-dari-secret-manager>"
$env:BENGKEL_KEY_ALIAS = "<alias>"
$env:BENGKEL_KEY_PASSWORD = "<ambil-dari-secret-manager>"
flutter build appbundle --release --dart-define=API_BASE_URL=https://api.example.com/api
```

Alternatifnya, simpan `storeFile`, `storePassword`, `keyAlias`, dan `keyPassword` di `android/key.properties` yang tidak di-commit. Build release akan berhenti jika signing belum dikonfigurasi, dan koneksi API non-HTTPS ditolak dalam mode release.

Sebelum publikasi:

1. Ganti `applicationId` dan `namespace` contoh `com.example.bengkel_app` dengan ID unik yang dimiliki penerbit.
2. Siapkan backend production, akun/role yang benar, kebijakan privasi, ikon/screenshot toko, dan proses rotasi/backup signing key.
3. Naikkan `version` di `pubspec.yaml` untuk setiap rilis.
4. Jalankan `flutter analyze`, `flutter test`, lalu build AAB release dengan secrets dan URL production melalui CI/CD.

Project saat ini belum dikonfigurasi untuk web hosting; dependensi database lokal `sqflite` digunakan untuk Android. Untuk menjalankan sebagai web app, perlu keputusan arsitektur penyimpanan sesi yang mendukung web dan konfigurasi target web terlebih dahulu.
