package edu.utap.colorexercises.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import edu.utap.colorexercises.EditPaletteActivity
import edu.utap.colorexercises.R
import edu.utap.colorexercises.model.OneColor

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ExerciseResultFragment : Fragment(R.layout.fragment_exercise_result) {

    private var userPaletteCount : Int = 0

    companion object {
        val titleKey = "TitleKey"
        val selectedColorKey = "SelectedColorKey"
        val mainColorKey = "MainColorKey"
        val resultStateKey = "ResultStateKey"
        val leveledUpKey = "LeveledUpKey"
        val sentColorKey = "SentColorKey"
        val userPaletteCountKey = "UserPaletteCountKey"
        fun newInstance(): ExerciseResultFragment {
            return ExerciseResultFragment()
        }
    }

    private fun initTitle(root: View, title: String){
        val titleTV = root.findViewById<TextView>(R.id.txt_exercise_result)
        titleTV.text = title
    }

    private fun initResultPair(root: View, mainColor: FloatArray, selectedColor: FloatArray){
        val selectedColorButton = root.findViewById<Button>(R.id.btn_result_selection)
        val mainColorButton = root.findViewById<Button>(R.id.btn_result_main)
        val selectedColorObject = OneColor(selectedColor)
        val mainColorObject = OneColor(mainColor)
        selectedColorButton.background.setTint(selectedColorObject.getInt())
        mainColorButton.background.setTint(mainColorObject.getInt())

        selectedColorButton.setOnLongClickListener{
            sendColorToPalettes(selectedColorObject)
            return@setOnLongClickListener false
        }

        mainColorButton.setOnLongClickListener{
            sendColorToPalettes(mainColorObject)
            return@setOnLongClickListener false
        }
    }

    private fun sendColorToPalettes(colorObject: OneColor){
        if (userPaletteCount > 0) {
            val palettesFragment = MyPalettesFragment()
            var args = Bundle()
            args.putString(sentColorKey, colorObject.getHex())
            palettesFragment.arguments = args
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.main_frame, palettesFragment)
                .addToBackStack(null)
                .commit()
            Toast.makeText(this.context, "Sent color to palettes", Toast.LENGTH_LONG)
                .show()
        } else {
            val intent = Intent(context, EditPaletteActivity::class.java)
            val extras = Bundle()
            extras.putString("addedColor", colorObject.getHex())
            intent.putExtras(extras)

            this.startActivity(intent)
        }
    }

    private fun initControls(root: View){
//        val backButton = root.findViewById<Button>(R.id.btn_result_back)
        val nextButton = root.findViewById<Button>(R.id.btn_result_next)

//        backButton.setOnClickListener {
//            parentFragmentManager.popBackStack()
//        }
        nextButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

    }

    private fun levelUpAnimation(){
        val levelUpView = view?.findViewById<Button>(R.id.level_up_view)
        //Log.d("XXX ExerciseResultFragment: ", "levelupview: ${levelUpView}")
        levelUpView?.apply {
            this.visibility = View.VISIBLE
            this.alpha=0.0f
            this.animate()
                .alpha(0.7f)
                .scaleXBy(2.0f)
                .scaleYBy(2.0f)
                .setListener(null)
                .setDuration(2000)
                .withEndAction {
                    this.animate()
                        .alpha(0.0f)
                        .setDuration(1000)
                        .withEndAction {
                            this.visibility = View.GONE
                        }
                }
                .start()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments ?: return
        val title = bundle.getString(titleKey)
        val mainColor = bundle.getFloatArray(mainColorKey)
        val selectedColor = bundle.getFloatArray(selectedColorKey)
        userPaletteCount = bundle.getInt(userPaletteCountKey)
        //added resultState in case we want to customize layout between correct and wrong state
        val resultState = bundle.getBoolean(resultStateKey)
        val leveledUp = bundle.getBoolean(leveledUpKey)

        if (title!=null) {initTitle(view, title)}
        if (mainColor!=null && selectedColor!=null) {initResultPair(view,mainColor,selectedColor)}
        initControls(view)
        if (leveledUp) levelUpAnimation()

    }
}