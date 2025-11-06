package com.example.reliablehouse

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reliablehouse.databinding.ActivityViewDonationHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ViewDonationHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewDonationHistoryBinding
    private lateinit var adapter: DonationHistoryAdapter
    private val donationsList = mutableListOf<Donation>()

    // Firebase instances
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityViewDonationHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DonationHistoryAdapter(donationsList)
        binding.recyclerView.adapter = adapter

        // Fetch donations for the logged-in user
        fetchUserDonations()
    }

    private fun fetchUserDonations() {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val donorEmail = currentUser.uid
        firestore.collection("Donations")
            .whereEqualTo("donorEmail", donorEmail) // Query donations by userId
            .get()
            .addOnSuccessListener { documents ->
                donationsList.clear()
                for (document in documents) {
                    val donation = document.toObject(Donation::class.java)
                    donationsList.add(donation)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("FetchDonations", "Error fetching donations", exception)
                Toast.makeText(this, "Failed to fetch donations", Toast.LENGTH_SHORT).show()
            }
    }
}
