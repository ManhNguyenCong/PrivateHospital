package com.example.privatehospital.homescreen.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.privatehospital.R
import com.example.privatehospital.databinding.ItemImagesListBinding
import com.example.privatehospital.util.loadImage

/**
 * This adapter is used by image recycler view
 *
 * @param images list of images
 */
class ImageAdapter(private val images: List<String>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    class ImageViewHolder(private val binding: ItemImagesListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        /**
         * This function is used to load image for item
         *
         * @param image image url
         */
        fun bind(image: String) {
            binding.imgHospital.loadImage(image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            ItemImagesListBinding.bind(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_images_list, parent, false)
            )
        )
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position])
    }
}