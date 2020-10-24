package edu.utap.colorexercises.model

import android.util.Log
import edu.utap.colorexercises.service.ColorGenerator
import edu.utap.colorexercises.service.ColorJudge
import kotlin.random.Random

class ExerciseSet {
    private lateinit var mainColor: OneColor
    private lateinit var colorList: List<OneColor>
    private lateinit var accuracyList: List<Double>
    private val generator = ColorGenerator()
    private var level = 0
    private var mode = "MATCHVALUE"
    private var easyLevel = 5
    private var difficultLevel = 30

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


    fun NewLuminanceSet(setSize: Int, mainSaturation: Double, hue: Int, saturation: Double){
        //for grayscale: hue = 0, saturation = 0
        //for randomized hue or saturation: hue = -1 or saturation = -1
        //Log.d("XXX ExerciseSet: ", "NewLuminanceSet: setSize=${setSize}, saturation=${mainSaturation}, hue=$hue")
        //TODO: repeating a lot of codes from other newSet functions, think of how to abstract new Colorset idea
        val tolerance = 0.02
        val newColorListHSL = generator.ColorSetFromUniformLuminance(setSize,hue,saturation,tolerance)
        val mainColorMatchTo = Random.nextInt(0,setSize)
        this.colorList = newColorListHSL.map{OneColor(it)        }
        val targetLuminance = this.colorList[mainColorMatchTo].getLuminance()
        this.mainColor = OneColor(generator.ColorFromRandomHue(mainSaturation,targetLuminance,tolerance))
        this.accuracyList = makeAccuracyList(ColorJudge::LuminanceDifference)
        //Log.d("XXX ExerciseSet: ", "main color saturation: ${mainColor.hsl[1]}")
    }

    fun NewSet() = when(mode){
        //TODO: Ugly hard code for now, think about better ways to represent it, and might be able to import from cloud for extended functionality
        "MATCHVALUE" -> NewLuminanceSet(level%7+6,generator.SaturationForLevel(10,level/7),generator.HueForLevel(level,difficultLevel),generator.SaturationForLevel(10,level/7))
        else -> NewLuminanceSet(6,0.0,0,0.0)

    }

    fun getMainColor() = this.mainColor
    fun getColorList() = this.colorList
    fun getAccuracyList() = this.accuracyList
    fun setLevel(level: Int){this.level = level}
    fun setMode(mode: String){this.mode = mode}
    fun getSize() = this.colorList.size
    fun setDifficultLevel(difficultLevel: Int) {this.difficultLevel=difficultLevel}

}