package com.example.trackfit.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.trackfit.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var gridViewCalendar: GridView
    private lateinit var profileImageView: ImageView
    private lateinit var profileNameTextView: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        gridViewCalendar = view.findViewById(R.id.gridViewCalendar)
        profileImageView = view.findViewById(R.id.fotoProfile)
        profileNameTextView = view.findViewById(R.id.nama)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Generate dates for the current month starting from the 1st
        val monthDates = generateMonthDates()
        val adapter = CalendarAdapter(requireContext(), monthDates)
        gridViewCalendar.adapter = adapter

        setDynamicHeight(gridViewCalendar)
        loadProfileData()

        val button1 = view.findViewById<ImageView>(R.id.imageView7)
        val button2 = view.findViewById<ImageView>(R.id.imageView8)
        val button3 = view.findViewById<ImageView>(R.id.imageView10)
        val button4 = view.findViewById<ImageView>(R.id.imageView11)
        val button5 = view.findViewById<TextView>(R.id.textView14)

        button1.setOnClickListener {
            val intent1 = Intent(requireContext(), ProgramLatihan1::class.java)
            startActivity(intent1)
        }
        button2.setOnClickListener {
            val intent2 = Intent(requireContext(), Nutrisi::class.java)
            startActivity(intent2)
        }
        button3.setOnClickListener {
            val intent3 = Intent(requireContext(), WaktuIstitahat::class.java)
            startActivity(intent3)
        }
        button4.setOnClickListener {
            val intent4 = Intent(requireContext(), BeratTinggi::class.java)
            startActivity(intent4)
        }
        button5.setOnClickListener {
            val intent5 = Intent(requireContext(), LinkYT::class.java)
            startActivity(intent5)
        }
        gridViewCalendar.setOnItemClickListener { parent, view, position, id ->
            // Mendapatkan tanggal dari posisi yang diklik
            val dateClicked = adapter.getItem(position)

            // Buat Intent untuk membuka TargetActivity
            val intent = Intent(requireContext(), Targetnya::class.java)
            // Mengirim tanggal yang diklik ke TargetActivity sebagai extra
            intent.putExtra("clickedDate", dateClicked)
            startActivity(intent)
        }
        return view
    }

    private fun generateMonthDates(): List<Date> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1) // Set to the 1st day of the current month

        val dates = mutableListOf<Date>()
        val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in 1..maxDay) {
            dates.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dates
    }

    private fun setDynamicHeight(gridView: GridView) {
        val listAdapter = gridView.adapter ?: return

        var totalHeight = 0
        val items = listAdapter.count
        val rows = (items + gridView.numColumns - 1) / gridView.numColumns // Ensure rounding up

        for (i in 0 until rows) {
            val listItem = listAdapter.getView(i, null, gridView)
            listItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            totalHeight += listItem.measuredHeight
        }

        val params = gridView.layoutParams
        params.height = totalHeight + gridView.verticalSpacing * (rows - 1)
        gridView.layoutParams = params
    }

    private class CalendarAdapter(
        private val context: android.content.Context,
        private val dates: List<Date>
    ) : ArrayAdapter<Date>(context, R.layout.grid_item_calendar, dates) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val date = dates[position]
            val calendar = Calendar.getInstance()
            calendar.time = date
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.grid_item_calendar, parent, false)
            val textView = view.findViewById<android.widget.TextView>(R.id.text1)

            textView.text = dayOfMonth.toString()
            textView.textSize = 18f
            textView.setTextColor(android.graphics.Color.BLACK)
            textView.textAlignment = android.view.View.TEXT_ALIGNMENT_CENTER

            return view
        }
    }

    private fun loadProfileData() {
        val userId = auth.currentUser?.uid
        userId?.let { uid ->
            firestore.collection("users").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val nama = document.getString("username")
                        profileNameTextView.text = nama
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("ProfileMenu", "Error getting user data", exception)
                }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ensure fragment is attached to the activity
        val context = context ?: return  // Use context directly if not null

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val storageRef = FirebaseStorage.getInstance().reference
        val profilePictureRef = storageRef.child("profile_pictures").child("$userId.jpg")

        profilePictureRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(context)
                .load(uri)
                .placeholder(R.drawable.profile)
                .into(profileImageView)
        }.addOnFailureListener { e ->
            Log.e("ProfileMenu", "Error loading profile picture from Firebase Storage", e)
        }
    }
}
