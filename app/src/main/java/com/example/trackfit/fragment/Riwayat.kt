package com.example.trackfit.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.trackfit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileOutputStream

class Riwayat : Fragment() {

    private lateinit var namaTextView: TextView
    private lateinit var tanggalTextView: TextView
    private lateinit var tanggalnyaTextView: TextView
    private lateinit var kegiatanTextView: TextView
    private lateinit var durasiTextView: TextView
    private lateinit var durasinyaTextView: TextView
    private lateinit var kkalTextView: TextView
    private lateinit var kkalnyaTextView: TextView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var profileImageView: ImageView
    private lateinit var kalsiumnya: TextView
    private lateinit var proteinnya: TextView
    private lateinit var vitaminnya: TextView
    private lateinit var tambahannya: TextView
    private lateinit var bangun: TextView
    private lateinit var tidur: TextView
    private lateinit var beratBadan: TextView
    private lateinit var tinggiBadan: TextView
    private lateinit var imageView21: ImageView
    private lateinit var imageView19: ImageView
    private lateinit var imageView22: ImageView
    private lateinit var btnShare: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_riwayat, container, false)

        namaTextView = view.findViewById(R.id.nama)
        tanggalTextView = view.findViewById(R.id.tanggal)
        kegiatanTextView = view.findViewById(R.id.kegiatan)
        durasiTextView = view.findViewById(R.id.durasi)
        durasinyaTextView = view.findViewById(R.id.durasinya)
        kkalTextView = view.findViewById(R.id.kkal)
        kkalnyaTextView = view.findViewById(R.id.kkalnya)
        profileImageView = view.findViewById(R.id.fotoProfile)
        kalsiumnya = view.findViewById(R.id.kalorinya)
        proteinnya = view.findViewById(R.id.proteinnya)
        vitaminnya = view.findViewById(R.id.vitaminnya)
        tambahannya = view.findViewById(R.id.tambahannya)
        bangun = view.findViewById(R.id.waktubangun)
        tidur = view.findViewById(R.id.waktutidur)
        beratBadan = view.findViewById(R.id.beratnya)
        tinggiBadan = view.findViewById(R.id.tingibadan)
        imageView21 = view.findViewById(R.id.imageView21)
        imageView19 = view.findViewById(R.id.imageView19)
        imageView22 = view.findViewById(R.id.imageView22)
        btnShare = view.findViewById(R.id.share)
        tanggalnyaTextView = view.findViewById(R.id.tanggalnya)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        loadRiwayatData()
        loadProfileData()
        loadNutrisiData()
        loadLacakTidur()
        loadbb()
        loadImageView21()

        imageView19.setOnClickListener {
            deleteData()
        }

        imageView22.setOnClickListener {
            deleteBBData()
            deleteImageView21Data()
        }

        btnShare.setOnClickListener {
            shareData()
        }

        return view
    }

    private fun loadRiwayatData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        userId?.let { uid ->
            firebaseFirestore.collection("users").document(uid).collection("program_latihan")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            val tanggal = document.getString("tanggal") ?: ""
                            val kegiatan = document.getString("nama_kegiatan") ?: ""
                            val durasi = document.getString("durasi") ?: ""
                            val kkal = document.getLong("kkal") ?: 0L
                            tanggalTextView.text = tanggal
                            tanggalnyaTextView.text = tanggal
                            kegiatanTextView.text = kegiatan
                            durasinyaTextView.text = durasi
                            kkalnyaTextView.text = kkal.toString()
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.exception)
                    }
                }
        }
    }

    private fun loadProfileData() {
        val userId = firebaseAuth.currentUser?.uid

        userId?.let { uid ->
            firebaseFirestore.collection("users").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    val nama = document.getString("username")
                    namaTextView.text = nama
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting user data", exception)
                }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = context ?: return

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val storageRef = FirebaseStorage.getInstance().reference
        val profilePictureRef = storageRef.child("profile_pictures").child("$userId.jpg")

        profilePictureRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(context)
                .load(uri)
                .placeholder(R.drawable.profile)
                .into(profileImageView)
        }.addOnFailureListener { e ->
            Log.e(TAG, "Error loading profile picture from Firebase Storage", e)
        }
    }

    private fun loadNutrisiData() {
        val userId = firebaseAuth.currentUser?.uid

        userId?.let { uid ->
            firebaseFirestore.collection("users").document(uid).collection("nutrisi")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            val kalsium = document.getString("kalsium") ?: ""
                            val protein = document.getString("protein") ?: ""
                            val vitamin = document.getString("vitamin") ?: ""
                            val tambahan = document.getString("tambahan") ?: ""
                            kalsiumnya.text = kalsium
                            proteinnya.text = protein
                            vitaminnya.text = vitamin
                            tambahannya.text = tambahan
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.exception)
                    }
                }
        }
    }

    private fun loadLacakTidur() {
        val userId = firebaseAuth.currentUser?.uid

        userId?.let { uid ->
            firebaseFirestore.collection("users").document(uid).collection("waktu_istirahat")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            val bangn = document.getString("waktu_bangun") ?: ""
                            val tdr = document.getString("waktu_tidur") ?: ""
                            bangun.text = bangn
                            tidur.text = tdr
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.exception)
                    }
                }
        }
    }

    private fun loadbb() {
        val userId = firebaseAuth.currentUser?.uid

        userId?.let { uid ->
            firebaseFirestore.collection("users").document(uid).collection("data_bb")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            val berat = document.getString("berat_badan") ?: ""
                            val tingi = document.getString("tinggi_badan") ?: ""
                            beratBadan.text = berat
                            tinggiBadan.text = tingi
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.exception)
                    }
                }
        }
    }

    private fun loadImageView21() {
        val userId = firebaseAuth.currentUser?.uid
        val storageRef = FirebaseStorage.getInstance().reference
        val imageView21Ref = storageRef.child("images").child("$userId/berat_tinggi.jpg")

        imageView21Ref.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this)
                .load(uri)
                .into(imageView21)
        }.addOnFailureListener { e ->
            Log.e(TAG, "Error loading image for imageView21 from Firebase Storage", e)
        }
    }

    private fun shareData() {
        val riwayat = buildString {
            append("Nama: ${namaTextView.text}\n")
            append("Tanggal: ${tanggalTextView.text}\n")
            append("Kegiatan: ${kegiatanTextView.text}\n")
            append("Durasi: ${durasinyaTextView.text}\n")
            append("KKal: ${kkalnyaTextView.text}\n")
            append("Kalsium: ${kalsiumnya.text}\n")
            append("Protein: ${proteinnya.text}\n")
            append("Vitamin: ${vitaminnya.text}\n")
            append("Tambahan: ${tambahannya.text}\n")
            append("Waktu Bangun: ${bangun.text}\n")
            append("Waktu Tidur: ${tidur.text}\n")
            append("Berat Badan: ${beratBadan.text}\n")
            append("Tinggi Badan: ${tinggiBadan.text}\n")
        }

        // Get URI of the image from imageView21
        val imageView21Uri = getUriFromImageView(imageView21)

        // Create intent to share text and image
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, riwayat)
            putExtra(Intent.EXTRA_STREAM, imageView21Uri)
            type = "image/jpeg" // Adjust type according to your image type
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun getUriFromImageView(imageView: ImageView): Uri? {
        val drawable = imageView.drawable ?: return null

        val bitmap = when (drawable) {
            is BitmapDrawable -> drawable.bitmap
            else -> return null
        }

        val file = File(requireContext().cacheDir, "share_image.jpg")
        file.createNewFile()

        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        return FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", file)
    }


    private fun deleteData() {
        deleteRiwayatData()
        deleteNutrisiData()
        deleteLacakTidurData()
    }

    private fun deleteRiwayatData() {
        val userId = firebaseAuth.currentUser?.uid

        userId?.let { uid ->
            firebaseFirestore.collection("users").document(uid).collection("program_latihan")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            document.reference.delete()
                                .addOnSuccessListener {
                                    Log.d(TAG, "DocumentSnapshot successfully deleted!")
                                    tanggalTextView.text = ""
                                    tanggalnyaTextView.text = ""
                                    kegiatanTextView.text = ""
                                    durasinyaTextView.text = ""
                                    kkalnyaTextView.text = ""
                                    Toast.makeText(context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error deleting document", e)
                                }
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.exception)
                    }
                }
        }
    }

    private fun deleteNutrisiData() {
        val userId = firebaseAuth.currentUser?.uid

        userId?.let { uid ->
            firebaseFirestore.collection("users").document(uid).collection("nutrisi")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            document.reference.delete()
                                .addOnSuccessListener {
                                    Log.d(TAG, "DocumentSnapshot successfully deleted!")
                                    kalsiumnya.text = ""
                                    proteinnya.text = ""
                                    vitaminnya.text = ""
                                    tambahannya.text = ""
                                    Toast.makeText(context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error deleting document", e)
                                }
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.exception)
                    }
                }
        }
    }

    private fun deleteLacakTidurData() {
        val userId = firebaseAuth.currentUser?.uid

        userId?.let { uid ->
            firebaseFirestore.collection("users").document(uid).collection("waktu_istirahat")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            document.reference.delete()
                                .addOnSuccessListener {
                                    Log.d(TAG, "DocumentSnapshot successfully deleted!")
                                    bangun.text = ""
                                    tidur.text = ""
                                    Toast.makeText(context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error deleting document", e)
                                }
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.exception)
                    }
                }
        }
    }

    private fun deleteBBData() {
        val userId = firebaseAuth.currentUser?.uid

        userId?.let { uid ->
            firebaseFirestore.collection("users").document(uid).collection("data_bb")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            document.reference.delete()
                                .addOnSuccessListener {
                                    Log.d(TAG, "DocumentSnapshot successfully deleted!")
                                    beratBadan.text = ""
                                    tinggiBadan.text = ""
                                    Toast.makeText(context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error deleting document", e)
                                }
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.exception)
                    }
                }
        }
    }

    private fun deleteImageView21Data() {
        val userId = firebaseAuth.currentUser?.uid
        val storageRef = FirebaseStorage.getInstance().reference
        val imageView21Ref = storageRef.child("images").child("$userId/berat_tinggi.jpg")

        imageView21Ref.delete().addOnSuccessListener {
            Log.d(TAG, "Image deleted successfully")
            imageView21.setImageDrawable(null)
        }.addOnFailureListener { e ->
            Log.e(TAG, "Error deleting image", e)
        }
    }

    companion object {
        private const val TAG = "RiwayatFragment"
    }
}
