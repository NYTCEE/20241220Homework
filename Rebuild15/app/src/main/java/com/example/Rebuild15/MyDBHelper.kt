package com.example.Rebuild15

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHelper(
    context: Context,
    name: String = DB_NAME,
    factory: SQLiteDatabase.CursorFactory? = null,
    version: Int = VERSION
) : SQLiteOpenHelper(context, name, factory, version) {

    companion object {
        private const val DB_NAME = "myDatabase"
        private const val VERSION = 1
        private const val TABLE_NAME = "myTable"
        private const val COL_BOOK = "book"
        private const val COL_PRICE = "price"
    }

    private var database: SQLiteDatabase? = null

    fun openDatabase() {
        database = this.writableDatabase
    }

    fun closeDatabase() {
        database?.close()
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_NAME (
                $COL_BOOK text PRIMARY KEY,
                $COL_PRICE integer NOT NULL
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertBook(book: String, price: Int): Boolean {
        return try {
            val values = ContentValues().apply {
                put(COL_BOOK, book)
                put(COL_PRICE, price)
            }
            database?.insert(TABLE_NAME, null, values)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun updateBook(book: String, price: Int): Boolean {
        return try {
            val values = ContentValues().apply {
                put(COL_PRICE, price)
            }
            database?.update(TABLE_NAME, values, "$COL_BOOK = ?", arrayOf(book))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun deleteBook(book: String): Boolean {
        return try {
            database?.delete(TABLE_NAME, "$COL_BOOK = ?", arrayOf(book))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun queryBooks(book: String? = null): Cursor? {
        val selection = if (book != null) "$COL_BOOK = ?" else null
        val selectionArgs = if (book != null) arrayOf(book) else null

        return database?.query(
            TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
    }
}