package com.example.privatehospital.homescreen.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.privatehospital.R
import com.example.privatehospital.databinding.ItemDoctorsListBinding
import com.example.privatehospital.model.Doctor
import com.example.privatehospital.util.loadImage

/**
 * This adapter is used by doctor recycler view
 *
 * @param onDoctorClick event onclick to item doctor
 */
class DoctorAdapter(
    private val onDoctorClick: (String) -> Unit,
    private val setMajorName: (TextView, String) -> Unit
) : ListAdapter<Doctor, DoctorAdapter.DoctorViewHolder>(DiffCallback) {
    class DoctorViewHolder(private val binding: ItemDoctorsListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * This function is used to bind data for item doctor
         *
         * @param doctor
         * @param setMajorName
         */
        fun bind(
            doctor: Doctor,
            setMajorName: (TextView, String) -> Unit
        ) {
            binding.tvName.text = doctor.name
            binding.tvDescribe.text = doctor.describe
            binding.imgDoctorImage.loadImage(doctor.image)
            setMajorName(binding.tvMajor, doctor.majorId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        return DoctorViewHolder(
            ItemDoctorsListBinding.bind(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_doctors_list, parent, false)
            )
        )
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        // Get current item
        val current = getItem(position)
        // Set event onclick for this item
        holder.itemView.setOnClickListener { onDoctorClick(current.id) }
        // Bind data for this item
        holder.bind(current, setMajorName)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Doctor>() {
            override fun areItemsTheSame(oldItem: Doctor, newItem: Doctor): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Doctor, newItem: Doctor): Boolean {
                return oldItem.name == newItem.name &&
                        oldItem.sex == newItem.sex &&
                        oldItem.numberPhone == newItem.numberPhone &&
                        oldItem.image == newItem.image &&
                        oldItem.describe == newItem.describe &&
                        oldItem.majorId == newItem.majorId &&
                        oldItem.hospitalId == newItem.hospitalId
            }

        }
    }
}
