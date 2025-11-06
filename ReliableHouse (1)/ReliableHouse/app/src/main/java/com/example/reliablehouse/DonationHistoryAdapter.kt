package com.example.reliablehouse

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reliablehouse.R
import com.example.reliablehouse.databinding.ItemDonationBinding

class DonationHistoryAdapter(private val donationsList: List<Donation>) :
    RecyclerView.Adapter<DonationHistoryAdapter.DonationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationViewHolder {
        val binding = ItemDonationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DonationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DonationViewHolder, position: Int) {
        val donation = donationsList[position]
        holder.bind(donation)
    }

    override fun getItemCount(): Int {
        return donationsList.size
    }

    class DonationViewHolder(private val binding: ItemDonationBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(donation: Donation) {
            binding.tvDonationDate.text = donation.date // Set the date
            binding.tvDonationAmount.text = donation.amount.toString() // Set the amount
            binding.tvDonatedItems.text = donation.donatedItems // Set the donated items
            binding.tvPickupPoint.text = donation.pickupPoint // Set the pickup point
        }
    }
}
