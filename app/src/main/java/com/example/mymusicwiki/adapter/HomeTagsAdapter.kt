package com.example.mymusicwiki.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.mymusicwiki.R
import com.example.mymusicwiki.model.HomeTag

class HomeTagsAdapter(
    private val tagList: ArrayList<HomeTag>,
    private val size: Int,
    private val from: String,
    private val onItemClicked: (position: Int) -> Unit
) : RecyclerView.Adapter<HomeTagsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        if (from.equals("home")) {
            val view = inflater
                .inflate(R.layout.home_tag_design, parent, false)
            return ViewHolder(view, onItemClicked)
        } else {
            val view = inflater
                .inflate(R.layout.album_tag_design, parent, false)
            return ViewHolder(view, onItemClicked)
        }


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val homeTag = tagList[position]
        // sets the text to the textview from our itemHolder class
        holder.tag_name.text = homeTag.name

    }

    override fun getItemCount(): Int {
        return size
    }

    class ViewHolder(ItemView: View, private val onItemClicked: (position: Int) -> Unit) :
        RecyclerView.ViewHolder(ItemView), View.OnClickListener {
        val tag_name: TextView = ItemView.findViewById(R.id.tag_name)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val position = adapterPosition
            onItemClicked(position)
        }

    }
}