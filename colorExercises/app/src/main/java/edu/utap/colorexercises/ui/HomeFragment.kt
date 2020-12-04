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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import edu.utap.colorexercises.AuthInitActivity
import edu.utap.colorexercises.MainActivity
import edu.utap.colorexercises.R
import edu.utap.colorexercises.model.MainViewModel
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment :
    Fragment(R.layout.fragment_home) {

    private var currentUser: FirebaseUser? = null
    private val viewModel: MainViewModel by activityViewModels()
    private val TAG = "HomeFragment"
    companion object {
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

    private fun initAccessUI() {
        viewModel.observeFirebaseAuthLiveData().observe(requireActivity(), Observer {
            if( it == null ) {
                btn_myPalettes?.visibility = View.GONE
            } else {
                btn_myPalettes?.visibility = View.VISIBLE
            }
        })
    }

    private fun initAuth() {
        viewModel.observeFirebaseAuthLiveData().observe(viewLifecycleOwner, Observer {
            currentUser = it
            viewModel.setLocalUserID(it?.uid)
            val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
            viewModel.getUser()
            toolbar.title = currentUser?.displayName
        })

    }

    private fun initObserveSignIn() {
        viewModel.observeFirebaseAuthLiveData().observe(viewLifecycleOwner, Observer {
            viewModel.initUserPalettes(it?.uid)
        })
    }

    private fun initButtons(root: View) {
        //Log.d("XXX HomeFragment", "Recycler view inited")
        val btnExercises = root.findViewById<Button>(R.id.btn_exercises)
        val btnMyPalettes = root.findViewById<Button>(R.id.btn_myPalettes)
        val btnBrowsePalettes = root.findViewById<Button>(R.id.btn_browsePalettes)
        val btnSignIn = root.findViewById<Button>(R.id.btn_signIn)
        val btnSettings = root.findViewById<Button>(R.id.btn_settings)
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
        btnSettings.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.main_frame, SettingsFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
        Log.d(TAG,"Init buttons: current user = ${currentUser}")
        if (currentUser!=null){
            btnSignIn.text = "Sign Out"
            initAuth()
            view?.findViewById<Button>(R.id.btn_settings)?.visibility=View.VISIBLE
            initSignOut(btnSignIn)
        }else{
            initSignIn(btnSignIn)
        }
    }

    private fun initSignIn(btnSignIn: Button){
        btnSignIn.setOnClickListener {
            btnSignIn.text = "Sign Out"
            view?.findViewById<Button>(R.id.btn_settings)?.visibility=View.VISIBLE
            initUserUI()
            initAuth()
            initSignOut(btnSignIn)
            Log.d(TAG, view.toString())


        }
    }

    private fun initSignOut(btnSignIn: Button){
        btnSignIn.setOnClickListener {
            if (currentUser!=null){
                view?.findViewById<Button>(R.id.btn_settings)?.visibility=View.INVISIBLE
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
        initAccessUI()
        initObserveSignIn()
    }

}