package com.example.trackfit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ProgramLatihan1 : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var namaKegiatanEditText: EditText
    private lateinit var durasiEditText: EditText
    private lateinit var kkalEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_program_latihan1)

        // Inisialisasi Firebase Firestore dan FirebaseAuth
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Mendapatkan referensi ke komponen-komponen dalam layout
        namaKegiatanEditText = findViewById(R.id.namakegiatan)
        durasiEditText = findViewById(R.id.durasilatihan)
        kkalEditText = findViewById(R.id.kkal)

        val textViewTanggal = findViewById<TextView>(R.id.tanggal)
        val simpanButton = findViewById<Button>(R.id.button2)

        // Mendapatkan tanggal hari ini dalam format yang diinginkan (misalnya: 5 Juli 2024)
        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        val tanggalSekarang = dateFormat.format(Date())
        textViewTanggal.text = tanggalSekarang

        // Menyimpan data ke Firestore saat tombol "Simpan" ditekan
        simpanButton.setOnClickListener {
            val namaKegiatan = namaKegiatanEditText.text.toString().trim()
            val durasiStr = durasiEditText.text.toString().trim()
            val kkalStr = kkalEditText.text.toString().trim()

            // Validasi input
            if (namaKegiatan.isEmpty()) {
                namaKegiatanEditText.error = "Nama kegiatan tidak boleh kosong"
                return@setOnClickListener
            }

            val durasi = durasiStr.toIntOrNull()
            if (durasi == null || durasi <= 0) {
                durasiEditText.error = "Durasi harus diisi dengan angka positif"
                return@setOnClickListener
            }

            val kkal = kkalStr.toIntOrNull()
            if (kkal == null || kkal <= 0) {
                kkalEditText.error = "Jumlah kalori harus diisi dengan angka positif"
                return@setOnClickListener
            }

            // Membuat objek data untuk disimpan ke Firestore
            val data = hashMapOf(
                "nama_kegiatan" to namaKegiatan,
                "durasi" to durasi.toString(),
                "kkal" to kkal,
                "tanggal" to tanggalSekarang
            )

            // Menyimpan data ke subkoleksi "program_latihan" dalam dokumen pengguna
            val user = auth.currentUser
            user?.let {
                val userId = it.uid
                db.collection("users").document(userId).collection("program_latihan")
                    .add(data)
                    .addOnSuccessListener { documentReference ->
                        Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                        clearFields() // Membersihkan input setelah berhasil disimpan
                    }
                    .addOnFailureListener { e ->
                        // Gagal menyimpan
                        Toast.makeText(this, "Gagal menyimpan data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } ?: run {
                Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Membersihkan input setelah berhasil disimpan
    private fun clearFields() {
        namaKegiatanEditText.text.clear()
        durasiEditText.text.clear()
        kkalEditText.text.clear()
    }
}
