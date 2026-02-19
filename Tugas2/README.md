# News Feed Simulator

## Deskripsi

Ini adalah aplikasi Android sederhana yang dibuat menggunakan Kotlin dan Jetpack Compose. Aplikasi ini mensimulasikan feed berita secara *real-time* dan mendemonstrasikan penggunaan berbagai konsep dari Kotlin Coroutines dan Flow.

## Cara Menjalankan Aplikasi

1.  **Buka Proyek**: Buka proyek ini menggunakan versi terbaru Android Studio.
2.  **Sinkronkan Gradle**: Tunggu hingga Android Studio selesai melakukan sinkronisasi dengan file Gradle.
3.  **Pilih Perangkat**: Pilih emulator Android yang tersedia atau hubungkan perangkat fisik.
4.  **Jalankan Aplikasi**: Tekan tombol **Run** (atau gunakan pintasan `Shift` + `F10`) untuk membangun dan menjalankan aplikasi di perangkat yang dipilih.

## Fitur Utama

Aplikasi ini mengimplementasikan beberapa fitur modern dalam pengembangan Android:

1.  **Simulasi Berita Real-time (Flow)**
    -   Sebuah `Flow` di dalam `NewsViewModel` secara otomatis menghasilkan data berita baru setiap 2 detik, mensimulasikan feed yang terus diperbarui.

2.  **Filter Berita Berdasarkan Kategori**
    -   Pengguna dapat memfilter berita yang ditampilkan berdasarkan kategori: "All", "Sports", "Technology", atau "Politics".
    -   Ini dicapai dengan menggabungkan (`combine`) flow berita dengan `StateFlow` yang menyimpan kategori yang sedang dipilih.

3.  **Transformasi Data (Flow Operator)**
    -   Operator `.map()` digunakan untuk mengubah data `News` mentah menjadi format `String` yang siap ditampilkan di UI.

4.  **Pelacak Berita yang Dibaca (StateFlow)**
    -   Sebuah `StateFlow` digunakan untuk menyimpan dan memperbarui jumlah total berita yang telah diklik (dianggap "dibaca"). UI akan secara otomatis diperbarui setiap kali nilai ini berubah.

5.  **Pengambilan Detail Asinkron (Coroutines)**
    -   Ketika pengguna mengklik sebuah item berita, sebuah *coroutine* baru diluncurkan menggunakan `viewModelScope.launch`.
    -   Ini mensimulasikan panggilan jaringan untuk mengambil detail berita dengan `delay` tanpa memblokir Main Thread, sehingga UI tetap responsif.
