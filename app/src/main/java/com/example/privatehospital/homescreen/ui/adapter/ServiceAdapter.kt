package com.example.privatehospital.homescreen.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.privatehospital.R
import com.example.privatehospital.databinding.ItemServicesListBinding
import com.example.privatehospital.model.Service

/**
 * This adapter is used by service recycler view
 *
 * @param setHospitalName set hospital name for tvHospital
 * @param onServiceClick event click to service
 */
class ServiceAdapter(
    private val setHospitalName: (TextView, String) -> Unit,
    private val onServiceClick: (String) -> Unit
) : ListAdapter<Service, ServiceAdapter.ServiceViewHolder>(DiffCallback) {
    class ServiceViewHolder(private val binding: ItemServicesListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * This function is used to bind data for item service list
         *
         * @param service
         * @param setHospitalName set hospital name for tvHospital
         */
        fun bind(
            service: Service,
            setHospitalName: (TextView, String) -> Unit
        ) {
            binding.tvName.text = service.name
            binding.tvDescribe.text = service.describe
            setHospitalName(binding.tvHospital, service.hospitalId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        return ServiceViewHolder(
            ItemServicesListBinding.bind(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_services_list, parent, false)
            )
        )
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        // Get current service
        val current = getItem(position)
        // Set onclick for current service
        holder.itemView.setOnClickListener {
            onServiceClick(current.id)
        }
        // Bind data for this service
        holder.bind(current, setHospitalName)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Service>() {
            override fun areItemsTheSame(oldItem: Service, newItem: Service): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Service, newItem: Service): Boolean {
                return oldItem.name == newItem.name &&
                        oldItem.timeStart == newItem.timeStart &&
                        oldItem.timeEnd == newItem.timeEnd &&
                        oldItem.cost == newItem.cost &&
                        oldItem.describe == newItem.describe &&
                        oldItem.categoryId == newItem.categoryId &&
                        oldItem.hospitalId == newItem.hospitalId
            }

        }
    }
}