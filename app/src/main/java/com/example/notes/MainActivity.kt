package com.example.notes

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var db: NotesDbHelper
    private lateinit var adapter: NotesAdapter
    private lateinit var etSearch: EditText
    private lateinit var tvEmpty: TextView
    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = NotesDbHelper(this)
        etSearch = findViewById(R.id.etSearch)
        tvEmpty = findViewById(R.id.tvEmpty)
        recycler = findViewById(R.id.recycler)

        adapter = NotesAdapter(
            onClick = { note ->
                startActivity(
                    Intent(this, EditActivity::class.java)
                        .putExtra(EditActivity.EXTRA_ID, note.id)
                )
            },
            onLongClick = { note -> confirmDelete(note) }
        )
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            startActivity(Intent(this, EditActivity::class.java))
        }

        etSearch.doAfterTextChanged { load() }
    }

    override fun onResume() {
        super.onResume()
        load()
    }

    private fun load() {
        val list = db.getAll(etSearch.text?.toString())
        adapter.submit(list)
        tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun confirmDelete(note: Note) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_title)
            .setMessage(R.string.delete_message)
            .setPositiveButton(R.string.delete) { _, _ ->
                db.delete(note.id)
                load()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
