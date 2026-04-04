package com.example.section3_android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.section3_android.R
import com.example.section3_android.model.ImageItem

/**
 * RecyclerView Adapter for displaying a list of dog images.
 * Uses Glide to load images from URLs into ImageViews.
 */
class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private var imageList: List<ImageItem> = ArrayList()

    /**
     * Updates the adapter's data and refreshes the RecyclerView.
     */
    fun setImageList(newList: List<ImageItem>) {
        this.imageList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val currentItem: ImageItem = imageList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    /**
     * ViewHolder class that holds references to the views in item_image.xml.
     */
    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageView: ImageView = itemView.findViewById(R.id.imageViewDog)

        fun bind(imageItem: ImageItem) {
            Glide.with(itemView.context)
                .load(imageItem.message)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(imageView)
        }
    }
}
