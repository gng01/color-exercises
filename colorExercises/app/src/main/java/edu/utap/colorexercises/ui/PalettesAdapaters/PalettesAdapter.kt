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

abstract class PalettesAdapter(private val context: Context, private val palettes: List<Palette>)
    : RecyclerView.Adapter<PalettesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var palette : Palette? = null
        val view = view

        init {
            view.setOnClickListener{
                onClick(palette)
            }
        }

        fun bind(position: Int) {
            palette = palettes[position]

            bind(view, palette)
        }
    }

    abstract fun onClick(palette: Palette?)

    abstract fun bind(view: View, palette: Palette?)

    abstract fun getLayoutId() : Int

    override fun getItemCount(): Int {
        return palettes.count()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(getLayoutId(), parent, false)
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
