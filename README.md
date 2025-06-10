# NewsApp

## Deskripsi Singkat
**NewsApp** adalah aplikasi Android yang dirancang untuk memberikan pengalaman membaca berita yang mudah, cepat, dan personal. Dengan desain antarmuka modern dan fitur-fitur interaktif, NewsApp memudahkan pengguna dalam mengakses berita terkini dari berbagai belahan dunia, mencari topik sesuai minat, serta menyimpan Berita favorit untuk dibaca kapan saja. Aplikasi ini juga mendukung pengaturan tema light/dark sesuai preferensi pengguna untuk kenyamanan maksimal.

NewsApp memanfaatkan layanan **NewsAPI** untuk memperoleh berita-berita aktual secara real-time. Salah satu contoh endpoint yang digunakan untuk mengambil berita populer bertema “apple” adalah:
```
https://newsapi.org/v2/everything?q=apple&from=2025-06-09&to=2025-06-09&sortBy=popularity&apiKey=YOUR_API_KEY
```
API ini memungkinkan aplikasi menampilkan berita yang relevan, terfilter berdasarkan tanggal dan tingkat popularitas.

## Fitur-Fitur Unggulan
- **Berita Terkini Otomatis:** Selalu ter-update dengan berita terbaru dan terpopuler.
- **Pencarian Cerdas:** Temukan berita sesuai kata kunci, topik, atau kategori yang diminati.
- **Favoritkan Berita:** Simpan berita penting ke daftar favorit untuk dibaca kembali dengan mudah.
- **Tema Adaptif:** Pilihan mode terang (light) atau gelap (dark) yang dapat diubah kapan saja demi kenyamanan mata.
- **Refresh & Penanganan Error:** Fitur refresh dan notifikasi jika terjadi kendala saat pemuatan data.

## Panduan Penggunaan
1. **Install aplikasi** melalui perangkat Android Anda.
2. **Buka aplikasi** untuk langsung melihat kumpulan berita utama hari ini.
3. **Telusuri atau cari berita** berdasarkan minat atau kata kunci.
4. **Klik berita** untuk membaca detail lengkap.
5. **Simpan ke favorit** dengan menekan ikon favorit pada artikel.
6. **Ubah tema aplikasi** melalui menu pengaturan sesuai preferensi Anda.

## Implementasi Teknis
- **Bahasa Pemrograman:** Java & Kotlin (mengikuti praktik terbaik pengembangan Android modern).
- **Networking:** Menggunakan Retrofit untuk komunikasi dengan NewsAPI.
- **UI/UX:** RecyclerView untuk menampilkan daftar berita, didukung Fragment dan Navigation Component untuk navigasi yang mulus.
- **Penyimpanan Lokal:** Room Database untuk menyimpan artikel favorit, SharedPreferences untuk pengaturan tema.
- **Threading:** Pemrosesan background menggunakan Executor agar aplikasi tetap responsif.
- **Penanganan Error:** Terdapat tombol refresh dan notifikasi jika terjadi kegagalan dalam memuat data.
- **Tema Dinamis:** Mendukung penggantian tema secara real-time (light/dark mode).

## Manfaat & Pembelajaran
Pengembangan aplikasi ini bertujuan untuk:
- Mempraktikkan pemanfaatan **REST API** dalam aplikasi mobile.
- Mengimplementasikan **arsitektur aplikasi Android** yang efisien dan terstruktur.
- Mengasah keterampilan dalam **pengelolaan data lokal** (Room, SharedPreferences).
- Meningkatkan kemampuan dalam mendesain **UI/UX** yang user-friendly dan adaptif.

---

> Proyek ini dibuat sebagai bagian dari tugas akhir praktikum **Pemrograman Mobile**. Dengan aplikasi ini, diharapkan saya dapat memahami praktik pengembangan aplikasi Android berbasis API dan data lokal secara komprehensif, sekaligus menerapkan konsep-konsep modern dalam pemrograman mobile.
