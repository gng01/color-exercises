package edu.utap.colorexercises.ui

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.utap.colorexercises.EditColorActivity
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

        initAdapter(view)

        viewModel.getAllPalettes()
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data?.extras?.apply{

        }
    }

    private fun initAdapter(view: View) {
        var palettesView = view.findViewById<RecyclerView>(R.id.palette_list)

        palettesView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.observePalettes().observe(viewLifecycleOwner, Observer {
            val adapter = PalettesAdapter(requireContext(), it)

            palettesView.adapter = adapter
        })
    }
}