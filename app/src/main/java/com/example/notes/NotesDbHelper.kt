package com.example.notes

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NotesDbHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITLE TEXT NOT NULL,
                $COL_CONTENT TEXT NOT NULL,
                $COL_UPDATED INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE")
        onCreate(db)
    }

    fun insert(title: String, content: String): Long {
        val values = ContentValues().apply {
            put(COL_TITLE, title)
            put(COL_CONTENT, content)
            put(COL_UPDATED, System.currentTimeMillis())
        }
        return writableDatabase.insert(TABLE, null, values)
    }

    fun update(id: Long, title: String, content: String) {
        val values = ContentValues().apply {
            put(COL_TITLE, title)
            put(COL_CONTENT, content)
            put(COL_UPDATED, System.currentTimeMillis())
        }
        writableDatabase.update(TABLE, values, "$COL_ID = ?", arrayOf(id.toString()))
    }

    fun delete(id: Long) {
        writableDatabase.delete(TABLE, "$COL_ID = ?", arrayOf(id.toString()))
    }

    fun get(id: Long): Note? {
        readableDatabase.query(
            TABLE, null, "$COL_ID = ?", arrayOf(id.toString()), null, null, null
        ).use { c ->
            return if (c.moveToFirst()) c.toNote() else null
        }
    }

    fun getAll(query: String? = null): List<Note> {
        val list = mutableListOf<Note>()
        val selection: String?
        val args: Array<String>?
        if (query.isNullOrBlank()) {
            selection = null
            args = null
        } else {
            selection = "$COL_TITLE LIKE ? OR $COL_CONTENT LIKE ?"
            val like = "%${query.trim()}%"
            args = arrayOf(like, like)
        }
        readableDatabase.query(
            TABLE, null, selection, args, null, null, "$COL_UPDATED DESC"
        ).use { c ->
            while (c.moveToNext()) list.add(c.toNote())
        }
        return list
    }

    private fun android.database.Cursor.toNote() = Note(
        id = getLong(getColumnIndexOrThrow(COL_ID)),
        title = getString(getColumnIndexOrThrow(COL_TITLE)),
        content = getString(getColumnIndexOrThrow(COL_CONTENT)),
        updatedAt = getLong(getColumnIndexOrThrow(COL_UPDATED))
    )

    companion object {
        private const val DB_NAME = "notes.db"
        private const val DB_VERSION = 1
        private const val TABLE = "notes"
        private const val COL_ID = "id"
        private const val COL_TITLE = "title"
        private const val COL_CONTENT = "content"
        private const val COL_UPDATED = "updated_at"
    }
}
