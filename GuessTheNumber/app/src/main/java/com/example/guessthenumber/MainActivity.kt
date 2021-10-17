package com.example.guessthenumber

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlin.reflect.typeOf

class MainActivity : AppCompatActivity() {

    lateinit var firstNum: EditText
    lateinit var secondNum: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firstNum = findViewById(R.id.first_num)
        secondNum = findViewById(R.id.second_num)
    }

    fun onStartGame(view: View) {
        val first_num_value = firstNum.text.toString().toInt()
        val second_num_value = secondNum.text.toString().toInt()
        if (first_num_value >= second_num_value) {
            Toast.makeText(this, "Start of the range cannot be equal or greater then the end of the interval!", Toast.LENGTH_LONG).show()
            return
        } else if (second_num_value - first_num_value == 1) {
            Toast.makeText(this, "Length of interval should be greater then 1!", Toast.LENGTH_LONG).show()
            return
        }
        Log.d("first_num_value", first_num_value.toString())
        Log.d("second_num_value", second_num_value.toString())
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("start", first_num_value)
        intent.putExtra("end", second_num_value)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        firstNum.setText("")
        secondNum.setText("")
    }
}