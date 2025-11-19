package com.example.kakulatorutmj

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import org.json.JSONArray

class HistoryActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var clearButton: Button

    private val PREFS_NAME = "calc_prefs"
    private val KEY_HISTORY = "calc_history"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        listView = findViewById(R.id.listHistory)
        clearButton = findViewById(R.id.btnClearHistory)

        loadHistory()

        clearButton.setOnClickListener {
            clearHistory()
            loadHistory()  // refresh list
        }
    }

    private fun loadHistory() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val json = prefs.getString(KEY_HISTORY, null)

        val arr = if (json == null) JSONArray() else JSONArray(json)

        val listData = ArrayList<String>()
        for (i in 0 until arr.length()) {
            listData.add(arr.getString(i))
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listData)
        listView.adapter = adapter
    }

    private fun clearHistory() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().remove(KEY_HISTORY).apply()
    }
}