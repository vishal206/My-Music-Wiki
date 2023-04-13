package com.example.mymusicwiki.adapter

import android.content.ClipData.Item
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mymusicwiki.R
import com.example.mymusicwiki.model.Album
import com.example.mymusicwiki.model.HomeTag

class AlbumsAdapter(
    private val context: Context,
    private val albumList: ArrayList<Album>,
    private val onItemClicked: (position: Int) -> Unit
) : RecyclerView.Adapter<AlbumsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater
            .inflate(R.layout.album_list_design, parent, false)

        return AlbumsAdapter.ViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val album = albumList[position]
        // sets the text to the textview from our itemHolder class
        holder.album_name.text = album.name
        holder.album_artist.text = album.artist
        Glide.with(context).load(album.imageUrl).into(holder.album_image)
    }

    override fun getItemCount(): Int {
        return albumList.size
    }

    class ViewHolder(ItemView: View, private val onItemClicked: (position: Int) -> Unit) :
        RecyclerView.ViewHolder(ItemView), View.OnClickListener {
        val album_name: TextView = ItemView.findViewById(R.id.album_name)
        val album_artist: TextView = ItemView.findViewById(R.id.album_artist)
        val album_image: ImageView = ItemView.findViewById(R.id.album_image)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val position = adapterPosition
            onItemClicked(position)
        }

    }
}