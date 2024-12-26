package com.example.Rebuild16_1

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHelper(
    context: Context,
    name: String = DB_NAME,
    factory: SQLiteDatabase.CursorFactory? = null,
    version: Int = VERSION
) : SQLiteOpenHelper(context, name, factory, version) {
    companion object {
        const val DB_NAME = "myDatabase"
        const val VERSION = 1
        const val TABLE_NAME = "myTable"
        const val COL_BOOK = "book"
        const val COL_PRICE = "price"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_NAME (
                $COL_BOOK text PRIMARY KEY, 
                $COL_PRICE integer NOT NULL
            )""".trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}

