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

class WaktuIstitahat : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var tidurEditText: EditText
    private lateinit var bangunEditText: EditText
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waktu_istitahat)

        // Inisialisasi Firebase Firestore
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        // Mendapatkan referensi ke komponen-komponen dalam layout
        tidurEditText = findViewById(R.id.namakegiatan)
        bangunEditText = findViewById(R.id.durasilatihan)
        val textViewTanggal = findViewById<TextView>(R.id.tanggal)
        val simpanButton = findViewById<Button>(R.id.button2)

        // Mendapatkan tanggal hari ini dalam format yang diinginkan (misalnya: 5 Juli 2024)
        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        val tanggalSekarang = dateFormat.format(Date())
        textViewTanggal.text = tanggalSekarang

        // Menyimpan data ke Firestore saat tombol "Simpan" ditekan
        simpanButton.setOnClickListener {
            val tidur = tidurEditText.text.toString().trim()
            val bangun = bangunEditText.text.toString().trim()

            // Validasi input
            if (tidur.isEmpty()) {
                tidurEditText.error = "Waktu tidur tidak boleh kosong"
                return@setOnClickListener
            }

            if (bangun.isEmpty()) {
                bangunEditText.error = "Waktu bangun tidak boleh kosong"
                return@setOnClickListener
            }

            // Membuat objek data untuk disimpan ke Firestore
            val data = hashMapOf(
                "waktu_tidur" to tidur,
                "waktu_bangun" to bangun
            )

            // Menyimpan data ke Firestore dengan collection "waktu_istirahat"
            val user = auth.currentUser
            user?.let {
                val userId = it.uid
                db.collection("users").document(userId).collection("waktu_istirahat")
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
        tidurEditText.text.clear()
        bangunEditText.text.clear()
    }
}
