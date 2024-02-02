package com.example.privatehospital.homescreen.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.privatehospital.R
import com.example.privatehospital.databinding.ItemHospitalsListBinding
import com.example.privatehospital.model.Hospital
import com.example.privatehospital.util.loadImage

/**
 * This adapter is used by hospital recycler view
 *
 * @param onHospitalClick event onclick to hospital
 */
class HospitalAdapter(
    private val onHospitalClick: (String) -> Unit
) : ListAdapter<Hospital, HospitalAdapter.HospitalViewHolder>(DiffCallback) {
    class HospitalViewHolder(private val binding: ItemHospitalsListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * This function is used to bind data for item hospital recycler view
         *
         * @param hospital
         */
        fun bind(hospital: Hospital) {
            binding.tvName.text = hospital.name
            binding.tvNumOfPersonRated.text = String.format("(%d)", hospital.numOfPersonRated)
            binding.rating.rating = hospital.rating
            binding.imgHospitalImage.loadImage(hospital.images[0])
            binding.tvDescribe.text = hospital.describe
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalViewHolder {
        return HospitalViewHolder(
            ItemHospitalsListBinding.bind(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_hospitals_list, parent, false)
            )
        )
    }

    override fun onBindViewHolder(holder: HospitalViewHolder, position: Int) {
        // Get current hospital
        val current = getItem(position)
        // Set onclick for this item
        holder.itemView.setOnClickListener {
            onHospitalClick(current.id)
        }
        // Bind data for this item
        holder.bind(current)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Hospital>() {
            override fun areItemsTheSame(oldItem: Hospital, newItem: Hospital): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Hospital, newItem: Hospital): Boolean {
                return oldItem.name == newItem.name &&
                        oldItem.address == newItem.address &&
                        oldItem.describe == newItem.describe &&
                        oldItem.rating == newItem.rating &&
                        oldItem.numOfPersonRated == newItem.numOfPersonRated &&
                        oldItem.images == newItem.images &&
                        oldItem.email == newItem.email &&
                        oldItem.website == newItem.website &&
                        oldItem.hotline == newItem.hotline
            }
        }
    }
}