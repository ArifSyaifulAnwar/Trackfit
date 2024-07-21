package com.example.trackfit

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class BeratTinggi : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var storageRef: StorageReference
    private lateinit var beratEditText: EditText
    private lateinit var tinggiEditText: EditText
    private lateinit var hasilCameraImageView: ImageView
    private var imageUri: Uri? = null
    private var isPortrait: Boolean = true // Menyimpan status tampilan saat ini
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_berat_tinggi)

        // Inisialisasi Firebase Firestore
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        // Inisialisasi Firebase Storage
        storageRef = FirebaseStorage.getInstance().reference

        // Mendapatkan referensi ke komponen-komponen dalam layout
        beratEditText = findViewById(R.id.namakegiatan)
        tinggiEditText = findViewById(R.id.durasilatihan)
        hasilCameraImageView = findViewById(R.id.hasilcamera)
        val textViewTanggal = findViewById<TextView>(R.id.tanggal)
        val simpanButton = findViewById<Button>(R.id.button2)
        val cameraImageView = findViewById<ImageView>(R.id.cameranya)
        val refreshImageView = findViewById<ImageView>(R.id.imageView16)

        // Mendapatkan tanggal hari ini dalam format yang diinginkan (misalnya: 5 Juli 2024)
        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        val tanggalSekarang = dateFormat.format(Date())
        textViewTanggal.text = tanggalSekarang

        // Mengambil gambar dari kamera saat tombol kamera ditekan
        cameraImageView.setOnClickListener {
            checkCameraPermission()
        }

        // Menyimpan data ke Firestore saat tombol "Simpan" ditekan
        simpanButton.setOnClickListener {
            val berat = beratEditText.text.toString().trim()
            val tinggi = tinggiEditText.text.toString().trim()

            // Validasi input
            if (berat.isEmpty()) {
                beratEditText.error = "Berat badan tidak boleh kosong"
                return@setOnClickListener
            }

            if (tinggi.isEmpty()) {
                tinggiEditText.error = "Tinggi badan tidak boleh kosong"
                return@setOnClickListener
            }

            // Upload image to Firebase Storage and save data to Firestore
            if (imageUri != null) {
                uploadImageToStorage { uri ->
                    saveDataToFirestore(berat, tinggi, uri.toString())
                }
            } else {
                saveDataToFirestore(berat, tinggi, null)
            }
        }

        // Refresh tampilan hasil kamera jika tombol refresh ditekan
        refreshImageView.setOnClickListener {
            hasilCameraImageView.setImageResource(android.R.color.transparent)
            imageUri = null
        }

        // Mengubah tampilan gambar antara potret dan lanskap saat imageView16 ditekan
        hasilCameraImageView.setOnClickListener {
            if (isPortrait) {
                // Rotate 90 derajat ke kanan untuk tampilan lanskap
                hasilCameraImageView.rotation = 90f
                isPortrait = false
            } else {
                // Kembali ke tampilan potret
                hasilCameraImageView.rotation = 0f
                isPortrait = true
            }
        }
    }

    // Membersihkan input setelah berhasil disimpan
    private fun clearFields() {
        beratEditText.text.clear()
        tinggiEditText.text.clear()
        hasilCameraImageView.setImageResource(android.R.color.transparent)
        imageUri = null
    }

    // Fungsi untuk mengambil gambar dari kamera
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    // Fungsi untuk menangani hasil dari pengambilan gambar dari kamera
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            hasilCameraImageView.setImageBitmap(imageBitmap)

            // Mengubah bitmap menjadi URI untuk upload ke Firebase Storage
            imageUri = bitmapToUri(imageBitmap)
        }
    }

    // Fungsi untuk mengubah Bitmap menjadi URI
    private fun bitmapToUri(bitmap: Bitmap): Uri {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }

    // Fungsi untuk mengupload gambar ke Firebase Storage
    private fun uploadImageToStorage(callback: (Uri) -> Unit) {
        val userId = auth.currentUser?.uid
        val imageRef = storageRef.child("images/$userId/berat_tinggi.jpg")

        imageUri?.let { uri ->
            val uploadTask = imageRef.putFile(uri)

            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    callback(downloadUri)
                    Toast.makeText(this, "Gambar berhasil diupload", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal mendapatkan URL gambar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Gagal mengupload gambar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fungsi untuk menyimpan data ke Firestore
    private fun saveDataToFirestore(berat: String, tinggi: String, imageUrl: String?) {
        val data = hashMapOf(
            "berat_badan" to berat,
            "tinggi_badan" to tinggi,
            "image_url" to imageUrl
        )

        val user = auth.currentUser
        user?.let {
            val userId = it.uid
            db.collection("users").document(userId).collection("data_bb")
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

    // Fungsi untuk memeriksa izin kamera dan meminta izin jika belum diberikan
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            // Izin belum diberikan, minta izin
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        } else {
            // Izin sudah diberikan, buka kamera
            dispatchTakePictureIntent()
        }
    }

    // Tanggapan dari permintaan izin
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                // Jika izin diberikan, buka kamera
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent()
                } else {
                    // Izin ditolak, berikan pesan atau tindakan lainnya
                    Toast.makeText(this, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {
                // Handle other permissions if needed
            }
        }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_CAMERA_PERMISSION = 101
    }
}
