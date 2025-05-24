# Integrasi Transaksi - Omsetku

## Fitur yang Ditambahkan

1. **Model Data**:
   - `TaxSettings`: Model untuk pengaturan pajak
   - `CartItem`: Model untuk item yang ditambahkan ke keranjang

2. **ViewModel**:
   - `TaxViewModel`: Mengelola pengaturan pajak (aktif/tidak dan persentase)
   - `CartViewModel`: Mengelola keranjang belanja dan proses checkout

3. **Repository**:
   - Penambahan fungsi pada `FirestoreRepository` untuk pengelolaan pajak

4. **Integrasi UI**:
   - `CashierScreen`: Terintegrasi dengan CartViewModel untuk menambahkan produk ke keranjang
   - `TransactionDetailScreen`: Menampilkan daftar produk dari keranjang dan melakukan checkout

## Alur Kerja Transaksi

1. **Menambahkan Produk ke Keranjang**:
   - User memilih produk di `CashierScreen`
   - Mengklik tombol + pada produk menambahkannya ke keranjang
   - Perubahan quantity pada produk disinkronkan dengan CartViewModel

2. **Memproses Transaksi**:
   - Tombol "Proses Transaksi" muncul ketika ada item di keranjang
   - Navigasi ke `TransactionDetailScreen` ketika tombol diklik

3. **Detail Transaksi dan Checkout**:
   - `TransactionDetailScreen` menampilkan daftar produk dari keranjang
   - Subtotal dihitung berdasarkan jumlah item x harga
   - Pajak dihitung berdasarkan pengaturan pajak (TaxSettings)
   - Tombol "Konfirmasi" menyimpan transaksi ke Firestore sebagai pemasukan
   - Transaksi yang disimpan otomatis mendapatkan deskripsi "Penjualan {nama produk}"

4. **Pengaturan Pajak**:
   - Pengguna dapat mengaktifkan/menonaktifkan pajak di `TaxSettingsScreen`
   - Pengguna dapat mengatur persentase pajak (mis. 10%)
   - Pengaturan pajak mempengaruhi perhitungan di `TransactionDetailScreen`

## Struktur Database Firestore

### Collection `taxSettings`
- Document ID: userID
- Fields:
  - `enabled`: Boolean (pajak aktif/tidak)
  - `rate`: Number (persentase pajak)
  - `userId`: String (ID user)
  - `updatedAt`: Timestamp

### Collection `transactions`
- Document ID: Auto-generated
- Fields:
  - `id`: String (ID transaksi)
  - `userId`: String (ID user)
  - `type`: String (INCOME/EXPENSE)
  - `amount`: Number (total transaksi)
  - `date`: Timestamp
  - `category`: String (kategori transaksi)
  - `description`: String (deskripsi transaksi)
  - `createdAt`: Timestamp 