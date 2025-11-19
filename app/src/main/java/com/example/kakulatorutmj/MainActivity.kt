package com.example.kakulatorutmj

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.content.SharedPreferences
import org.json.JSONArray
import android.content.Intent
import kotlin.math.sqrt


class MainActivity : AppCompatActivity() {

    private lateinit var screen: TextView

    private var value1 = 0.0
    private var value2 = 0.0
    private var operator = ""
    private var input = ""
    private var display = ""

    private val PREFS_NAME = "calc_prefs"
    private val KEY_HISTORY = "calc_history"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        screen = findViewById(R.id.txtScreen)

        // ==== TOMBOL ANGKA ====
        setNumberButton(R.id.btn0)
        setNumberButton(R.id.btn1)
        setNumberButton(R.id.btn2)
        setNumberButton(R.id.btn3)
        setNumberButton(R.id.btn4)
        setNumberButton(R.id.btn5)
        setNumberButton(R.id.btn6)
        setNumberButton(R.id.btn7)
        setNumberButton(R.id.btn8)
        setNumberButton(R.id.btn9)


        findViewById<Button>(R.id.btnBackspace).setOnClickListener {
            if (display.isNotEmpty()) {
                display = display.dropLast(1)
                screen.text = display
            }

            if (input.isNotEmpty()) {
                input = input.dropLast(1)
            }
        }

// ==== TITIK DESIMAL ====
        findViewById<Button>(R.id.btnPoint).setOnClickListener {
            if (!input.contains(".")) {
                input += "."
                display += "."
                screen.text = display
            }
        }

        // ==== OPERATOR ====
        findViewById<Button>(R.id.btnAdd).setOnClickListener { setOperator("+") }
        findViewById<Button>(R.id.btnSubtract).setOnClickListener { setOperator("-") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { setOperator("×") }
        findViewById<Button>(R.id.btnDiv).setOnClickListener { setOperator(":") }

        // ==== PERSEN (%) ====
        findViewById<Button>(R.id.btnPercent).setOnClickListener {
            if (input.isNotEmpty()) {
                val value = input.toDouble() / 100
                input = value.toString()
                display = input
                screen.text = display

                saveHistoryRecord("$display% = $value")
            }
        }

// ==== AKAR KUADRAT (√) ====
        findViewById<Button>(R.id.btnSqrt).setOnClickListener {
            if (input.isNotEmpty()) {
                val number = input.toDouble()

                if (number < 0) {
                    screen.text = "INVALID"
                } else {
                    val result = sqrt(number)
                    val hasilStr = if (result % 1 == 0.0) result.toInt().toString() else result.toString()

                    input = hasilStr
                    display = hasilStr
                    screen.text = hasilStr

                    saveHistoryRecord("√$number = $hasilStr")
                }
            }
        }

        // ==== CLEAR ====
        findViewById<Button>(R.id.btnClear).setOnClickListener {
            input = ""
            display = ""
            value1 = 0.0
            value2 = 0.0
            operator = ""
            screen.text = ""
        }

        // ==== SAMA DENGAN (=) ====
        findViewById<Button>(R.id.btnEquals).setOnClickListener {
            if (input.isNotEmpty()) {
                value2 = input.toDouble()

                // Simpan ekspresi sebelum dihitung
                val expression = display

                hitung()

                val resultStr = screen.text.toString()

                // Simpan ke riwayat
                val record = "$expression = $resultStr"
                saveHistoryRecord(record)

                // Update tampilan
                display = resultStr
                input = resultStr
            }
        }

        // ==== TOMBOL HISTORY ====
        val btnHistory = findViewById<Button>(R.id.btnHistory)
        btnHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
    }



    private fun setNumberButton(id: Int) {
        findViewById<Button>(id).setOnClickListener {
            val text = (it as Button).text.toString()
            input += text
            display += text
            screen.text = display
        }
    }

    private fun setOperator(op: String) {
        if (input.isNotEmpty()) {
            value1 = input.toDouble()
            operator = op
            display += op
            screen.text = display
            input = ""
        }
    }

    private fun hitung() {
        val hasil = when (operator) {
            "+" -> value1 + value2
            "-" -> value1 - value2
            "×" -> value1 * value2
            ":" -> if (value2 != 0.0) value1 / value2 else 0.0
            else -> 0.0
        }

        val hasilStr = if (hasil % 1 == 0.0) hasil.toInt().toString() else hasil.toString()
        screen.text = hasilStr
    }


    private fun saveHistoryRecord(record: String) {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val json = prefs.getString(KEY_HISTORY, null)
        val arr = if (json == null) JSONArray() else JSONArray(json)

        arr.put(record)
        prefs.edit().putString(KEY_HISTORY, arr.toString()).apply()
    }

    private fun loadHistoryArray(): JSONArray {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val json = prefs.getString(KEY_HISTORY, null)
        return if (json == null) JSONArray() else JSONArray(json)
    }

    private fun clearHistory() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().remove(KEY_HISTORY).apply()
    }
}