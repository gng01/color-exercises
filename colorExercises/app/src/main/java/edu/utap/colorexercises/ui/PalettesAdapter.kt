package edu.utap.colorexercises.ui

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.colorexercises.R
import edu.utap.colorexercises.model.Palette
import kotlinx.android.synthetic.main.fragment_mypalettes.view.*
import kotlinx.android.synthetic.main.palette_list_item.view.*
import kotlin.random.Random

class PalettesAdapter(private val context: Context, private val palettes: List<Palette>)
    : RecyclerView.Adapter<PalettesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var nameView = view.findViewById<TextView>(R.id.name)
        val colorsView = view.findViewById<LinearLayout>(R.id.colors)

        init {
        }

        fun bind(position: Int) {
            nameView.text = palettes[position].name

            palettes[position].colors.forEach {
                colorsView.addView(createColorView(position, it))
            }
        }
    }

    override fun getItemCount(): Int {
        return palettes.count()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.palette_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    private fun createColorView(index: Int, color: String): ImageView {
        var view = ImageView(context)
        view.layoutParams = LinearLayout.LayoutParams(100, 100)
        view.setBackgroundColor(Color.parseColor(color))
        //view.id = index

        return view;
    }
}
