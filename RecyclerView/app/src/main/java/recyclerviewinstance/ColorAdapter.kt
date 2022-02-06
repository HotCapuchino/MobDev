package recyclerviewinstance

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerview.R
import com.nvt.color.ColorPickerDialog

class ColorAdapter(private val colorList: ArrayList<Int>, private val context: Context) :
    RecyclerView.Adapter<ColorAdapterItem>() {
    private var changeablePosition = -1
    private val adapterContext = this

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorAdapterItem {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_item, parent, false)

        return ColorAdapterItem(itemView, ::createColorPickerDialog)
    }

    override fun getItemCount(): Int {
        return colorList.size
    }

    override fun onBindViewHolder(holder: ColorAdapterItem, position: Int) {
        val color = colorList[position]

        holder.holderPosition = position
        holder.color = color
        holder.colorCodeView.text = "Color code: ${color.toString(16)}"
        holder.colorBoxView.setBackgroundColor(color)
    }

    private fun createColorPickerDialog(color: Int, position: Int): ColorPickerDialog {
        changeablePosition = position
        return ColorPickerDialog(this.context, color, false, colorPickerListener)
    }

    private val colorPickerListener = object : ColorPickerDialog.OnColorPickerListener {
        override fun onCancel(dialog: ColorPickerDialog?) {
            //
        }

        override fun onOk(dialog: ColorPickerDialog?, color: Int) {
            if (changeablePosition >= 0) {
                colorList[changeablePosition] = color
            }

            adapterContext.notifyDataSetChanged()
        }

    }

}