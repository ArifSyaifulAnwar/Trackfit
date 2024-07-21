package com.example.trackfit
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore

class LinkYT : AppCompatActivity() {

    private lateinit var link1EditText: TextInputEditText
    private lateinit var link2EditText: TextInputEditText
    private lateinit var link3EditText: TextInputEditText
    private lateinit var link4EditText: TextInputEditText
    private lateinit var saveButton: Button
    private lateinit var link1Layout: TextInputLayout
    private lateinit var link2Layout: TextInputLayout
    private lateinit var link3Layout: TextInputLayout
    private lateinit var link4Layout: TextInputLayout

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_yt)

        link1EditText = findViewById(R.id.linknya)
        link2EditText = findViewById(R.id.linknya1)
        link3EditText = findViewById(R.id.linknya2)
        link4EditText = findViewById(R.id.linknya3)
        saveButton = findViewById(R.id.button2)
        link1Layout = findViewById(R.id.link)
        link2Layout = findViewById(R.id.link1)
        link3Layout = findViewById(R.id.link2)
        link4Layout = findViewById(R.id.link3)

        saveButton.setOnClickListener {
            saveLinksToFirestore()
        }
    }

    private fun saveLinksToFirestore() {
        val link1 = link1EditText.text.toString().trim()
        val link2 = link2EditText.text.toString().trim()
        val link3 = link3EditText.text.toString().trim()
        val link4 = link4EditText.text.toString().trim()

        // Validasi minimal satu link yang diisi
        if (link1.isEmpty() && link2.isEmpty() && link3.isEmpty() && link4.isEmpty()) {
            link1Layout.error = "Minimal satu link harus diisi"
            return
        }

        // Dapatkan referensi koleksi 'links' di Firestore
        val linksCollection = firestore.collection("Links")

        // Buat HashMap untuk menyimpan data link
        val data = hashMapOf<String, String>()

        // Tambahkan link yang diisi ke dalam HashMap
        if (link1.isNotEmpty()) {
            data["link1"] = link1
        }
        if (link2.isNotEmpty()) {
            data["link2"] = link2
        }
        if (link3.isNotEmpty()) {
            data["link3"] = link3
        }
        if (link4.isNotEmpty()) {
            data["link4"] = link4
        }

        // Menyimpan data ke Firestore
        linksCollection.document("links_data").set(data)
            .addOnSuccessListener {

                link1EditText.text = null
                link2EditText.text = null
                link3EditText.text = null
                link4EditText.text = null
                link1Layout.error = null
                link2Layout.error = null
                link3Layout.error = null
                link4Layout.error = null
            }
            .addOnFailureListener { e ->

                link1Layout.error = "Gagal menyimpan: ${e.message}"
            }
    }
}
