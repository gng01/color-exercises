package edu.utap.colorexercises

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.FragmentTransaction
import edu.utap.colorexercises.ui.HomeFragment

class MainActivity : AppCompatActivity() {

    private lateinit var homeFragment: HomeFragment

    private fun initHomeFragment() {
        supportFragmentManager
            .beginTransaction()
            // No back stack for home
            .add(R.id.main_frame, homeFragment,"homeFragment")
            .addToBackStack(null)
            // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

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
}