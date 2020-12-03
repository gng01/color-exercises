package edu.utap.colorexercises.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.OvershootInterpolator
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ogaclejapan.arclayout.ArcLayout
import edu.utap.colorexercises.R
import edu.utap.colorexercises.model.*
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random.Default.nextInt


/**
 * Exercise Fragment that handles view bindings
 * Not using separate adapter because it is not supported by ArcLayout
 */
class ExercisesFragment : Fragment(R.layout.fragment_exercises) {

    private val debug = true

    val modesList = ExerciseModesRepository().fetchModesList()
    val buttonsRepository = ButtonsRepository()
    //val modeMapName2ID = modesList.map{it.displayName to it.id}.toMap()//mapOf("Match Value" to "MATCHVALUE","Match Hue" to "MATCHHUE")
    private var displaynamesArray = modesList.map{it.displayName}//modeMap.keys.toMutableList()


    private lateinit var accuracyList: List<Double>
    private val exerciseSet = ExerciseSet()
    private var accuracyThreshold = 90
    private var level = 0
    private var mode = modesList[0]//"MATCHVALUE"
    private var description = "DummyTitle"
    private var difficultLevel = 30
    private var middleLevel = 15
    //private var progress = 0
    private lateinit var progressBar: ProgressBar
    private var roundsToLevelUp = 4
    private var levelsArray = mutableListOf<Int>(0)

    private var levelsAdapter: ArrayAdapter<Int>? =null
    private var modesAdapter: ArrayAdapter<String>? =null
    private lateinit var levelsSpinner: Spinner
    private lateinit var modesSpinner: Spinner
    private var levelsMap = mutableMapOf<ExerciseMode, MutableList<Int>>(mode to levelsArray)
    private val viewModel: MainViewModel by viewModels()


    private val TAG = "XXX ExercisesFragment"

    companion object {
        val exercisesFragmentKey = "ExercisesFragment"


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
        titleTV.text = description
    }

    private fun setHelp(root: View){
        val helpButton = root.findViewById<Button>(R.id.btn_exercise_help)
        helpButton.setOnClickListener {
            val urlIntent = Intent(Intent.ACTION_VIEW)
            urlIntent.data = Uri.parse(mode.help)
            this.requireActivity().startActivity(urlIntent)
        }
    }

    private fun setProgress(givenProgress: Int): Boolean {
        progressBar.progress = givenProgress
        //Log.d("XXX ExercisesFragment: ", "progress ${givenProgress}")
        if(givenProgress>=100){
            //Log.d("XXX ExercisesFragment: ", "incrementing level to ${level+1}")
            exerciseSet.setLevel(level + 1)
            updateLevels(level + 1)
            progress=0
            progressBar.progress = progress
            //trick to get around spinner.setSelection not working issue
            return true
        }
        // return: if leveled up, variable for showing levelUpAnimation
        return false
    }

    private fun updateLevels(level: Int){
        if(!levelsMap.containsKey(this.mode)){
            levelsMap[this.mode] = mutableListOf(0)
        }else {
            levelsMap[this.mode]!!.apply {
                if(!this.contains(level)) {
                    this.add(level)
                }
            }
        }
        viewModel.setMode(this.mode.id, level)
        levelsArray = levelsMap[this.mode]!!
        levelsAdapter!!.notifyDataSetChanged()
        //had to add the following line because notifyDataSetChanged() is not always working
        // this takes more resources but is only required under rare occasion, thus tolerable
        if (levelsSpinner.count!=levelsArray.size){
            updateLevelsSpinner()
        }
        postToLevelsSpinner()

        this.level = level

    }

    private fun postToLevelsSpinner(){
        levelsSpinner.post(Runnable {
            kotlin.run { levelsSpinner.setSelection(levelsArray.last()) }
        })
    }

    private fun updateLevelsSpinner(){
        levelsAdapter = ArrayAdapter(this.requireContext(), R.layout.spinner_row, levelsArray)
        levelsAdapter!!.notifyDataSetChanged()
        levelsSpinner.adapter = levelsAdapter
    }


    private fun initProgressBar(root: View){
        this.progressBar = root.findViewById(R.id.progressbar_exercises)
        this.setProgress(progress)
    }

    private fun initLevelsMap(){
        for (value in modesList){
            levelsMap[value] = viewModel.getModeLevels(value.id)
        }
        if (this.levelsMap[this.mode]!=null){
            this.levelsArray = this.levelsMap[this.mode]!!
        }
    }

    private fun initExerciseSet(){
        exerciseSet.setLevel(this.level)
        exerciseSet.setMode(this.mode.id)
        exerciseSet.setDifficultLevel(this.difficultLevel)
        exerciseSet.newSet()
        this.accuracyList = exerciseSet.getAccuracyList()
        this.description = mode.description

    }

    private fun initLevelsSpinner(root: View, userPaletteCount: Int){
        levelsSpinner = root.findViewById<Spinner>(R.id.spinner_levels)
        levelsAdapter = ArrayAdapter(this.requireContext(), R.layout.spinner_row, levelsArray)
        levelsAdapter!!.notifyDataSetChanged()
        levelsSpinner.adapter = levelsAdapter
        levelsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (levelsArray[p2]==level) return
                level = levelsArray[p2]
                refresh(userPaletteCount)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }


    private fun initModesSpinner(root: View, userPaletteCount: Int){
        modesSpinner = root.findViewById<Spinner>(R.id.spinner_modes)
        modesAdapter = ArrayAdapter(
            this.requireContext(),
            R.layout.spinner_modes_row,
            displaynamesArray
        )
        modesAdapter!!.notifyDataSetChanged()
        modesSpinner.adapter = modesAdapter
        modesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (modesList.find { it.displayName== displaynamesArray[p2]}?.id==mode.id) return
                mode = modesList.find { it.displayName== displaynamesArray[p2]}?: error("mode doesn't exist")
                modesToLevels(mode)

                progress=0
                postToLevelsSpinner()
                refresh(userPaletteCount)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    private fun modesToLevels(mode: ExerciseMode){
        if (levelsMap[mode]==null){
            updateLevels(0)
        }else {
            levelsArray = levelsMap[mode]!!
            levelsAdapter?.notifyDataSetChanged()
            updateLevels(levelsArray.last())
        }
    }



    private fun bindMainColor(root: View): Button? {
        val mainColorButton = root.findViewById<Button>(R.id.btn_exercise_main_color)
        mainColorButton.background.setTint(exerciseSet.getMainColor().getInt())
        return mainColorButton
    }

    private fun bindButton(view: View, position: Int, paletteCount: Int){
        if(position>=exerciseSet.getSize()){
            view.visibility = View.GONE
            return
        }
        view.visibility = View.VISIBLE
        if(level>=difficultLevel){
            view.setBackgroundResource(buttonsRepository.randomButton())}

        view.background.setTint(exerciseSet.getColorList()[position].getInt())
        view.setOnClickListener {
            val resultFragment = ExerciseResultFragment()
            val args = Bundle()
            val mainColor = exerciseSet.getMainColor()
            val selectedColor = exerciseSet.getColorList()[position]
            val accuracy = accuracyList[position]
            args.putFloatArray(ExerciseResultFragment.mainColorKey, mainColor.hsl)
            args.putFloatArray(ExerciseResultFragment.selectedColorKey, selectedColor.hsl)
            args.putInt(ExerciseResultFragment.userPaletteCountKey, paletteCount)
            if (accuracy>=this.accuracyThreshold) {
                progress+=accuracy.toInt()/this.roundsToLevelUp.toInt()
                var leveledUp = setProgress(progress)
                args.putBoolean(ExerciseResultFragment.leveledUpKey, leveledUp)
                args.putString(
                    ExerciseResultFragment.titleKey, String.format(
                        "Match! Accuracy: %.1f",
                        accuracy
                    )
                )
                args.putBoolean(ExerciseResultFragment.resultStateKey, true)
            }else{
                args.putBoolean(ExerciseResultFragment.leveledUpKey, false)
                args.putString(
                    ExerciseResultFragment.titleKey, String.format(
                        "Incorrect! Accuracy: %.1f",
                        accuracy
                    )
                )
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

    fun refresh(userPaletteCount: Int){
        val root = view
        root?.apply {
            initProgressBar(this)
             initExerciseSet()
             initArcLayout(this, userPaletteCount)
            SetTitle(this)
            setHelp(this)


        }

    }

    private fun initChildButtons(root: View, userPaletteCount: Int): ArcLayout? {
        val arcLayout = root.findViewById<ArcLayout>(R.id.arc_layout)
        val buttonId = buttonsRepository.randomButton()
        for (i in 0 until arcLayout.childCount) {
            if (level>middleLevel){
                arcLayout.getChildAt(i).setBackgroundResource(buttonId)
            }else{
                arcLayout.getChildAt(i).setBackgroundResource(R.drawable.ic_exercise_button_round_50)
            }
            bindButton(arcLayout.getChildAt(i), i, userPaletteCount)

        }
        return arcLayout
    }



    // animation codes adapted from Arclayout demo codes.
    private fun showColors(arcLayout: ArcLayout, mainColorButton: Button) {
        arcLayout.setVisibility(View.VISIBLE)
        val animSet = AnimatorSet()
        val animList: MutableList<Animator> = ArrayList()
        var i = 0
        val len: Int = arcLayout.getChildCount()
        while (i < len) {
            createShowItemAnimator(arcLayout.getChildAt(i), mainColorButton)?.let { animList.add(it) }
            i++
        }

        val randomAngle = ThreadLocalRandom.current().nextInt(0, 180).toFloat()
        val wholeAnimation = ObjectAnimator.ofPropertyValuesHolder(arcLayout, AnimatorUtils.rotation(0f, randomAngle))
        animList.add(wholeAnimation)

        animSet.duration = 400
        animSet.interpolator = OvershootInterpolator()
        animSet.playTogether(animList)
        animSet.start()
    }

    private fun createShowItemAnimator(item: View, mainColorButton: Button): Animator? {
        val dx: Float = mainColorButton.x - item.x
        val dy: Float = mainColorButton.y - item.y
        item.scaleX = 0.5f
        item.scaleY = 0.5f
        item.rotation = 0f
        item.translationX = dx
        item.translationY = dy
        return ObjectAnimator.ofPropertyValuesHolder(
            item,
            AnimatorUtils.rotation(0f, ThreadLocalRandom.current().nextInt(0, 360).toFloat()),
            AnimatorUtils.translationX(dx, 0f),
            AnimatorUtils.translationY(dy, 0f),
            AnimatorUtils.scaleX(1f),
            AnimatorUtils.scaleY(1f)

        )
    }

    private fun initArcLayout(root: View, userPaletteCount: Int){
        val arcLayout = initChildButtons(root, userPaletteCount)
        val mainColorButton = bindMainColor(root)
        if (mainColorButton != null) {
            if (arcLayout != null) {
                root.viewTreeObserver.addOnGlobalLayoutListener(
                    object : OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            // Layout has happened here.
                            showColors(arcLayout, mainColorButton)
                            // Don't forget to remove your listener when you are done with it.
                            root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }
                    })
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getUser()
        setDebugMode()
        initLevelsMap()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeFirebaseAuthLiveData()

        val userId = viewModel.getUid()

        viewModel.getUserPalettes(userId, {
            initProgressBar(view)
            initExerciseSet()

            var userPaletteCount = -1
            if (userId != null)
                userPaletteCount = it?.count() ?: 0

            initLevelsSpinner(view, userPaletteCount)
            initModesSpinner(view, userPaletteCount)
            updateLevels(levelsArray[levelsArray.lastIndex])
            initArcLayout(view, userPaletteCount)
            SetTitle(view)
            setHelp(view)
        })
    }
}