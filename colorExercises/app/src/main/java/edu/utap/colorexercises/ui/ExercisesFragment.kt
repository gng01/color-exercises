package edu.utap.colorexercises.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ogaclejapan.arclayout.ArcLayout
import edu.utap.colorexercises.R
import kotlin.random.Random

/**
 * Exercise Fragment that handles view bindings
 * Not using separate adapter because it is not supported by ArcLayout
 */
class ExercisesFragment : Fragment(R.layout.fragment_exercises) {

    private val dummyColorList = listOf<String>("#EDD8F2","#D8F2E5","#F2D8E4","#D8DFF2","#D8F2ED","#F2F2D8")
    private var dummyMatch = Random.nextInt(dummyColorList.size)
    private var dummyCenter = dummyColorList[dummyMatch]
    private var dummyTitle = "Match color: click on the color that matches the color of center circle"


    companion object {
        fun newInstance(): ExercisesFragment {
            return ExercisesFragment()
        }
    }

    private fun SetTitle(view: TextView){
        view.text = dummyTitle
    }

    private fun BindCenter(view: View){
        view.background.setTint(Color.parseColor(dummyCenter))
        view.setOnClickListener {
            Toast.makeText(this.context, "Clicked on center", Toast.LENGTH_LONG).show()
        }
    }
    private fun BindButton(view: View, position: Int){
        //view.setBackgroundColor(Color.parseColor(dummyColorList[position]))
        view.background.setTint(Color.parseColor(dummyColorList[position]))
        view.setOnClickListener {
            if (position==dummyMatch) {
                Toast.makeText(this.context, "Correct!", Toast.LENGTH_LONG)
                    .show()
            }else{
                Toast.makeText(this.context, "Wrong!", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun initChildButtons(arcLayout: ArcLayout){
        for (i in 0 until arcLayout.childCount) {
            //Log.d("XXX ExercisesFragment: ", "${i}'th layout")
            BindButton(arcLayout.getChildAt(i),i)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arcLayout = view.findViewById<ArcLayout>(R.id.arc_layout)
        val centerButton = view.findViewById<Button>(R.id.btn_exercise_center)
        val titleTV = view.findViewById<TextView>(R.id.txt_exerciseTitle)
        initChildButtons(arcLayout)
        BindCenter(centerButton)
        SetTitle(titleTV)
    }
}