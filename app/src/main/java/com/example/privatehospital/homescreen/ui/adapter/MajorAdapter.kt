package com.example.privatehospital.homescreen.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.privatehospital.R
import com.example.privatehospital.databinding.ItemCategoriesListBinding
import com.example.privatehospital.databinding.ItemGroupServicesListBinding
import com.example.privatehospital.model.Category
import com.example.privatehospital.model.Major
import com.example.privatehospital.util.TAG
import com.example.privatehospital.util.loadImage

/**
 * This adapter is used by major recycler view
 *
 * @param submitListForCategoryAdapter submit list for category adapter
 * @param navToServiceList navigate to service list
 */
class MajorAdapter(
    private val submitListForCategoryAdapter: (String, CategoryAdapter) -> Unit,
    private val navToServiceList: (Category) -> Unit
) : ListAdapter<Major, MajorAdapter.MajorViewHolder>(DiffCallback) {
    class MajorViewHolder(private val binding: ItemGroupServicesListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        /**
         * This function is used to bind data for recycler item major
         *
         * @param major
         * @param submitListForCategoryAdapter
         * @param navToServiceList
         */
        fun bind(
            major: Major,
            submitListForCategoryAdapter: (String, CategoryAdapter) -> Unit,
            navToServiceList: (Category) -> Unit
        ) {
            Log.d(TAG, "bind: " + major.toString())
            // Load title of major
            binding.tvTitle.text = major.name
            // Init category adapter
            val categoryAdapter = CategoryAdapter(navToServiceList)
            // Set adapter for recycler view
            binding.rvCategories.adapter = categoryAdapter
            // Submit list for category adapter
            submitListForCategoryAdapter(major.id, categoryAdapter)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MajorViewHolder {
        return MajorViewHolder(
            ItemGroupServicesListBinding.bind(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_group_services_list, parent, false)
            )
        )
    }

    override fun onBindViewHolder(holder: MajorViewHolder, position: Int) {
        // Bind data for item major
        Log.d(TAG, "onBindViewHolder: ")
        holder.bind(getItem(position), submitListForCategoryAdapter, navToServiceList)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Major>() {
            override fun areItemsTheSame(oldItem: Major, newItem: Major): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Major, newItem: Major): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }

    /**
     * This adapter is used by category recycle view
     *
     * @param navToServiceList func navigate to service list by category
     */
    class CategoryAdapter(private val navToServiceList: (Category) -> Unit) :
        ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(DiffCallback) {
        class CategoryViewHolder(private val binding: ItemCategoriesListBinding) :
            RecyclerView.ViewHolder(binding.root) {
            /**
             * This function is used to bind data for item category
             *
             * @param category
             */
            fun bind(category: Category) {
                // Load icon
                binding.imgShortcut.loadImage(category.image)
                // Load name
                binding.tvName.text = category.name
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
            return CategoryViewHolder(
                ItemCategoriesListBinding.bind(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_categories_list, parent, false)
                )
            )
        }

        override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
            // Get current category
            val current = getItem(position)
            // Set onclick for item category
            holder.itemView.setOnClickListener {
                navToServiceList(current)
            }
            // Bind data for item category
            holder.bind(current)
        }

        companion object {
            private val DiffCallback = object : DiffUtil.ItemCallback<Category>() {
                override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
                    return oldItem.name == newItem.name &&
                            oldItem.image == newItem.image &&
                            oldItem.majorId == newItem.majorId
                }
            }
        }

    }
}