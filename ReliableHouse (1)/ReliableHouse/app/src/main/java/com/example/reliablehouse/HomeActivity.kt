package com.example.reliablehouse

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseUser

class HomeActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var currentUser: FirebaseUser? = null // Change to nullable type
    private lateinit var messageTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        messageTextView = findViewById(R.id.textWelcome)

        val buttonDonor = findViewById<Button>(R.id.buttonDonor)
        val buttonViewDonations = findViewById<Button>(R.id.buttonViewDonations)
        val buttonVolunteer = findViewById<Button>(R.id.buttonVolunteer)

        // Check if the user is logged in
        currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            // If no user is logged in, display the message and navigate to Login Activity
            messageTextView.text = "No user is logged in."
            startActivity(Intent(this, LoginActivity::class.java)) // Redirect to login page
            finish() // Close the HomeActivity to prevent back navigation
            return
        }

        // Display the current user's email (optional)
        messageTextView.text = "Welcome, ${currentUser?.email}"

        // Listen for role selection buttons
        buttonDonor.setOnClickListener {
            handleRoleSelection("donor")
        }

        buttonViewDonations.setOnClickListener {
            // Navigate to donation history activity
            startActivity(Intent(this, ViewDonationHistoryActivity::class.java))
        }

        buttonVolunteer.setOnClickListener {
            handleRoleSelection("volunteer")
        }
    }

    // Function to handle role selection
    private fun handleRoleSelection(role: String) {
        currentUser?.let { user ->
            // Use Firebase UID instead of email for the document reference
            val userRef = firestore.collection("users").document(user.uid)
            userRef.update("role", role)
                .addOnSuccessListener {
                    if (role == "donor") {
                        startActivity(Intent(this, DonateActivity::class.java)) // Navigate to donor page
                    } else {
                        startActivity(Intent(this, VolunteerActivity::class.java)) // Navigate to volunteer page
                    }
                    finish() // Finish current activity after navigation
                }
                .addOnFailureListener { error ->
                    Toast.makeText(this, "Error updating role: ${error.message}", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            messageTextView.text = "Error: No user is logged in."
        }
    }
}
