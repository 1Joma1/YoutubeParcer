package com.joma.youtubeparcer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.joma.youtubeparcer.R
import com.joma.youtubeparcer.model.ItemsItem

class DetailPlaylistAdapter(val function: (ItemsItem) -> Unit) :
    RecyclerView.Adapter<DetailPlaylistAdapter.YouTubeViewHolder>() {


    private var list = mutableListOf<ItemsItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YouTubeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_youtube_playlist, parent, false)
        return YouTubeViewHolder(view, function)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: YouTubeViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun updateData(newList: List<ItemsItem>?) {
        list = newList as MutableList<ItemsItem>
        notifyDataSetChanged()
    }

    class YouTubeViewHolder(itemView: View, val function: (ItemsItem) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private var image: ImageView? = null
        private var title: TextView? = null
        private var description: TextView? = null

        init {
            image = itemView.findViewById(R.id.item_image)
            title = itemView.findViewById(R.id.item_title)
            description = itemView.findViewById(R.id.item_description)
        }

        fun bind(item: ItemsItem) {
            Glide.with(image!!.context).load(item.snippet.thumbnails.medium.url).apply(
                RequestOptions.bitmapTransform(
                    RoundedCorners(12)
                )).into(image!!)
            title?.text = item.snippet.title
            itemView.setOnClickListener {
                function(item)
            }
        }

    }
}