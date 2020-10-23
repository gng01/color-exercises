package edu.utap.colorexercises.model

import android.util.Log
import kotlin.random.Random

class ExerciseSet {
    private lateinit var mainColor: OneColor
    private lateinit var colorList: List<OneColor>
    private lateinit var correctPositionList: List<Boolean>
    private val generator = ColorGenerator()

    //TODO: Add Title to exercise Set
    fun AddColorList(centerColor: OneColor, colorList: List<OneColor>){
        this.mainColor = centerColor
        this.colorList = colorList
    }

    fun CorrectPosition(criteria: String, threshold: Double): List<Boolean> {
        val correctList = mutableListOf<Boolean>()
        this.colorList.forEachIndexed { i, color ->
            correctList.add(i,OneColor.match(criteria,this.mainColor,color,threshold.toFloat()))
        }
        Log.d("XXX ExerciseSet: ", "Correct positions: ${correctList.toList()}")
        return correctList.toList()
    }

    fun NewAllGreyScaleSet(setSize: Int, threshold: Double){
        val tolerance = 0.1 //TODO: hardcoding tolerance for now, should be changed to shared value later
        val hue = 0
        val saturation = 0.0
        val newColorList = mutableListOf<OneColor>()
        val mainColorMatchTo = Random.nextInt(0,setSize)
        for (i in 0 until setSize){
            newColorList.add(i, OneColor(generator.ColorFromRandomLuminance(hue, saturation,tolerance)))
        }
        this.colorList = newColorList
        this.mainColor = newColorList[mainColorMatchTo]
        this.correctPositionList = CorrectPosition("MATCHVALUE",threshold)

    }


    fun getMainColor() = this.mainColor
    fun getColorList() = this.colorList
    fun getCorrectPosition() = this.correctPositionList

}