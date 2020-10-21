package edu.utap.colorexercises.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import edu.utap.colorexercises.R

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class ExercisesFragment : Fragment(R.layout.fragment_exercises) {

    companion object {
        fun newInstance(): ExercisesFragment {
            return ExercisesFragment()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_second).setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }
}