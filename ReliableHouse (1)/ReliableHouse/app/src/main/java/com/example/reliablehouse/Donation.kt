package com.example.reliablehouse

data class Donation(
    val date: String = "", // Date of the donation
    val amount: Double = 0.0, // Amount donated
    val donatedItems: String = "", // Description of donated items
    val pickupPoint: String = "", // Pickup point for the donation
    val donorEmail: String = "" // User ID (from Firebase)
)


