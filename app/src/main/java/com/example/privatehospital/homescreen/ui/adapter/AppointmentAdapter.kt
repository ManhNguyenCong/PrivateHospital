package com.example.privatehospital.homescreen.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.privatehospital.R
import com.example.privatehospital.databinding.ItemAppointmentScheduleListBinding
import com.example.privatehospital.model.Appointment
import com.example.privatehospital.util.TIME_ZONE
import com.example.privatehospital.util.format
import com.example.privatehospital.util.setTextColorByColorId
import com.example.privatehospital.util.toDateTime
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * This adapter is used by Recycler view Appointment schedule
 *
 *
 */
class AppointmentAdapter(
    /**
     * This is used to set event onclick for item of recycler view
     *
     * @param serviceId
     * @param userId
     */
    private val setItemOnClick: (String, String) -> Unit,
    private val setServiceName: (TextView, String) -> Unit
) :
    ListAdapter<Appointment, AppointmentAdapter.AppointmentViewHolder>(DiffCallback) {
    class AppointmentViewHolder(private val binding: ItemAppointmentScheduleListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        /**
         * This function is used to bind data for appointment item
         *
         * @param appointment
         * @param setServiceName
         */
        fun bind(
            appointment: Appointment,
            setServiceName: (TextView, String) -> Unit,
        ) {
            val dateTime = appointment.dateTime.toDateTime()
            // Set time
            binding.tvTime.text = dateTime.time.format()
            // Set service name
            setServiceName(binding.tvServiceName, appointment.serviceId)

            // Todo Handle layout by appointment status
            if (dateTime < Clock.System.now().toLocalDateTime(TimeZone.of(TIME_ZONE))) {
                binding.tvTime.setTextColorByColorId(R.color.gray717171)
                binding.tvLine.setTextColorByColorId(R.color.gray717171)
                binding.tvServiceName.setTextColorByColorId(R.color.gray717171)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        return AppointmentViewHolder(
            ItemAppointmentScheduleListBinding.bind(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_appointment_schedule_list, parent, false
                )
            )
        )
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.itemView.setOnClickListener {
            setItemOnClick(currentItem.serviceId, currentItem.userId)
        }
        holder.bind(currentItem, setServiceName)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Appointment>() {
            override fun areItemsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
                return oldItem.userId == newItem.userId && oldItem.serviceId == newItem.serviceId
            }

            override fun areContentsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
                return oldItem.dateTime == newItem.dateTime && oldItem.status == newItem.status
            }
        }
    }
}