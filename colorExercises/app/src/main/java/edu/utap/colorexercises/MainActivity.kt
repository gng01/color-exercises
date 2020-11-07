package edu.utap.colorexercises

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import edu.utap.colorexercises.model.MainViewModel
import edu.utap.colorexercises.ui.HomeFragment

class MainActivity : AppCompatActivity() {
    private lateinit var homeFragment: HomeFragment
    companion object {
        const val TAG = "ColorExercises"
    }
    private val viewModel: MainViewModel by viewModels()

    private fun initUserUI() {
        viewModel.observeFirebaseAuthLiveData().observe(this, Observer {
            if( it == null ) {
                Log.d(TAG, "No one is signed in")
            } else {
                Log.d(TAG, "${it.displayName} ${it.email} ${it.uid} signed in")
            }
        })
    }

    private fun initHomeFragment() {
        supportFragmentManager
            .beginTransaction()
            // No back stack for home
            .add(R.id.main_frame, homeFragment,"homeFragment")
            .commitNow()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        initUserUI()
        val authInitIntent = Intent(this, AuthInitActivity::class.java)
        startActivity(authInitIntent)

        homeFragment = HomeFragment.newInstance()
        initHomeFragment()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(javaClass.simpleName, "onActivityResult")
    }
}