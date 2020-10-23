package edu.utap.colorexercises.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ogaclejapan.arclayout.ArcLayout
import edu.utap.colorexercises.R
import edu.utap.colorexercises.model.ExerciseSet
import edu.utap.colorexercises.model.OneColor
import kotlin.random.Random

/**
 * Exercise Fragment that handles view bindings
 * Not using separate adapter because it is not supported by ArcLayout
 */
class ExercisesFragment : Fragment(R.layout.fragment_exercises) {

//    private val dummyColorList = listOf<String>("#EDD8F2","#D8F2E5","#F2D8E4","#D8DFF2","#D8F2ED","#F2F2D8")
//    private var dummyMatch = Random.nextInt(dummyColorList.size)
//    private var dummyMainColor = OneColor(dummyColorList[dummyMatch])
    private var dummyTitle = "Match color: click on the color that matches the color of center circle"
    private lateinit var correctPosition: List<Boolean>
    private val exerciseSet = ExerciseSet()

    companion object {
        fun newInstance(): ExercisesFragment {
            return ExercisesFragment()
        }
    }

    private fun SetTitle(root: View){
        val titleTV = root.findViewById<TextView>(R.id.txt_exerciseTitle)
        titleTV.text = dummyTitle
    }

    private fun initExerciseSet(){
//        val colorList = dummyColorList.map {
//            OneColor(it)
//        }
//        exerciseSet.AddColorList(dummyMainColor,colorList)
        exerciseSet.NewAllGreyScaleSet(6,0.1)
        this.correctPosition = exerciseSet.getCorrectPosition()
    }

    private fun BindMainColor(root: View){
        val mainColorButton = root.findViewById<Button>(R.id.btn_exercise_main_color)
        mainColorButton.background.setTint(exerciseSet.getMainColor().getInt())
        mainColorButton.setOnClickListener {
            Toast.makeText(this.context, "Clicked on center", Toast.LENGTH_LONG).show()
        }
    }
    private fun BindButton(view: View, position: Int){
        //view.setBackgroundColor(Color.parseColor(dummyColorList[position]))
        view.background.setTint(exerciseSet.getColorList()[position].getInt())
        view.setOnClickListener {
            val resultFragment = ExerciseResultFragment()
            val args = Bundle()
            // change common language of color to hsl
            args.putFloatArray(ExerciseResultFragment.mainColorKey,exerciseSet.getMainColor().hsl)
            args.putFloatArray(ExerciseResultFragment.selectedColorKey,exerciseSet.getColorList()[position].hsl)
            if (this.correctPosition[position]) {
                args.putString(ExerciseResultFragment.titleKey, "Match!")
                args.putBoolean(ExerciseResultFragment.resultStateKey, true)
            }else{
                args.putString(ExerciseResultFragment.titleKey, "Try Again!")
                args.putBoolean(ExerciseResultFragment.resultStateKey, false)
            }
            resultFragment.arguments = args
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.main_frame, resultFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun initChildButtons(root: View){
        val arcLayout = root.findViewById<ArcLayout>(R.id.arc_layout)
        for (i in 0 until arcLayout.childCount) {
            //Log.d("XXX ExercisesFragment: ", "${i}'th layout")
            BindButton(arcLayout.getChildAt(i),i)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initExerciseSet()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initChildButtons(view)
        BindMainColor(view)
        SetTitle(view)
    }
}