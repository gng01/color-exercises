package edu.utap.colorexercises.ui

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.utap.colorexercises.EditColorActivity
import edu.utap.colorexercises.EditPaletteActivity
import edu.utap.colorexercises.R
import edu.utap.colorexercises.model.Palette
import kotlinx.android.synthetic.main.fragment_mypalettes.*


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MyPalettesFragment : Fragment(R.layout.fragment_mypalettes) {

    companion object {
        fun newInstance(): MyPalettesFragment {
            return MyPalettesFragment()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val palettes = initializePalettes()

        initAdapter(view, palettes)
    }

    override fun onStart() {
        super.onStart()

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

    private fun initializePalettes() : List<Palette> {
        val p1 = Palette()
        p1.name = "my palette"
        p1.colors = mutableListOf<String>("#FF3333", "#555555", "#888888", "#AAAAAA")

        val p2 = Palette()
        p2.name = "grayscale"
        p2.colors = mutableListOf<String>("#333333", "#555555", "#888888", "#AAAAAA")

        val p3 = Palette()
        p3.name = "dark vibes"
        p3.colors = mutableListOf<String>("#333333", "#555555", "#888888", "#AAAAAA")

        val p4 = Palette()
        p4.name = "p4"
        p4.colors = mutableListOf<String>("#333333", "#555555", "#888888", "#AAAAAA")

        val p5 = Palette()
        p5.name = "p5"
        p5.colors = mutableListOf<String>("#333333", "#555555", "#888888", "#AAAAAA")

        val p6 = Palette()
        p6.name = "dark vibes ii"
        p6.colors = mutableListOf<String>("#333333", "#555555", "#888888", "#AAAAAA")

        val p7 = Palette()
        p7.name = "p7"
        p7.colors = mutableListOf<String>("#333333", "#555555", "#888888", "#AAAAAA")

        val p8 = Palette()
        p8.name = "p8"
        p8.colors = mutableListOf<String>("#333333", "#555555", "#888888", "#AAAAAA")

        return listOf<Palette>(p1, p2, p3, p4, p5, p6, p7, p8)
    }
}