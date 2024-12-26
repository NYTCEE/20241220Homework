package com.example.Rebuild17

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.Rebuild17.databinding.ActivityMainBinding
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val client = OkHttpClient()

    // 使用sealed class處理API結果狀態
    sealed class ApiResult {
        data class Success(val data: AirQualityData) : ApiResult()
        data class Error(val message: String) : ApiResult()
    }

    // 數據類別整合
    data class AirQualityData(val result: Result) {
        data class Result(val records: List<Record>) {
            data class Record(val SiteName: String, val Status: String) {
                fun toDisplayString() = "地區：$SiteName, 狀態：$Status"
            }
        }
    }

    companion object {
        private const val API_URL = "https://api.italkutalk.com/api/air"
        private const val DIALOG_TITLE = "臺北市空氣品質"
        private const val ERROR_PREFIX = "查詢失敗: "
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupWindowInsets()
        setupClickListeners()
    }

    private fun setupUI() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupClickListeners() {
        binding.btnQuery.setOnClickListener {
            it.isEnabled = false
            fetchAirQualityData()
        }
    }

    private fun fetchAirQualityData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val result = executeNetworkRequest()
                withContext(Dispatchers.Main) {
                    handleApiResult(result)
                }
            } finally {
                withContext(Dispatchers.Main) {
                    binding.btnQuery.isEnabled = true
                }
            }
        }
    }

    private suspend fun executeNetworkRequest(): ApiResult {
        return try {
            val request = Request.Builder()
                .url(API_URL)
                .build()

            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }

            response.body?.string()?.let { json ->
                val data = Gson().fromJson(json, AirQualityData::class.java)
                ApiResult.Success(data)
            } ?: ApiResult.Error("回應數據為空")

        } catch (e: IOException) {
            ApiResult.Error(e.localizedMessage ?: "網絡請求失敗")
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "未知錯誤")
        }
    }

    private fun handleApiResult(result: ApiResult) {
        when (result) {
            is ApiResult.Success -> showAirQualityDialog(result.data)
            is ApiResult.Error -> showError(result.message)
        }
    }

    private fun showAirQualityDialog(data: AirQualityData) {
        val items = data.result.records
            .map { it.toDisplayString() }
            .toTypedArray()

        AlertDialog.Builder(this)
            .setTitle(DIALOG_TITLE)
            .setItems(items, null)
            .show()
    }

    private fun showError(message: String) {
        Toast.makeText(
            this,
            ERROR_PREFIX + message,
            Toast.LENGTH_SHORT
        ).show()
    }
}