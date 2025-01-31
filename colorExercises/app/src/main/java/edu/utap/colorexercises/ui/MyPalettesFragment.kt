package edu.utap.colorexercises.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import edu.utap.colorexercises.EditPaletteActivity
import edu.utap.colorexercises.R
import edu.utap.colorexercises.model.MainViewModel
import edu.utap.colorexercises.model.Palette
import kotlinx.android.synthetic.main.fragment_mypalettes.*


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MyPalettesFragment : Fragment(R.layout.fragment_mypalettes) {
    private val viewModel: MainViewModel by viewModels()

    companion object {
        fun newInstance(): MyPalettesFragment {
            return MyPalettesFragment()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //hex color passed from ExerciseResultFragment (String of hex color in form of "#000000"
        val bundle = arguments
        var hexColor : String? = null
        if (bundle!=null){
            hexColor = bundle.getString(ExerciseResultFragment.sentColorKey)
        }
        viewModel.observePalettes().observe(viewLifecycleOwner, Observer {
            initAdapter(view, it, hexColor)
        })

        viewModel.getUserPalettes(FirebaseAuth.getInstance().currentUser?.uid, {})

        initAddPaletteTrigger()

        initLayout(hexColor)
    }

    override fun onStart() {
        super.onStart()

    }

    private fun initLayout(hexColor: String?) {
        addColorInstructions.visibility = if (hexColor == null) View.GONE else View.VISIBLE
    }

    private fun initAddPaletteTrigger() {
        addPaletteTrigger.setOnClickListener {
            val intent = Intent(context, EditPaletteActivity::class.java)
            this.startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data?.extras?.apply{

        }
    }

    private fun initAdapter(view: View, palettes: List<Palette>, hexColor: String?) {
        var palettesView = view.findViewById<RecyclerView>(R.id.palette_list)

        palettesView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = UserPalettesAdapter(requireContext(), palettes, hexColor)

        palettesView.adapter = adapter
    }
}