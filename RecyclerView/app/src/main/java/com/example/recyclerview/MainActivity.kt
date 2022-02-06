package com.example.recyclerview

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.nvt.color.ColorPickerDialog
import recyclerviewinstance.ColorAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var colorBttn: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var colorPicker: ColorPickerDialog
    private val colorList = ArrayList<Int>()
    private lateinit var colorAdapter: ColorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        colorBttn = findViewById(R.id.color_picker)
        colorBttn.setOnClickListener(colorClickListener)
        recyclerView = findViewById(R.id.recycler_view)
        colorList.add(Color.BLACK)

        // reference to ColorPickerModal - https://github.com/novatien/SmartColorPicker
        colorPicker = ColorPickerDialog(this, Color.WHITE, false, colorPickerListener)
        colorAdapter = ColorAdapter(colorList, this)
        recyclerView.adapter = colorAdapter
    }

    private val colorClickListener = View.OnClickListener {
        colorPicker.show()
    }

    private val colorPickerListener = object : ColorPickerDialog.OnColorPickerListener {
        override fun onCancel(dialog: ColorPickerDialog?) {
            //
        }

        override fun onOk(dialog: ColorPickerDialog?, color: Int) {
            Log.d("Color List", colorList.toString())
            colorList.add(color)

            colorAdapter.notifyDataSetChanged()
        }

    }
}