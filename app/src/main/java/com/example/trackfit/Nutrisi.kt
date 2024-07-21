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

class Nutrisi : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var kalsiumEditText: EditText
    private lateinit var proteinEditText: EditText
    private lateinit var vitaminEditText: EditText
    private lateinit var tambahanEditText: EditText
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutrisi)

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        // Get references to components in the layout
        kalsiumEditText = findViewById(R.id.namakegiatan)
        proteinEditText = findViewById(R.id.durasilatihan)
        vitaminEditText = findViewById(R.id.kkal)
        tambahanEditText = findViewById(R.id.tambahannya)
        val textViewTanggal = findViewById<TextView>(R.id.tanggal)
        val simpanButton = findViewById<Button>(R.id.button2)

        // Get today's date in the desired format (e.g., 5 Juli 2024)
        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        val tanggalSekarang = dateFormat.format(Date())
        textViewTanggal.text = tanggalSekarang

        // Save data to Firestore when "Simpan" button is clicked
        simpanButton.setOnClickListener {
            val kalsium = kalsiumEditText.text.toString().trim()
            val protein = proteinEditText.text.toString().trim()
            val vitamin = vitaminEditText.text.toString().trim()
            val tambahan = tambahanEditText.text.toString().trim()

            // Validate input
            if (kalsium.isEmpty()) {
                kalsiumEditText.error = "Kalsium tidak boleh kosong"
                return@setOnClickListener
            }

            if (protein.isEmpty()) {
                proteinEditText.error = "Protein tidak boleh kosong"
                return@setOnClickListener
            }

            if (vitamin.isEmpty()) {
                vitaminEditText.error = "Vitamin tidak boleh kosong"
                return@setOnClickListener
            }

            if (tambahan.isEmpty()) {
                tambahanEditText.error = "Tambahan tidak boleh kosong"
                return@setOnClickListener
            }

            // Create data object to be saved to Firestore
            val data = hashMapOf(
                "kalsium" to kalsium,
                "protein" to protein,
                "vitamin" to vitamin,
                "tambahan" to tambahan,
                "tanggal" to tanggalSekarang // Include date as part of nutrition data
            )

            // Save data to Firestore under "program_latihan" collection
            val user = auth.currentUser
            user?.let {
                val userId = it.uid
                db.collection("users").document(userId).collection("nutrisi")
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

    // Clear input fields after successful save
    private fun clearFields() {
        kalsiumEditText.text.clear()
        proteinEditText.text.clear()
        vitaminEditText.text.clear()
        tambahanEditText.text.clear()
    }
}
