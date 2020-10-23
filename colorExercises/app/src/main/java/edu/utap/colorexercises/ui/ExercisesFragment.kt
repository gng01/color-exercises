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

    private var dummyTitle = "Match color: click on the color that matches the color of center circle"
    private lateinit var correctPosition: List<Boolean>
    private val exerciseSet = ExerciseSet()
    private val setSize = 10 //<=12


    companion object {
        val refreshKey = "RefreshKey"
        val exercisesFragmentKey = "ExercisesFragment"
        fun newInstance(): ExercisesFragment {
            return ExercisesFragment()
        }
    }

    private fun SetTitle(root: View){
        val titleTV = root.findViewById<TextView>(R.id.txt_exerciseTitle)
        titleTV.text = dummyTitle
    }

    private fun initExerciseSet(){
        exerciseSet.NewAllGreyScaleSet(setSize,0.1)
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
        if(position>=setSize){
            view.visibility = View.GONE
            return
        }
        view.visibility = View.VISIBLE

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
                .replace(R.id.main_frame, resultFragment, exercisesFragmentKey)
                .addToBackStack(null)
                .commit()
        }
    }

    fun remoteRefresh(){
        initExerciseSet()
    }

    private fun initChildButtons(root: View){
        val arcLayout = root.findViewById<ArcLayout>(R.id.arc_layout)
        for (i in 0 until arcLayout.childCount) {
            //Log.d("XXX ExercisesFragment: ", "${i}'th layout")
            BindButton(arcLayout.getChildAt(i),i)

        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initExerciseSet()
        initChildButtons(view)
        BindMainColor(view)
        SetTitle(view)
    }
}