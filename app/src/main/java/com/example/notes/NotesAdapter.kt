package com.example.notes

import android.icu.text.SimpleDateFormat
import android.icu.util.ULocale
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Date

class NotesAdapter(
    private val onClick: (Note) -> Unit,
    private val onLongClick: (Note) -> Unit
) : RecyclerView.Adapter<NotesAdapter.VH>() {

    private var items: List<Note> = emptyList()

    // تاریخ شمسی
    private val dateFormat =
        SimpleDateFormat("yyyy/MM/dd - HH:mm", ULocale("fa_IR@calendar=persian"))

    fun submit(list: List<Note>) {
        items = list
        notifyDataSetChanged()
    }

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitle)
        val preview: TextView = view.findViewById(R.id.tvPreview)
        val date: TextView = view.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val note = items[position]
        holder.title.text = note.title.ifBlank { holder.itemView.context.getString(R.string.untitled) }
        holder.preview.text = note.content
        holder.preview.visibility = if (note.content.isBlank()) View.GONE else View.VISIBLE
        holder.date.text = dateFormat.format(Date(note.updatedAt))
        holder.itemView.setOnClickListener { onClick(note) }
        holder.itemView.setOnLongClickListener { onLongClick(note); true }
    }

    override fun getItemCount() = items.size
}
