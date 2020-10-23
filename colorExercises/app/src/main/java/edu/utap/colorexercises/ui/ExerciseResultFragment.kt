package edu.utap.colorexercises.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import edu.utap.colorexercises.R
import edu.utap.colorexercises.model.OneColor

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ExerciseResultFragment : Fragment(R.layout.fragment_exercise_result) {

    companion object {
        val titleKey = "TitleKey"
        val selectedColorKey = "SelectedColorKey"
        val mainColorKey = "MainColorKey"
        val resultStateKey = "ResultStateKey"
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

        //TODO: leave these button clickable, in case we want to access color info/ add to pallet here
        selectedColorButton.setOnClickListener {
            Toast.makeText(this.context, "Selected color added to palette", Toast.LENGTH_LONG)
                .show()
        }

        selectedColorButton.setOnLongClickListener{
            Toast.makeText(this.context, "Info about selected color", Toast.LENGTH_LONG)
                .show()
            return@setOnLongClickListener false
        }

        mainColorButton.setOnClickListener {
            Toast.makeText(this.context, "Main color added to palette", Toast.LENGTH_LONG)
                .show()
        }

        mainColorButton.setOnLongClickListener{
            Toast.makeText(this.context, "Info about main color", Toast.LENGTH_LONG)
                .show()
            return@setOnLongClickListener false
        }
    }

    private fun initControls(root: View){
        val backButton = root.findViewById<Button>(R.id.btn_result_back)
        val nextButton = root.findViewById<Button>(R.id.btn_result_next)

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments ?: return
        val title = bundle.getString(titleKey)
        val mainColor = bundle.getFloatArray(mainColorKey)
        val selectedColor = bundle.getFloatArray(selectedColorKey)
        //added resultState in case we want to customize layout between correct and wrong state
        val resultState = bundle.getBoolean(resultStateKey)

        if (title!=null) {initTitle(view, title)}
        if (mainColor!=null && selectedColor!=null) {initResultPair(view,mainColor,selectedColor)}
        initControls(view)

    }
}