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
import kotlin.properties.Delegates

class EditPaletteActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private var palette = Palette()
    private var colorData = HashMap<Int, String>()
    private val defaultColors = mutableListOf<String>("#000000", "#555555", "#999999", "#EEEEEE")
    private var isNewPalette = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_palette)

        val sourceIntent = intent
        val sourceBundle = sourceIntent.extras
        val colors = sourceBundle?.getStringArray("palette")

        isNewPalette = colors.isNullOrEmpty()

        UpdateViews()

        palette.id = sourceBundle?.getString("id").toString()
        palette.name = sourceBundle?.getString("name").toString()
        palette.keywords = sourceBundle?.getStringArray("keywords")?.toMutableList()!!

        palette.colors = colors?.toMutableList() ?: defaultColors

        populate(palette)

        initSaveTrigger()
    }

    private fun populate(palette: Palette) {
        name.setText(palette.name)

        tags.setText(palette.keywords.joinToString())

        palette.colors.toList().let { populateColors(it) }
    }

    private fun UpdateViews(){
        heading.text = (if (isNewPalette) "Create" else "Edit") + " Palette"
    }

    private fun initSaveTrigger() {
        saveTrigger.setOnClickListener {
            palette.colors = ArrayList<String>(colorData.values)
            palette.name = name.text.toString()
            palette.keywords = tags.text.split(",").map { it -> it.trim() }.toMutableList()

            viewModel.savePalette(palette, { onSave() })
        }
    }

    private fun onSave() {
        finish()
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

        view.setOnClickListener{
            val intent = Intent(this, EditColorActivity::class.java)
            val extras = Bundle()
            extras.putInt(EditColorActivity.originalColorKey, Color.parseColor(colorData[id]))
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