package edu.utap.colorexercises

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.larswerkman.holocolorpicker.ColorPicker
import com.larswerkman.holocolorpicker.OpacityBar
import com.larswerkman.holocolorpicker.SVBar

class EditColorActivity : AppCompatActivity(), ColorPicker.OnColorChangedListener {

    private var picker: ColorPicker? = null
    private var svBar: SVBar? = null
    private var opacityBar: OpacityBar? = null
    private var button: Button? = null
    private var text: TextView? = null

    companion object {
        val originalColorKey = "originalColor"
        val colorKey = "editcolor"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_color)
        picker = findViewById<View>(R.id.picker) as ColorPicker
        svBar = findViewById<View>(R.id.svbar) as SVBar
        opacityBar = findViewById<View>(R.id.opacitybar) as OpacityBar
        button = findViewById<View>(R.id.button1) as Button
        picker!!.addSVBar(svBar)
        picker!!.addOpacityBar(opacityBar)
        picker!!.setOnColorChangedListener(this)
        button!!.setOnClickListener {
            picker!!.oldCenterColor = picker!!.color

            onFinish(picker!!.color)
        }
    }

    override fun onStart() {
        super.onStart()

        val sourceIntent = intent
        val sourceBundle = sourceIntent.extras
        val originalColor = sourceBundle?.getInt(originalColorKey)

        picker = findViewById<View>(R.id.picker) as ColorPicker

        if (originalColor != null) {
            picker!!.oldCenterColor = originalColor
        }
    }

    fun onFinish(color: Int) {
        Intent().apply{
            putExtra(colorKey, color)

            setResult(RESULT_OK, this)
        }

        finish()
    }

    override fun onColorChanged(color: Int) {
    }
}