package com.example.notes

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class EditActivity : AppCompatActivity() {

    private lateinit var db: NotesDbHelper
    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private var noteId: Long = -1L
    private var deleted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = NotesDbHelper(this)
        etTitle = findViewById(R.id.etTitle)
        etContent = findViewById(R.id.etContent)

        noteId = savedInstanceState?.getLong(STATE_ID)
            ?: intent.getLongExtra(EXTRA_ID, -1L)

        if (noteId != -1L && savedInstanceState == null) {
            db.get(noteId)?.let {
                etTitle.setText(it.title)
                etContent.setText(it.content)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(STATE_ID, noteId)
    }

    override fun onPause() {
        super.onPause()
        if (!deleted) save()
    }

    private fun save() {
        val title = etTitle.text.toString().trim()
        val content = etContent.text.toString().trim()
        if (title.isEmpty() && content.isEmpty()) {
            // یادداشت خالی ذخیره نمی‌شود
            if (noteId != -1L) {
                db.delete(noteId)
                noteId = -1L
            }
            return
        }
        if (noteId == -1L) {
            noteId = db.insert(title, content)
        } else {
            db.update(noteId, title, content)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_delete -> {
                AlertDialog.Builder(this)
                    .setTitle(R.string.delete_title)
                    .setMessage(R.string.delete_message)
                    .setPositiveButton(R.string.delete) { _, _ ->
                        if (noteId != -1L) db.delete(noteId)
                        deleted = true
                        finish()
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_ID = "note_id"
        private const val STATE_ID = "state_id"
    }
}
