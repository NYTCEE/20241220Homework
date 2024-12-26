package com.example.Rebuild16_1

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private val items = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var dbrw: SQLiteDatabase
    private lateinit var edBook: EditText
    private lateinit var edPrice: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        setupWindowInsets()
        initializeViews()
        setListener()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initializeViews() {
        dbrw = MyDBHelper(this).writableDatabase
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        findViewById<ListView>(R.id.listView).adapter = adapter
        edBook = findViewById(R.id.edBook)
        edPrice = findViewById(R.id.edPrice)
    }

    private fun setListener() {
        findViewById<Button>(R.id.btnInsert).setOnClickListener { handleInsert() }
        findViewById<Button>(R.id.btnUpdate).setOnClickListener { handleUpdate() }
        findViewById<Button>(R.id.btnDelete).setOnClickListener { handleDelete() }
        findViewById<Button>(R.id.btnQuery).setOnClickListener { handleQuery() }
    }

    private fun handleInsert() {
        if (!validateInput()) return
        try {
            val values = ContentValues().apply {
                put(MyDBHelper.COL_BOOK, edBook.text.toString())
                put(MyDBHelper.COL_PRICE, edPrice.text.toString().toInt())
            }
            dbrw.insert(MyDBHelper.TABLE_NAME, null, values)
            showToast("新增:${edBook.text},價格:${edPrice.text}")
            cleanEditText()
        } catch (e: Exception) {
            showToast("新增失敗:$e")
        }
    }

    private fun handleUpdate() {
        if (!validateInput()) return
        try {
            val values = ContentValues().apply {
                put(MyDBHelper.COL_PRICE, edPrice.text.toString().toInt())
            }
            val selection = "${MyDBHelper.COL_BOOK} = ?"
            val selectionArgs = arrayOf(edBook.text.toString())
            dbrw.update(MyDBHelper.TABLE_NAME, values, selection, selectionArgs)
            showToast("更新:${edBook.text},價格:${edPrice.text}")
            cleanEditText()
        } catch (e: Exception) {
            showToast("更新失敗:$e")
        }
    }

    private fun handleDelete() {
        if (edBook.length() < 1) {
            showToast("書名請勿留空")
            return
        }
        try {
            val selection = "${MyDBHelper.COL_BOOK} = ?"
            val selectionArgs = arrayOf(edBook.text.toString())
            dbrw.delete(MyDBHelper.TABLE_NAME, selection, selectionArgs)
            showToast("刪除:${edBook.text}")
            cleanEditText()
        } catch (e: Exception) {
            showToast("刪除失敗:$e")
        }
    }

    private fun handleQuery() {
        val selection = if (edBook.length() < 1) null
        else "${MyDBHelper.COL_BOOK} = ?"
        val selectionArgs = if (edBook.length() < 1) null
        else arrayOf(edBook.text.toString())

        dbrw.query(MyDBHelper.TABLE_NAME, null, selection, selectionArgs,
            null, null, null)?.use { cursor ->
            items.clear()
            showToast("共有${cursor.count}筆資料")
            while (cursor.moveToNext()) {
                items.add("書名:${cursor.getString(0)}\t\t\t\t價格:${cursor.getInt(1)}")
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun validateInput() = if (edBook.length() < 1 || edPrice.length() < 1) {
        showToast("欄位請勿留空")
        false
    } else true

    private fun showToast(text: String) =
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()

    private fun cleanEditText() {
        edBook.setText("")
        edPrice.setText("")
    }

    override fun onDestroy() {
        super.onDestroy()
        dbrw.close()
    }
}
