package edu.utap.colorexercises

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View.generateViewId
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import edu.utap.colorexercises.model.FavoritedPalette
import edu.utap.colorexercises.model.MainViewModel
import edu.utap.colorexercises.model.Palette
import kotlinx.android.synthetic.main.activity_edit_palette.*

class EditPaletteActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private var palette = Palette()
    private var colorData = HashMap<Int, String>()
    private val defaultColors = mutableListOf<String>("#000000", "#555555", "#999999", "#EEEEEE")
    private var isNewPalette = true

    private fun isPaletteOwner() : Boolean {
        return palette.ownerUserID == null || palette.ownerUserID == FirebaseAuth.getInstance().currentUser?.uid
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sourceIntent = intent
        val sourceBundle = sourceIntent.extras
        val colors = sourceBundle?.getStringArray("palette")

        isNewPalette = colors.isNullOrEmpty()

        palette = initPalette(sourceBundle)

        initUi(palette)
    }

    private fun initUi(palette: Palette) {
        // we can refactor later, but lets try sharing this activity between viewing and editing palettes
        setContentView(if (isPaletteOwner()) R.layout.activity_edit_palette else R.layout.activity_view_palette)

        initSaveTrigger()

        initLayout()

        populate(palette)
    }
    
    private fun initLayout() {
        findViewById<TextView>(R.id.heading)?.text = (if (isNewPalette) "Create" else "Edit") + " Palette"
    }

    private fun initPalette(bundle: Bundle?) : Palette {
        val colors = bundle?.getStringArray("palette")?.toMutableList()

        val addedColor = bundle?.getString("addedColor")

        var palette = Palette()

        palette.id = bundle?.getString("id")
        palette.name = bundle?.getString("name")
        palette.favoritedUsersList = bundle?.getStringArray("favoritedUserIds")?.toMutableList() ?: mutableListOf<String>()
        palette.keywords = bundle?.getStringArray("keywords")?.toMutableList() ?: mutableListOf<String>()
        palette.ownerUserID = bundle?.getString("ownerUserId")

        palette.colors = colors ?: defaultColors

        if (addedColor != null) {
            palette.colors.add(addedColor)
        }

        return palette
    }

    private fun populate(palette: Palette) {
        // again, not a fan of doing it this way, where we check if the user is viewing or editing
        if (isPaletteOwner()) {
            findViewById<EditText>(R.id.name).setText(palette.name)

            findViewById<EditText>(R.id.tags).setText(palette.keywords.joinToString())
        } else {
            findViewById<TextView>(R.id.name).setText(palette.name)

            findViewById<TextView>(R.id.username).setText(palette.ownerUserName)
        }

        palette.colors.toList().let { populateColors(it) }
    }

    private fun initSaveTrigger() {
        // another use of if-statement for edit vs view
        if (isPaletteOwner())
            findViewById<Button>(R.id.saveTrigger)?.setOnClickListener {
                palette.colors = ArrayList<String>(colorData.values)
                palette.name = name.text.toString()
                palette.keywords = tags.text.split(",").map { it -> it.trim() }.toMutableList()

                val user = FirebaseAuth.getInstance().currentUser
                palette.ownerUserID = user?.uid
                palette.ownerUserName = user?.displayName

                viewModel.savePalette(palette, { onSave() })
            }
        else
            // add to favorites
            findViewById<Button>(R.id.copyTrigger)?.setOnClickListener {
                val user = FirebaseAuth.getInstance().currentUser

                if (user != null) {
                    viewModel.AddToFavoritePalettes(convertPaletteToFavoritedPalette(palette))

                    if (!palette.favoritedUsersList.contains(user.uid)) {
                        palette.favoritedUsersList.add(user.uid)
                        viewModel.savePalette(palette, { onSave() })
                    }
                }
            }
    }

    private fun convertPaletteToFavoritedPalette(palette: Palette) : FavoritedPalette {
        var favoritedPalette = FavoritedPalette()
        favoritedPalette.id = palette.id
        favoritedPalette.name = palette.name
        favoritedPalette.colors = palette.colors

        return favoritedPalette
    }

    private fun onSave() {
        finish()
    }

    private fun populateColors(colors: List<String>) {
        colors.forEach{
            val view = createColorView(it)
            palette_colors.addView(view)
        }
        palette_colors.addView(createAddColorView())
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

    private fun createAddColorView(): TextView {
        // copied code from createColorView. maybe refactor
        val id = generateViewId()

        var view = TextView(this)
        view.id = id
        view.textSize = 25f
        view.setTextColor(Color.WHITE)
        view.gravity = Gravity.CENTER
        view.layoutParams = GridLayout.LayoutParams(
            GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f),
            GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f),
        )

        view.setBackgroundColor(Color.BLACK)
        view.text = "+"
        view.height = 500

        view.setOnClickListener{
            val intent = Intent(this, EditColorActivity::class.java)
            val extras = Bundle()
            extras.putInt(EditColorActivity.originalColorKey, Color.BLACK)
            extras.putInt(EditColorActivity.viewIdKey, id)

            intent.putExtras(extras)
            val result = 2
            startActivityForResult(intent, result)
        }

        return view;
    }

    private fun registerColorData(id: Int, color: String) {
        // need data to go along with color views. used for saving
        colorData[id] = color
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data?.extras?.apply{
            val color = getInt(EditColorActivity.colorKey)
            val colorHex = String.format("#%06X", 0xFFFFFF and color)

            var id : Int? = null

            if (requestCode == 1) {
                id = getInt(EditColorActivity.viewIdKey)
                val view = findViewById<TextView>(id)

                view.setBackgroundColor(color)
            } else if (requestCode == 2) {
                id = generateViewId()
                val view = createColorView(colorHex)

                palette_colors.removeViewAt(palette_colors.childCount - 1)
                palette_colors.addView(view)
                palette_colors.addView(createAddColorView())
            }

            if (id != null)
                registerColorData(id, colorHex)
        }
    }
}