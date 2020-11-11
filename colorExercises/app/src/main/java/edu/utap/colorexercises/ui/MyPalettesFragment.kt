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

        viewModel.observePalettes().observe(viewLifecycleOwner, Observer {
            initAdapter(view, it)
        })

        viewModel.getAllPalettes()

        initAddPaletteTrigger()
    }

    override fun onStart() {
        super.onStart()

    }

    private fun initAddPaletteTrigger() {
        addPaletteTrigger.setOnClickListener {
            val intent = Intent(context, EditPaletteActivity::class.java)
            val extras = Bundle()
            extras.putStringArray("palette", arrayOf("#000000", "#333333", "#999999", "#AAAAAA"))
            intent.putExtras(extras)
            val result = 1
            this.startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data?.extras?.apply{

        }
    }

    private fun initAdapter(view: View, palettes: List<Palette>) {
        var palettesView = view.findViewById<RecyclerView>(R.id.palette_list)

        palettesView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = PalettesAdapter(requireContext(), palettes)

        palettesView.adapter = adapter
    }
}