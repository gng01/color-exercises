package edu.utap.colorexercises.model

import edu.utap.colorexercises.service.ColorGenerator
import edu.utap.colorexercises.service.ColorJudge
import kotlin.random.Random

class ExerciseSet {
    private lateinit var mainColor: OneColor
    private lateinit var colorList: List<OneColor>
    private lateinit var accuracyList: List<Double>
    private val generator = ColorGenerator()

    //TODO: Add Title/ explanation to exercise Set
    fun AddColorList(centerColor: OneColor, colorList: List<OneColor>){
        this.mainColor = centerColor
        this.colorList = colorList
    }

    private fun makeAccuracyList(criteria: (OneColor, OneColor)-> Double): List<Double> {
        val correctList = mutableListOf<Double>()
        this.colorList.forEachIndexed { i, color ->
            correctList.add(i,ColorJudge.accuracy(this.mainColor,color,criteria))
        }
        //Log.d("XXX ExerciseSet: ", "Correct positions: ${correctList.toList()}")
        return correctList.toList()
    }


    fun NewColorToGraySet(setSize: Int, mainSaturation: Double){
        //TODO: repeating a lot of codes from other newSet functions, think of how to abstract new Colorset idea
        val tolerance = 0.02
        val hue = 0
        val saturation = 0.0
        val newColorList = mutableListOf<OneColor>()
        val mainColorMatchTo = Random.nextInt(0,setSize)
        val addedColors = mutableListOf<FloatArray>()
        for (i in 0 until setSize){
            var color = generator.ColorFromRandomLuminance(hue, saturation,tolerance)
            while (addedColors.contains(color)) {
                color = generator.ColorFromRandomLuminance(hue, saturation,tolerance)
            }
            newColorList.add(i, OneColor(color))
            addedColors.add(color)
            //Log.d("XXX ExerciseSet: ", "color: ${color.toList()}")
        }
        this.colorList = newColorList
        val targetLuminance = newColorList[mainColorMatchTo].getLuminance()
        this.mainColor = OneColor(generator.ColorFromRandomHue(mainSaturation,targetLuminance,tolerance))
        this.accuracyList = makeAccuracyList(ColorJudge::LuminanceDifference)
    }

    fun getMainColor() = this.mainColor
    fun getColorList() = this.colorList
    fun getAccuracyList() = this.accuracyList

}