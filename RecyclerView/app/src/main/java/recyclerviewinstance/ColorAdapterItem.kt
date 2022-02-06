package recyclerviewinstance

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerview.R
import com.nvt.color.ColorPickerDialog

class ColorAdapterItem(view: View, callback: (color: Int, position: Int) -> ColorPickerDialog) : RecyclerView.ViewHolder(view) {
    val colorCodeView: TextView = view.findViewById(R.id.color_code)
    val colorBoxView: ImageView = view.findViewById(R.id.color_box)
    var color: Int = 0
    var holderPosition: Int = -1

    private val colorBoxClick = View.OnClickListener {
        if (holderPosition >= 0) {
            callback(color, holderPosition).show()
        }
    }

    init {
        colorBoxView.setOnClickListener(colorBoxClick)
    }
}
