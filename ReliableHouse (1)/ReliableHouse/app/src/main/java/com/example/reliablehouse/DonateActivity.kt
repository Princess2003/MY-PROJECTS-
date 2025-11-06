package com.example.reliablehouse

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DonateActivity : AppCompatActivity() {

    private lateinit var donationTypeSpinner: Spinner
    private lateinit var donationAmountSection: LinearLayout
    private lateinit var itemDonationSection: LinearLayout
    private lateinit var pickupPointSection: LinearLayout
    private lateinit var paymentMethodSpinner: Spinner
    private lateinit var donateButton: Button
    private lateinit var donateOther: CheckBox
    private lateinit var otherItems: EditText
    private lateinit var donationAmount: EditText
    private lateinit var pickupPoint: EditText
    private lateinit var cardNumber: EditText
    private lateinit var cardExpiry: EditText
    private lateinit var cardCVV: EditText
    private lateinit var donateClothes: CheckBox
    private lateinit var donateFood: CheckBox
    private lateinit var donateHygiene: CheckBox

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donate)

        initViews()
        setListeners()
    }

    private fun initViews() {
        donationTypeSpinner = findViewById(R.id.donationTypeSpinner)
        donationAmountSection = findViewById(R.id.donationAmountSection)
        itemDonationSection = findViewById(R.id.itemDonationSection)
        pickupPointSection = findViewById(R.id.pickupPointSection)
        paymentMethodSpinner = findViewById(R.id.paymentMethodSpinner)
        donateButton = findViewById(R.id.donateButton)
        donateOther = findViewById(R.id.donateOther)
        otherItems = findViewById(R.id.otherItems)
        donationAmount = findViewById(R.id.donationAmount)
        pickupPoint = findViewById(R.id.pickupPoint)
        cardNumber = findViewById(R.id.cardNumber)
        cardExpiry = findViewById(R.id.cardExpiry)
        cardCVV = findViewById(R.id.cardCVV)
        donateClothes = findViewById(R.id.donateClothes)
        donateFood = findViewById(R.id.donateFood)
        donateHygiene = findViewById(R.id.donateHygiene)
    }

    private fun setListeners() {
        donationTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position == 0) { // For monetary donations
                    itemDonationSection.visibility = View.GONE
                    pickupPointSection.visibility = View.GONE
                } else {  // For item donations
                    itemDonationSection.visibility = View.VISIBLE
                    pickupPointSection.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        paymentMethodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> showCreditCardDialog()
                    1 -> showPaypalDialog()
                    else -> { }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        donateOther.setOnCheckedChangeListener { _, isChecked ->
            // Show other items field if 'Other' is selected
            otherItems.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        donateButton.setOnClickListener {
            submitDonation()
        }
    }

    private fun submitDonation() {
        val donationType = donationTypeSpinner.selectedItem?.toString() ?: ""
        val amount = donationAmount.text.toString().trim()
        val items = getSelectedItems()
        val pickup = pickupPoint.text.toString().trim()
        val paymentMethod = paymentMethodSpinner.selectedItem?.toString() ?: ""

        // Basic validation for required fields
        if (donationType.isEmpty() || amount.isEmpty() || pickup.isEmpty() || items.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show()
            return
        }

        // Additional validation for credit card details
        if (paymentMethod == "Credit Card" &&
            (cardNumber.text.isEmpty() || cardExpiry.text.isEmpty() || cardCVV.text.isEmpty())) {
            Toast.makeText(this, "Please enter valid credit card details.", Toast.LENGTH_SHORT).show()
            return
        }

        // Credit card format validation
        if (paymentMethod == "Credit Card") {
            val cardNumberText = cardNumber.text.toString().trim()
            val cardExpiryText = cardExpiry.text.toString().trim()
            val cardCVVText = cardCVV.text.toString().trim()

            // Simple regex validation for credit card number
            val cardNumberPattern = "^\\d{16}$".toRegex() // 16 digits
            if (!cardNumberPattern.matches(cardNumberText)) {
                Toast.makeText(this, "Please enter a valid credit card number.", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val user = firebaseAuth.currentUser
        if (user != null) {
            val donationData = hashMapOf(
                "donationType" to donationType,
                "amount" to amount,
                "items" to items,
                "pickupPoint" to pickup,
                "paymentMethod" to paymentMethod,
                "userEmail" to user.email
            )

            firestore.collection("donations")
                .add(donationData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Donation submitted successfully!", Toast.LENGTH_SHORT).show()
                    finish()  // Finish the activity
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to submit donation: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please log in to donate.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))  // Navigate to login
            finish()  // Finish DonateActivity to prevent navigating back
        }
    }

    private fun getSelectedItems(): List<String> {
        val selectedItems = mutableListOf<String>()
        if (donateClothes.isChecked) selectedItems.add("Clothes")
        if (donateFood.isChecked) selectedItems.add("Food")
        if (donateHygiene.isChecked) selectedItems.add("Hygiene Products")
        if (donateOther.isChecked) {
            val otherItemText = otherItems.text.toString().trim()
            if (otherItemText.isEmpty()) {
                Toast.makeText(this, "Please specify the other items.", Toast.LENGTH_SHORT).show()
                return emptyList()
            }
            selectedItems.add(otherItemText)
        }
        return selectedItems
    }

    // Function to show Credit Card Dialog
    private fun showCreditCardDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_credit_card, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Enter Credit Card Details")
            .setPositiveButton("Submit") { dialog, _ ->
                // Handle submission logic for credit card payment
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    // Function to show PayPal Dialog
    private fun showPaypalDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_paypal, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("PayPal Payment")
            .setPositiveButton("Submit") { dialog, _ ->
                // Handle submission logic for PayPal payment
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }
}
