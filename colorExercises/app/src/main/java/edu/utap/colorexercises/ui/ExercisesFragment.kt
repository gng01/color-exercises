package edu.utap.colorexercises.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.ogaclejapan.arclayout.ArcLayout
import edu.utap.colorexercises.R
import edu.utap.colorexercises.model.ExerciseSet

/**
 * Exercise Fragment that handles view bindings
 * Not using separate adapter because it is not supported by ArcLayout
 */
class ExercisesFragment : Fragment(R.layout.fragment_exercises) {

    private val debug = false
    private var dummyTitle = "Match color: click on the value that matches the value of center circle"
    private lateinit var accuracyList: List<Double>
    private val exerciseSet = ExerciseSet()
    private var accuracyThreshold = 90
    private var level = 0
    private var mode = "MATCHVALUE"
    private var difficultLevel = 30
    //private var progress = 0
    private lateinit var progressBar: ProgressBar
    private var roundsToLevelUp = 4
    private var levelsArray = mutableListOf<Int>(0)
    private var modesArray = modeMap.keys.toMutableList()
    private var levelsAdapter: ArrayAdapter<Int>? =null
    private var modesAdapter: ArrayAdapter<String>? =null
    private lateinit var levelsSpinner: Spinner
    private lateinit var modesSpinner: Spinner
    private var levelsMap = mutableMapOf<String,MutableList<Int>>(mode to levelsArray)


    companion object {
        val refreshKey = "RefreshKey"
        val exercisesFragmentKey = "ExercisesFragment"
        val modeMap = mapOf("Match Value" to "MATCHVALUE","Match Hue" to "MATCHHUE")
        var progress = 0
        fun newInstance(): ExercisesFragment {
            return ExercisesFragment()
        }
    }

    private fun setDebugMode(){
        if (this.debug) {
            this.roundsToLevelUp = 1
            this.accuracyThreshold = 0
        }
    }

    private fun SetTitle(root: View){
        val titleTV = root.findViewById<TextView>(R.id.txt_exerciseTitle)
        titleTV.text = dummyTitle
    }

    private fun setProgress(givenProgress: Int): Boolean {
        progressBar.progress = givenProgress
        //Log.d("XXX ExercisesFragment: ", "progress ${givenProgress}")
        if(givenProgress>=100){
            //Log.d("XXX ExercisesFragment: ", "incrementing level to ${level+1}")
            exerciseSet.setLevel(level+1)
            updateLevels(level+1)
            progress=0
            progressBar.progress = progress
            //trick to get around spinner.setSelection not working issue
            return true
        }
        // return: if leveled up, variable for showing levelUpAnimation
        return false
    }

    private fun updateLevels(level: Int){
        //Log.d("XXX ExercisesFragment: ", "levels before ${levelsMap.entries}, curlevel $level")
        //Log.d("XXX ExercisesFragment: ", "levels ${levelsMap.entries}")
        if(!levelsMap.containsKey(this.mode)){
            levelsMap.put(this.mode, mutableListOf(0))
        }else {
            levelsMap[this.mode]!!.apply {
                if(!this.contains(level)) {
                    this.add(level)


                }
            }
        }
        levelsArray = levelsMap[this.mode]!!
        Log.d("XXX ExercisesFragment: updateLevels", "levelsArray: ${levelsArray.toList()}, ${levelsArray.last()}")
        levelsAdapter!!.notifyDataSetChanged()
        levelsSpinner.post(Runnable {
            kotlin.run { levelsSpinner.setSelection(levelsArray.last()) }
        })
        this.level = level

    }



    private fun initProgressBar(root: View){
        this.progressBar = root.findViewById(R.id.progressbar_exercises)
        this.setProgress(progress)
    }

    private fun initLevelsMap(){
        for (value in modeMap.values){
            levelsMap.put(value, mutableListOf(0))
        }
    }

    private fun initExerciseSet(){
        exerciseSet.setLevel(this.level)
        exerciseSet.setMode(this.mode)
        exerciseSet.setDifficultLevel(this.difficultLevel)
        exerciseSet.NewSet()
        this.accuracyList = exerciseSet.getAccuracyList()
    }

    private fun initLevelsSpinner(root: View){
        levelsSpinner = root.findViewById<Spinner>(R.id.spinner_levels)
        levelsAdapter = ArrayAdapter(this.requireContext(), R.layout.spinner_row,levelsArray)
        levelsAdapter!!.notifyDataSetChanged()
        levelsSpinner.adapter = levelsAdapter
        levelsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (levelsArray[p2]==level) return
                level = levelsArray[p2]
                Log.d("XXX ExercisesFragment: levelsSpinner", "levelsArray: ${levelsArray.toList()}")
                refresh()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }


    private fun initModesSpinner(root: View){
        modesSpinner = root.findViewById<Spinner>(R.id.spinner_modes)
        modesAdapter = ArrayAdapter(this.requireContext(), R.layout.spinner_modes_row,modesArray)
        modesAdapter!!.notifyDataSetChanged()
        modesSpinner.adapter = modesAdapter
        modesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //TODO: had bug when switching from MATCHHUE to MATCHVALUE: index out of bound: index 5 for size 5
                // however, not able to reproduce
                if (modeMap[modesArray[p2]]==mode) return
                mode = modeMap[modesArray[p2]] ?: error("mode doesn't exist")
                if (levelsMap[mode]==null){
                    updateLevels(0)
                }else{
                    levelsArray = levelsMap[mode]!!
                    levelsAdapter?.notifyDataSetChanged()
                    updateLevels(levelsArray.last())
                }

                progress=0
                levelsSpinner.post(Runnable {
                    kotlin.run { levelsSpinner.setSelection(levelsArray.last()) }
                })
                Log.d("XXX ExercisesFragment: ModeSpinner", "levelsArray: ${levelsArray.toList()}")
                refresh()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    private fun BindMainColor(root: View){
        val mainColorButton = root.findViewById<Button>(R.id.btn_exercise_main_color)
        mainColorButton.background.setTint(exerciseSet.getMainColor().getInt())
        mainColorButton.setOnClickListener {
            Toast.makeText(this.context, "Clicked on center", Toast.LENGTH_LONG).show()
        }
    }
    private fun BindButton(view: View, position: Int){
        if(position>=exerciseSet.getSize()){
            view.visibility = View.GONE
            return
        }
        view.visibility = View.VISIBLE

        view.background.setTint(exerciseSet.getColorList()[position].getInt())
        view.setOnClickListener {
            val resultFragment = ExerciseResultFragment()
            val args = Bundle()
            val mainColor = exerciseSet.getMainColor()
            val selectedColor = exerciseSet.getColorList()[position]
            val accuracy = accuracyList[position]
            args.putFloatArray(ExerciseResultFragment.mainColorKey,mainColor.hsl)
            args.putFloatArray(ExerciseResultFragment.selectedColorKey,selectedColor.hsl)
            if (accuracy>=this.accuracyThreshold) {
                progress+=accuracy.toInt()/this.roundsToLevelUp.toInt()
                var leveledUp = setProgress(progress)
                args.putBoolean(ExerciseResultFragment.leveledUpKey, leveledUp)
                args.putString(ExerciseResultFragment.titleKey, String.format("Match! Accuracy: %.1f",accuracy))
                args.putBoolean(ExerciseResultFragment.resultStateKey, true)
            }else{
                args.putBoolean(ExerciseResultFragment.leveledUpKey, false)
                args.putString(ExerciseResultFragment.titleKey, String.format("Incorrect! Accuracy: %.1f",accuracy))
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

    fun refresh(){
        val root = view
        root?.apply {
            //Log.d("XXX ExercisesFragment: ", "refreshing, level $level")
            //initProgressBar(this)
             initExerciseSet()
             initChildButtons(this)
             BindMainColor(this)
            SetTitle(this)

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
        setDebugMode()
        initLevelsMap()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProgressBar(view)
        initExerciseSet()

        initLevelsSpinner(view)
        initModesSpinner(view)
        initChildButtons(view)
        BindMainColor(view)
        SetTitle(view)
    }
}