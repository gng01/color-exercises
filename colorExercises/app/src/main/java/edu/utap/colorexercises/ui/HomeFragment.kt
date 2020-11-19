package edu.utap.colorexercises.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseUser
import edu.utap.colorexercises.AuthInitActivity
import edu.utap.colorexercises.MainActivity
import edu.utap.colorexercises.R
import edu.utap.colorexercises.model.MainViewModel

class HomeFragment :
    Fragment(R.layout.fragment_home) {
    /*

    TODO [Added by Arky 1118]:
        1. Add user name change page
        2. Add save to my palettes option if i'm not the owner
        3. Create one more game mode
        4. Link colors in ExerciseFragment to Add to MyPalettesFragment
        5. Add ArcLayout Animation in ExerciseFragment

     */
    private var currentUser: FirebaseUser? = null
    private val viewModel: MainViewModel by activityViewModels()
    companion object {
        const val idKey = "idKey"
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    private fun initUserUI() {
        viewModel.observeFirebaseAuthLiveData().observe(requireActivity(), Observer {
            if( it == null ) {
                Log.d(MainActivity.TAG, "No one is signed in")
            } else {
                Log.d(MainActivity.TAG, "${it.displayName} ${it.email} ${it.uid} signed in")
            }
        })
        val authInitIntent = Intent(requireActivity(), AuthInitActivity::class.java)
        startActivity(authInitIntent)

    }

    private fun initAuth() {
        viewModel.observeFirebaseAuthLiveData().observe(viewLifecycleOwner, Observer {
            currentUser = it
            val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
            viewModel.getUser()
            toolbar.title = currentUser?.displayName

        })
    }

    private fun initButtons(root: View) {
        //Log.d("XXX HomeFragment", "Recycler view inited")
        val btnExercises = root.findViewById<Button>(R.id.btn_exercises)
        val btnMyPalettes = root.findViewById<Button>(R.id.btn_myPalettes)
        val btnBrowsePalettes = root.findViewById<Button>(R.id.btn_browsePalettes)
        val btnSignIn = root.findViewById<Button>(R.id.btn_signIn)
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
        if (currentUser!=null){
            btnSignIn.text = "Sign Out"
            initSignOut(btnSignIn)
        }else{
            initSignIn(btnSignIn)
        }
    }

    private fun initSignIn(btnSignIn: Button){
        btnSignIn.setOnClickListener {
            btnSignIn.text = "Sign Out"
            initUserUI()
            initAuth()
            initSignOut(btnSignIn)

        }
    }

    private fun initSignOut(btnSignIn: Button){
        btnSignIn.setOnClickListener {
            if (currentUser!=null){
                viewModel.signOut()
                btnSignIn.text = "Sign In"
                initSignIn(btnSignIn)
                val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
                toolbar.title = "Please Sign in"
            }
        }
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons(view)
    }

}