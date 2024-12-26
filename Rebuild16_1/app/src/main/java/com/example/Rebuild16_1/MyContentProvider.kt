package com.example.Rebuild16_1

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri

class MyContentProvider : ContentProvider() {
    private lateinit var dbrw: SQLiteDatabase

    companion object {
        const val AUTHORITY = "com.example.Rebuild16_1.provider"
        val URI_BOOK = Uri.parse("content://$AUTHORITY/${MyDBHelper.TABLE_NAME}")
    }

    override fun onCreate(): Boolean {
        val context = context ?: return false
        dbrw = MyDBHelper(context).writableDatabase
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        values ?: return null
        val rowId = dbrw.insert(MyDBHelper.TABLE_NAME, null, values)
        return Uri.withAppendedPath(URI_BOOK, rowId.toString())
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        values ?: return 0
        return dbrw.update(MyDBHelper.TABLE_NAME, values, selection, selectionArgs)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return dbrw.delete(MyDBHelper.TABLE_NAME, selection, selectionArgs)
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        return dbrw.query(MyDBHelper.TABLE_NAME, projection, selection, selectionArgs,
            null, null, sortOrder)
    }

    override fun getType(uri: Uri): String? = null
}