package edu.utap.colorexercises.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.utap.colorexercises.EditPaletteActivity
import edu.utap.colorexercises.R
import edu.utap.colorexercises.model.Palette

class SharedPalettesAdapter(private val context: Context, private val palettes: List<Palette>)
    : PalettesAdapter(context, palettes) {

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
                extras.putString("ownerUserId", palette?.ownerUserID)
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
}
