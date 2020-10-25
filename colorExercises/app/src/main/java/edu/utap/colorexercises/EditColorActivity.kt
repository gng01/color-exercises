package edu.utap.colorexercises

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_color)
        picker = findViewById<View>(R.id.picker) as ColorPicker
        svBar = findViewById<View>(R.id.svbar) as SVBar
        opacityBar = findViewById<View>(R.id.opacitybar) as OpacityBar
        button = findViewById<View>(R.id.button1) as Button
        text = findViewById<View>(R.id.textView1) as TextView
        picker!!.addSVBar(svBar)
        picker!!.addOpacityBar(opacityBar)
        picker!!.setOnColorChangedListener(this)
        button!!.setOnClickListener {
            fun onClick(v: View?) {
                text!!.setTextColor(picker!!.color)
                picker!!.oldCenterColor = picker!!.color
            }
        }
    }

    override fun onColorChanged(color: Int) {
        //gives the color when it's changed.
    }
}