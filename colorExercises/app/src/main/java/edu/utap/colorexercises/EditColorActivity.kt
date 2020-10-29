package edu.utap.colorexercises

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.larswerkman.holocolorpicker.*

class EditColorActivity : AppCompatActivity(), ColorPicker.OnColorChangedListener {

    private var picker: ColorPicker? = null
    private var saturationBar: SaturationBar? = null
    private var valueBar: ValueBar? = null
    private var button: Button? = null
    private var text: TextView? = null

    private var id: Int = 0

    companion object {
        val originalColorKey = "originalColor"
        val colorKey = "editcolor"
        val viewIdKey = "viewIdKey"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_color)
        picker = findViewById<View>(R.id.picker) as ColorPicker
        saturationBar = findViewById<View>(R.id.saturationbar) as SaturationBar
        valueBar = findViewById<View>(R.id.valuebar) as ValueBar
        button = findViewById<View>(R.id.button1) as Button
        picker!!.addSaturationBar(saturationBar)
        picker!!.addValueBar(valueBar)
        picker!!.setOnColorChangedListener(this)
        button!!.setOnClickListener {
            picker!!.oldCenterColor = picker!!.color

            onFinish(id, picker!!.color)
        }
    }

    override fun onStart() {
        super.onStart()

        val sourceIntent = intent
        val sourceBundle = sourceIntent.extras
        val originalColor = sourceBundle?.getInt(originalColorKey)
        this.id = sourceBundle?.getInt(viewIdKey)!!

        picker = findViewById<View>(R.id.picker) as ColorPicker

        if (originalColor != null) {
            picker!!.oldCenterColor = originalColor
        }
    }

    fun onFinish(id: Int, color: Int) {
        Intent().apply{
            putExtra(colorKey, color)
            putExtra(viewIdKey, id)

            setResult(RESULT_OK, this)
        }

        finish()
    }

    override fun onColorChanged(color: Int) {
    }
}