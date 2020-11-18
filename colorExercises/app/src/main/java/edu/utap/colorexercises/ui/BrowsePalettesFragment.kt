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
import edu.utap.colorexercises.R
import edu.utap.colorexercises.model.MainViewModel
import edu.utap.colorexercises.model.Palette
import kotlinx.android.synthetic.main.fragment_browse_palettes.*
import kotlinx.android.synthetic.main.fragment_mypalettes.palettesHeading
import java.util.concurrent.ThreadLocalRandom

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class BrowsePalettesFragment : Fragment(R.layout.fragment_browse_palettes) {
    private val viewModel: MainViewModel by viewModels()

    companion object {
        fun newInstance(): BrowsePalettesFragment {
            return BrowsePalettesFragment()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observePalettes().observe(viewLifecycleOwner, Observer {
            initAdapter(view, it)
        })

        initLayout()

        viewModel.getAllPalettes(null)

        initAddPaletteTrigger()
    }

    private fun initLayout() {
        palettesHeading.text = "Browse Palettes"
    }

    private fun generateRandomPalette(){
        //Function for adding and saving many palettes for testing
        fun randomKeywords(numKeywords: Int): MutableList<String> {
            val keywordsBase = listOf<String>(
                "autumn",
                "winter",
                "summer",
                "spring",
                "beautiful",
                "dark",
                "bright",
                "light"
            )
            val randomKeywords = mutableListOf<String>()
            for (i in 0..numKeywords){
                randomKeywords.add(
                    keywordsBase[ThreadLocalRandom.current()
                        .nextInt(keywordsBase.size)]
                )
            }
            return randomKeywords
        }
        fun randomColors(numColors: Int): MutableList<String> {
            val randomColors = mutableListOf<String>()
            for (i in 0..numColors){
                val color = (Math.random() * 16777215).toInt() or (0xFF shl 24)
                val hexColor = java.lang.String.format("#%06X", 0xFFFFFF and color)
                randomColors.add(hexColor)
            }
            return randomColors
        }

        val numKeywords = 4
        val numColors = 6
        val palette = Palette()
        palette.ownerUserID = FirebaseAuth.getInstance().currentUser?.uid
        palette.ownerUserName = FirebaseAuth.getInstance().currentUser?.displayName
        palette.colors = randomColors(numColors)
        palette.keywords = randomKeywords(numKeywords)
        palette.name = palette.keywords.joinToString(" ")
        viewModel.savePalette(palette, { callBack() })

    }
    private fun initAddPaletteTrigger() {
        randomPalette.setOnClickListener {
            // copied from MyPalettes. Should do something else here
            generateRandomPalette()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data?.extras?.apply{

        }
    }

    private fun callBack() {
    }

    private fun initAdapter(view: View, palettes: List<Palette>) {
        var palettesView = view.findViewById<RecyclerView>(R.id.palette_list)

        palettesView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = SharedPalettesAdapter(requireContext(), palettes)

        palettesView.adapter = adapter
    }
}