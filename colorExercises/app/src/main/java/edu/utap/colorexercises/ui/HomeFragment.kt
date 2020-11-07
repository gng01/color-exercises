package edu.utap.colorexercises.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseUser
import edu.utap.colorexercises.R
import edu.utap.colorexercises.model.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class HomeFragment :
    Fragment(R.layout.fragment_home) {
    private var currentUser: FirebaseUser? = null
    private val viewModel: MainViewModel by activityViewModels()
    companion object {
        const val idKey = "idKey"
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    private fun initAuth() {
        viewModel.observeFirebaseAuthLiveData().observe(viewLifecycleOwner, Observer {
            currentUser = it
            val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
            toolbar.title = currentUser?.displayName
        })
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
        initAuth()
        initButtons(view)
    }

}