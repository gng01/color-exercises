package edu.utap.colorexercises.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.utap.colorexercises.EditPaletteActivity
import edu.utap.colorexercises.MainActivity
import edu.utap.colorexercises.R
import edu.utap.colorexercises.model.MainViewModel
import edu.utap.colorexercises.model.Palette
import kotlinx.android.synthetic.main.fragment_mypalettes.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class BrowsePalettesFragment : Fragment(R.layout.fragment_browsepalettes) {
    private val viewModel: MainViewModel by viewModels()

    companion object {
        fun newInstance(): BrowsePalettesFragment {
            return BrowsePalettesFragment()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeLivePalettes().observe(viewLifecycleOwner, Observer {
            initAdapter(view, it)
        })

        initSearch(view)


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data?.extras?.apply{

        }
    }

    private fun initSearch(view: View){
        val searchEditText = view.findViewById<EditText>(R.id.ed_search_palette)
        searchEditText.addTextChangedListener (object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s==null || s.isEmpty()) (activity as MainActivity).hideKeyboard()
                viewModel.setSearchTerm(s.toString())}
        })
    }

    private fun initAdapter(view: View, palettes: List<Palette>) {
        var palettesView = view.findViewById<RecyclerView>(R.id.browse_palette_list)

        palettesView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = SharedPalettesAdapter(requireContext(), palettes)

        palettesView.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}