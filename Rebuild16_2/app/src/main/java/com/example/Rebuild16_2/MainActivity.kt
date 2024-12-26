package com.example.Rebuild16_2

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.Rebuild16_2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val items = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var binding: ActivityMainBinding

    // 使用companion object存放常量
    companion object {
        private val CONTENT_URI = Uri.parse("content://com.example.Rebuild16")
        private const val EMPTY_FIELD_ERROR = "欄位請勿留空"
        private const val EMPTY_BOOK_ERROR = "書名請勿留空"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupWindowInsets()
        initializeAdapter()
        setupListeners()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initializeAdapter() {
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        binding.listView.adapter = adapter
    }

    private fun setupListeners() {
        with(binding) {
            btnInsert.setOnClickListener { handleInsert() }
            btnUpdate.setOnClickListener { handleUpdate() }
            btnDelete.setOnClickListener { handleDelete() }
            btnQuery.setOnClickListener { handleQuery() }
        }
    }

    private fun handleInsert() {
        val (name, price) = getBookDetails()
        if (name.isEmpty() || price.isEmpty()) {
            showToast(EMPTY_FIELD_ERROR)
            return
        }

        val values = ContentValues().apply {
            put("book", name)
            put("price", price)
        }

        contentResolver.insert(CONTENT_URI, values)?.let {
            showToast("新增:$name,價格:$price")
            cleanEditText()
        } ?: showToast("新增失敗")
    }

    private fun handleUpdate() {
        val (name, price) = getBookDetails()
        if (name.isEmpty() || price.isEmpty()) {
            showToast(EMPTY_FIELD_ERROR)
            return
        }

        val values = ContentValues().apply {
            put("price", price)
        }

        val count = contentResolver.update(CONTENT_URI, values, name, null)
        if (count > 0) {
            showToast("更新:$name,價格:$price")
            cleanEditText()
        } else {
            showToast("更新失敗")
        }
    }

    private fun handleDelete() {
        val name = binding.edBook.text.toString()
        if (name.isEmpty()) {
            showToast(EMPTY_BOOK_ERROR)
            return
        }

        val count = contentResolver.delete(CONTENT_URI, name, null)
        if (count > 0) {
            showToast("刪除:$name")
            cleanEditText()
        } else {
            showToast("刪除失敗")
        }
    }

    private fun handleQuery() {
        val name = binding.edBook.text.toString()
        val selection = name.ifEmpty { null }

        contentResolver.query(CONTENT_URI, null, selection, null, null)?.use { cursor ->
            cursor.moveToFirst()
            items.clear()
            showToast("共有${cursor.count}筆資料")

            repeat(cursor.count) {
                items.add("書名:${cursor.getString(0)}\t\t\t\t價格:${cursor.getInt(1)}")
                cursor.moveToNext()
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun getBookDetails(): Pair<String, String> {
        return Pair(
            binding.edBook.text.toString(),
            binding.edPrice.text.toString()
        )
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private fun cleanEditText() {
        binding.edBook.text.clear()
        binding.edPrice.text.clear()
    }
}