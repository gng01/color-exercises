package edu.utap.colorexercises.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.colorexercises.EditPaletteActivity
import edu.utap.colorexercises.R
import edu.utap.colorexercises.model.Palette
import kotlinx.android.synthetic.main.fragment_mypalettes.*
import kotlinx.android.synthetic.main.fragment_mypalettes.view.*
import kotlinx.android.synthetic.main.palette_list_item.view.*
import kotlin.random.Random

class PalettesAdapter(private val context: Context, private val palettes: List<Palette>)
    : RecyclerView.Adapter<PalettesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var nameView = view.findViewById<TextView>(R.id.name)
        private var tagsView = view.findViewById<TextView>(R.id.tags)
        val colorsView = view.findViewById<LinearLayout>(R.id.colors)
        var palette : Palette? = null

        init {
            view.setOnClickListener{
                val intent = Intent(context, EditPaletteActivity::class.java)
                val extras = Bundle()
                extras.putString("id", palette?.id)
                extras.putString("name", palette?.name)
                extras.putStringArray("keywords", palette?.keywords?.toTypedArray())
                extras.putStringArray("palette", palette?.colors?.toTypedArray())
                intent.putExtras(extras)
                val result = 1
                context.startActivity(intent)
            }
        }

        fun bind(position: Int) {
            palette = palettes[position]

            nameView.text = palettes[position].name

            tagsView.text = palettes[position].keywords.joinToString()

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

    protected fun createColorView(index: Int, color: String): ImageView {
        var view = ImageView(context)
        view.layoutParams = LinearLayout.LayoutParams(150, 150)
        view.setBackgroundColor(Color.parseColor(color))
        //view.id = index

        return view;
    }
}
