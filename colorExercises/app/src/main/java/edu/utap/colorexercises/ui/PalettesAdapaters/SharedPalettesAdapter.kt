package edu.utap.colorexercises.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import edu.utap.colorexercises.EditPaletteActivity
import edu.utap.colorexercises.R
import edu.utap.colorexercises.model.Palette

class SharedPalettesAdapter(private val context: Context, private val palettes: List<Palette>)
    : PalettesAdapter(context, palettes) {


    override fun onClick(palette: Palette?) {
        val intent = Intent(context, EditPaletteActivity::class.java)
        val extras = Bundle()
        extras.putString("id", palette?.id)
        extras.putString("name", palette?.name)
        extras.putString("username", palette?.ownerUserName)
        extras.putString("ownerUserId", palette?.ownerUserID)
        extras.putStringArray("favoritedUserIds", palette?.favoritedUsersList?.toTypedArray())
        extras.putStringArray("keywords", palette?.keywords?.toTypedArray())
        extras.putStringArray("palette", palette?.colors?.toTypedArray())
        intent.putExtras(extras)
        val result = 1
        context.startActivity(intent)
    }

    override fun getLayoutId(): Int {
        return R.layout.palette_list_item
    }

    override fun bind(view: View, palette: Palette?) {
        val nameView = view.findViewById<TextView>(R.id.name)
        val usernameView = view.findViewById<TextView>(R.id.username)
        val tagsView = view.findViewById<TextView>(R.id.tags)
        val colorsView = view.findViewById<LinearLayout>(R.id.colors)

        nameView.text = palette?.name
        usernameView.text = palette?.ownerUserName
        tagsView.text = palette?.keywords?.joinToString()

        colorsView.removeAllViews()
        palette?.colors?.forEach {
            colorsView.addView(createColorView(0, it))
        }
    }
}
