package edu.utap.colorexercises.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import edu.utap.colorexercises.R

class HomeFragment :
    Fragment(R.layout.fragment_home) {

    companion object {
        const val idKey = "idKey"
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    private fun initButtons(root: View) {
        //Log.d("XXX HomeFragment", "Recycler view inited")
        val btnExercises = root.findViewById<Button>(R.id.btn_exercises)
        val btnMyPalettes = root.findViewById<Button>(R.id.btn_myPalettes)
        val btnBrowsePalettes = root.findViewById<Button>(R.id.btn_browsePalettes)
        btnExercises.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.main_frame, ExercisesFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
        btnMyPalettes.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.main_frame, MyPalettesFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
        btnBrowsePalettes.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.main_frame, BrowsePalettesFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initButtons(view)
    }

}