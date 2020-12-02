package edu.utap.colorexercises.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.doOnEnd
import com.ogaclejapan.arclayout.ArcLayout
import edu.utap.colorexercises.EditPaletteActivity
import edu.utap.colorexercises.R
import edu.utap.colorexercises.model.OneColor
import pl.droidsonroids.gif.GifImageView
import java.util.ArrayList
import java.util.concurrent.ThreadLocalRandom

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ExerciseResultFragment : Fragment(R.layout.fragment_exercise_result) {

    companion object {
        val titleKey = "TitleKey"
        val selectedColorKey = "SelectedColorKey"
        val mainColorKey = "MainColorKey"
        val resultStateKey = "ResultStateKey"
        val leveledUpKey = "LeveledUpKey"
        val sentColorKey = "SentColorKey"
        val userPaletteCountKey = "UserPaletteCountKey"
        fun newInstance(): ExerciseResultFragment {
            return ExerciseResultFragment()
        }
    }

    private var userPaletteCount : Int = 0

    private fun initTitle(root: View, title: String){
        val titleTV = root.findViewById<TextView>(R.id.txt_exercise_result)
        titleTV.text = title
    }

    private fun initResultPair(root: View, mainColor: FloatArray, selectedColor: FloatArray){
        val selectedColorButton = root.findViewById<ImageView>(R.id.btn_result_selection)
        val mainColorButton = root.findViewById<ImageView>(R.id.btn_result_main)
        val selectedColorObject = OneColor(selectedColor)
        val mainColorObject = OneColor(mainColor)
        selectedColorButton.background.setTint(selectedColorObject.getInt())
        mainColorButton.background.setTint(mainColorObject.getInt())

        selectedColorButton.setOnLongClickListener{
            sendColorToPalettes(selectedColorObject)
            return@setOnLongClickListener false
        }

        mainColorButton.setOnLongClickListener{
            sendColorToPalettes(mainColorObject)
            return@setOnLongClickListener false
        }
    }

    private fun sendColorToPalettes(colorObject: OneColor){
        if (userPaletteCount > 0) {
            val palettesFragment = MyPalettesFragment()
            var args = Bundle()
            args.putString(sentColorKey, colorObject.getHex())
            palettesFragment.arguments = args
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.main_frame, palettesFragment)
                .addToBackStack(null)
                .commit()
        } else {
            val intent = Intent(context, EditPaletteActivity::class.java)
            val extras = Bundle()
            extras.putString("addedColor", colorObject.getHex())
            intent.putExtras(extras)

            this.startActivity(intent)
        }
    }

    private fun initNextButton(root: View){
        val nextButton = root.findViewById<Button>(R.id.btn_result_next)
        nextButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

    }

    private fun levelUpAnimation(){
        val levelUpText = view?.findViewById<TextView>(R.id.level_up_view)
        val levelUpGIF = view?.findViewById<GifImageView>(R.id.level_up_gif)

        val animSet = AnimatorSet()
        val animList: MutableList<Animator> = ArrayList()

        textAnimator(levelUpText!!)?.let{animList.add(it)}
        gifAnimator(levelUpGIF!!)?.let{animList.add(it)}

        animSet.duration = 5000
        animSet.interpolator = OvershootInterpolator()
        animSet.playTogether(animList)
        animSet.start()
        animSet.doOnEnd {
            disappearAnimation(levelUpText)
            disappearAnimation(levelUpGIF)
        }


    }




    private fun disappearAnimation(item: View) {
        item.animate()
            .alpha(0f)
            .setDuration(1000)
            .withEndAction {
                item.visibility = View.GONE
            }
        return
    }

    private fun textAnimator(item: TextView): Animator? {
        item.visibility = View.VISIBLE
        return ObjectAnimator.ofPropertyValuesHolder(
            item,
            AnimatorUtils.translationX(-100f, 0f),
            AnimatorUtils.translationY(-100f, 600f),
            AnimatorUtils.scaleX(1.2f),
            AnimatorUtils.scaleY(1.2f)

        )
    }

    private fun gifAnimator(item: GifImageView): Animator? {
        item.visibility = View.VISIBLE
        return ObjectAnimator.ofPropertyValuesHolder(
            item,
            AnimatorUtils.translationY(0f, -100f),
            AnimatorUtils.rotation(360f),
            AnimatorUtils.scaleX(1f),
            AnimatorUtils.scaleY(1f)

        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments ?: return
        val title = bundle.getString(titleKey)
        val mainColor = bundle.getFloatArray(mainColorKey)
        val selectedColor = bundle.getFloatArray(selectedColorKey)
        userPaletteCount = bundle.getInt(userPaletteCountKey)
        val leveledUp = bundle.getBoolean(leveledUpKey)

        if (title!=null) {initTitle(view, title)}
        if (mainColor!=null && selectedColor!=null) {initResultPair(view,mainColor,selectedColor)}
        initNextButton(view)
        if (leveledUp) levelUpAnimation()

    }
}