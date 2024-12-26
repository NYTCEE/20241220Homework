package com.example.Rebuild15

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val items = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var dbHelper: MyDBHelper
    private lateinit var edBook: EditText
    private lateinit var edPrice: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupDatabase()
        setListeners()
    }

    private fun initializeViews() {
        edBook = findViewById(R.id.edBook)
        edPrice = findViewById(R.id.edPrice)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        findViewById<ListView>(R.id.listView).adapter = adapter
    }

    private fun setupDatabase() {
        dbHelper = MyDBHelper(this)
        dbHelper.openDatabase()
    }

    private fun setListeners() {
        findViewById<Button>(R.id.btnInsert).setOnClickListener { handleInsert() }
        findViewById<Button>(R.id.btnUpdate).setOnClickListener { handleUpdate() }
        findViewById<Button>(R.id.btnDelete).setOnClickListener { handleDelete() }
        findViewById<Button>(R.id.btnQuery).setOnClickListener { handleQuery() }
    }

    private fun handleInsert() {
        if (validateInput()) {
            val book = edBook.text.toString()
            val price = edPrice.text.toString().toInt()

            if (dbHelper.insertBook(book, price)) {
                showToast("新增成功：$book，價格：$price")
                cleanEditText()
                handleQuery()
            } else {
                showToast("新增失敗")
            }
        }
    }

    private fun handleUpdate() {
        if (validateInput()) {
            val book = edBook.text.toString()
            val price = edPrice.text.toString().toInt()

            if (dbHelper.updateBook(book, price)) {
                showToast("更新成功：$book，價格：$price")
                cleanEditText()
                handleQuery()
            } else {
                showToast("更新失敗")
            }
        }
    }

    private fun handleDelete() {
        if (edBook.length() < 1) {
            showToast("書名請勿留空")
            return
        }

        val book = edBook.text.toString()
        if (dbHelper.deleteBook(book)) {
            showToast("刪除成功：$book")
            cleanEditText()
            handleQuery()
        } else {
            showToast("刪除失敗")
        }
    }

    private fun handleQuery() {
        val book = if (edBook.length() > 0) edBook.text.toString() else null
        val cursor = dbHelper.queryBooks(book)

        cursor?.use { c ->
            items.clear()
            showToast("共有${c.count}筆資料")

            if (c.moveToFirst()) {
                do {
                    val bookName = c.getString(c.getColumnIndexOrThrow("book"))
                    val price = c.getInt(c.getColumnIndexOrThrow("price"))
                    items.add("書名：$bookName\t\t\t\t價格：$price")
                } while (c.moveToNext())
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun validateInput(): Boolean {
        return when {
            edBook.length() < 1 || edPrice.length() < 1 -> {
                showToast("欄位請勿留空")
                false
            }
            else -> true
        }
    }

    private fun showToast(text: String) =
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()

    private fun cleanEditText() {
        edBook.setText("")
        edPrice.setText("")
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.closeDatabase()
    }
}