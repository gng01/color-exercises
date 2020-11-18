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
import androidx.recyclerview.widget.RecyclerView
import edu.utap.colorexercises.EditPaletteActivity
import edu.utap.colorexercises.R
import edu.utap.colorexercises.model.Palette

class UserPalettesAdapter(private val context: Context, private val palettes: List<Palette>)
    : PalettesAdapter(context, palettes) {

    override fun onClick(palette: Palette?) {
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

    override fun getLayoutId(): Int {
        return R.layout.user_palette_list_item
    }

    override fun bind(view: View, palette: Palette?) {
        val nameView = view.findViewById<TextView>(R.id.name)
        val tagsView = view.findViewById<TextView>(R.id.tags)
        val colorsView = view.findViewById<LinearLayout>(R.id.colors)

        nameView.text = palette?.name
        tagsView.text = palette?.keywords?.joinToString()

        palette?.colors?.forEach {
            colorsView.addView(createColorView(0, it))
        }
    }
}
