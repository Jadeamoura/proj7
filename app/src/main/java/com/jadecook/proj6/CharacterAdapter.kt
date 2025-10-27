package com.jadecook.proj6

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CharacterAdapter(
    private val items: MutableList<ApiCharacter>
) : RecyclerView.Adapter<CharacterAdapter.VH>() {

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivAvatar: ImageView = itemView.findViewById(R.id.ivAvatar)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvSubtitle: TextView = itemView.findViewById(R.id.tvSubtitle)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_character, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name
        holder.tvSubtitle.text = "${item.status} • ${item.species}"
        holder.tvLocation.text = "Last seen: ${item.locationName}"

        Glide.with(holder.itemView)
            .load(item.imageUrl)
            .into(holder.ivAvatar)

        // Stretch: Toast on item click
        holder.itemView.setOnClickListener {
            Toast.makeText(
                holder.itemView.context,
                "${item.name} — ${item.status} • ${item.species}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int = items.size

    fun replaceAll(newItems: List<ApiCharacter>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
