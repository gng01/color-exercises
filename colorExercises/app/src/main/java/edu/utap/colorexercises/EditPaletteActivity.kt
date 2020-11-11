package edu.utap.colorexercises

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.View.generateViewId
import android.widget.GridLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import edu.utap.colorexercises.model.MainViewModel
import edu.utap.colorexercises.model.Palette
import kotlinx.android.synthetic.main.activity_edit_palette.*

class EditPaletteActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private var palette = Palette()
    private var colorData = HashMap<Int, String>()
    private val defaultColors = mutableListOf<String>("#000000", "#555555", "#999999", "#EEEEEE")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_palette)

        val sourceIntent = intent
        val sourceBundle = sourceIntent.extras
        val colors = sourceBundle?.getStringArray("palette")

        palette.colors = colors?.toMutableList() ?: defaultColors

        palette.colors.toList().let { populateColors(it) }

        initSaveTrigger()
    }

    private fun initSaveTrigger() {
        saveTrigger.setOnClickListener {
            palette.colors = ArrayList<String>(colorData.values)
            palette.name = name.text.toString()
            palette.keywords = tags.text.split(",").map { it -> it.trim() }.toMutableList()

            viewModel.savePalette(palette)
        }
    }

    private fun populateColors(colors: List<String>) {
        colors.forEach{
            val view = createColorView(it)
            palette_colors.addView(view)
        }
    }

    private fun createColorView(color: String): TextView {
        val id = generateViewId()

        var view = TextView(this)
        view.id = id
        view.layoutParams = GridLayout.LayoutParams(
            GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f),
            GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f),
        )

        view.setBackgroundColor(Color.parseColor(color))
        view.height = 500

        val triggerColor = getBgColor(view)

        view.setOnClickListener{
            val intent = Intent(this, EditColorActivity::class.java)
            val extras = Bundle()
            extras.putInt(EditColorActivity.originalColorKey, triggerColor)
            extras.putInt(EditColorActivity.viewIdKey, id)

            intent.putExtras(extras)
            val result = 1
            startActivityForResult(intent, result)
        }

        registerColorData(id, color)

        return view;
    }

    private fun registerColorData(id: Int, color: String) {
        // need data to go along with color views. used for saving
        colorData[id] = color
    }

    override fun onStart() {
        super.onStart()
    }

    private fun getBgColor(view: View): Int {
        return (view.background as ColorDrawable).color
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data?.extras?.apply{
            val color = getInt(EditColorActivity.colorKey)
            val id = getInt(EditColorActivity.viewIdKey)

            val view = findViewById<TextView>(id)
            view.setBackgroundColor(color)

            registerColorData(id, String.format("#%06X", 0xFFFFFF and color))
        }
    }
}