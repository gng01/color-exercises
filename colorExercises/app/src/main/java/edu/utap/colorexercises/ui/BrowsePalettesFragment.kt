package edu.utap.colorexercises.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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
    private val TAG = "XXX BrowsePalettesFragment"
    companion object {
        fun newInstance(): BrowsePalettesFragment {
            return BrowsePalettesFragment()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllPalettes()

        viewModel.observeLivePalettes().observe(viewLifecycleOwner, Observer {
            initAdapter(view, it)
        })


        initSearch(view)
        initFavBtn(view)

        viewModel.isShowingFavorites = false
    }

    override fun onStart() {
        super.onStart()

        if (viewModel.isShowingFavorites)
            viewModel.filterList("favoritedUsersList")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data?.extras?.apply{

        }
    }

    private fun initSearch(view: View) {
        val searchEditText = view.findViewById<EditText>(R.id.ed_search_palette)
        val searchBtn = view.findViewById<Button>(R.id.btn_Search)
        searchBtn.setOnClickListener {
            val text = searchEditText.text.toString()
            if (text == null || text.isEmpty()) {
                viewModel.setSearchTerm("")

            }else{
                viewModel.setSearchTerm(text)
            }
            viewModel.filterList("keywords")
            (activity as MainActivity).hideKeyboard()
        }

        searchEditText.setText("")

    }

    private fun initAdapter(view: View, palettes: List<Palette>): SharedPalettesAdapter {
        var palettesView = view.findViewById<RecyclerView>(R.id.browse_palette_list)

        palettesView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = SharedPalettesAdapter(requireContext(), palettes)

        palettesView.adapter = adapter
        adapter.notifyDataSetChanged()
        return adapter
    }

    private fun initFavBtn(view: View){
        var favIcon = view.findViewById<ImageView>(R.id.btn_favorites)
        favIcon.setImageResource(R.drawable.ic_baseline_favorite_24)
        favIcon.setOnClickListener {
            if(viewModel.getUid()==null){
                Toast.makeText(context,"Please sign in", Toast.LENGTH_LONG).show()
            }else{
                viewModel.filterList("favoritedUsersList")
                viewModel.isShowingFavorites = true
                initBackBtn(favIcon)
            }
        }

    private fun initBackBtn(backIcon: ImageView){
        backIcon.setImageResource(R.drawable.ic_baseline_arrow_back_24)
        backIcon.setOnClickListener {
            viewModel.getAllPalettes()
            initFavBtn(backIcon)
        }
    }
}