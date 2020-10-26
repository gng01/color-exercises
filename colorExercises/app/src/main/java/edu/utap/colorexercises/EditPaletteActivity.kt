package edu.utap.colorexercises

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edit_palette.*

class EditPaletteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_palette)

    }

    override fun onStart() {
        super.onStart()

        val buttonColor = getBgColor(editColor)

        editColor.setOnClickListener{
            val intent = Intent(this, EditColorActivity::class.java)
            val extras = Bundle()
            extras.putInt(EditColorActivity.originalColorKey, buttonColor)
            intent.putExtras(extras)
            val result = 1
            startActivityForResult(intent, result)
        }
    }

    private fun getBgColor(view: View): Int {
        return (view.background as ColorDrawable).color
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data?.extras?.apply{
            val color = getInt(EditColorActivity.colorKey)

            val view = editColor
            view.setBackgroundColor(color)
        }
    }
}