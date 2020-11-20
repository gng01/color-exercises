package edu.utap.colorexercises.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import edu.utap.colorexercises.AuthInitActivity
import edu.utap.colorexercises.MainActivity
import edu.utap.colorexercises.R
import edu.utap.colorexercises.model.MainViewModel
import kotlinx.android.synthetic.main.fragment_home.*

class SettingsFragment :
    Fragment(R.layout.fragment_settings) {
    val TAG = "SettingsFragment"
    companion object {
        const val idKey = "idKey"
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

    private fun initButtons(root: View) {
        val cancelNameButton = root.findViewById<Button>(R.id.btn_cancelName)
        val submitNameButton = root.findViewById<Button>(R.id.btn_submitName)
        val enterNameET = root.findViewById<EditText>(R.id.ed_changeName)
        cancelNameButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        submitNameButton.setOnClickListener {
            val enteredName = enterNameET.text.toString()
            val firebaseAuth = FirebaseAuth.getInstance();
            val user = firebaseAuth.currentUser
            if (enteredName.isNotEmpty()) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(enteredName).build();
                user?.updateProfile(profileUpdates)?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(TAG, FirebaseAuth.getInstance().getCurrentUser()?.getDisplayName());
                    }

                }
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons(view)
    }

}