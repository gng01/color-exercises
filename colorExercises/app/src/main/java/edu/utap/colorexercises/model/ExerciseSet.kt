package edu.utap.colorexercises.model

import edu.utap.colorexercises.service.ColorGenerator
import edu.utap.colorexercises.service.ColorJudge
import kotlin.random.Random

class ExerciseSet {
    private lateinit var mainColor: OneColor
    private lateinit var colorList: List<OneColor>
    private lateinit var accuracyList: List<Double>
    private var level = 0
    private var mode = "MATCHVALUE"
    private var difficultLevel = 30
    private var tolerance = 0.02
    private var setSize = 6


    private fun makeAccuracyList(criteria: (OneColor, OneColor)-> Double): List<Double> {
        val correctList = mutableListOf<Double>()
        this.colorList.forEachIndexed { i, color ->
            correctList.add(i,ColorJudge.accuracy(this.mainColor,color,criteria))
        }
        return correctList.toList()
    }


    fun newLuminanceSet(hue: Int, saturation: Double){
        val newColorListHSL = ColorGenerator.colorSetFromUniformLuminance(setSize,hue,saturation,tolerance)
        val mainColorMatchTo = Random.nextInt(0,setSize)
        this.colorList = newColorListHSL.map{OneColor(it)        }
        val targetLuminance = this.colorList[mainColorMatchTo].getLuminance()
        this.mainColor = OneColor(ColorGenerator.colorFromRandomHue(saturation,targetLuminance,tolerance))
        this.accuracyList = makeAccuracyList(ColorJudge::luminanceDifference)
    }

    fun newHueSet(luminance: Double, saturation: Double){
        val newColorListHSL = ColorGenerator.colorSetFromRandomHue(setSize,saturation,luminance,tolerance)
        val mainColorMatchTo = Random.nextInt(0,setSize)
        this.colorList = newColorListHSL.map{OneColor(it)}
        val targetHue = this.colorList[mainColorMatchTo].getHSL()[0].toInt()
        this.mainColor = OneColor(ColorGenerator.colorFromRandomLuminance(targetHue,saturation,tolerance))
        this.accuracyList = makeAccuracyList(ColorJudge::hueDistance)
    }

    fun newComplementarySet(luminance: Double, saturation: Double){
        val newColorListHSL = ColorGenerator.colorSetFromRandomHue(setSize,saturation,luminance,tolerance)
        val mainColorMatchTo = Random.nextInt(0,setSize)
        this.colorList = newColorListHSL.map{OneColor(it)}
        val targetHue = this.colorList[mainColorMatchTo].getHSL()[0].toInt()
        this.mainColor = OneColor(ColorGenerator.complementaryColor(targetHue,saturation,tolerance))
        this.accuracyList = makeAccuracyList(ColorJudge::complementary)
    }

    fun newTriadSet(luminance: Double, saturation: Double){
        val newColorListHSL = ColorGenerator.colorSetFromRandomHue(setSize,saturation,luminance,tolerance)
        val mainColorMatchTo = Random.nextInt(0,setSize)
        this.colorList = newColorListHSL.map{OneColor(it)}
        val targetHue = this.colorList[mainColorMatchTo].getHSL()[0].toInt()
        this.mainColor = OneColor(ColorGenerator.triadColor(targetHue,saturation,tolerance))
        this.accuracyList = makeAccuracyList(ColorJudge::triad)
    }


    fun newSet() {
        setSetSize(level % 7 + 6)
        val incrementingSaturation = ColorGenerator.saturationForLevelLow2High(10, level / 7)
        val decrementingSaturation = ColorGenerator.saturationForLevelHigh2Low(10, level / 7, 0.1)
        val hue = ColorGenerator.hueForLevel(level, difficultLevel)
        val luminance = ColorGenerator.luminanceForLevel(level, difficultLevel)

        // This is great structure, IMO.  You can add new sets here without modifying existing code.
        // I know we discussed this early on, but I think it's great.
        // What you did here was follow the Open-Close principle:
        // open to extension--you can add more stuff;
        // closed to modification--you don't need to change anything when you add more sets.
        return when(mode){
            "MATCHVALUE" -> newLuminanceSet(hue,incrementingSaturation)
            "MATCHHUE" ->newHueSet(luminance,decrementingSaturation)
            "COMPLEMENTARY" -> newComplementarySet(luminance,decrementingSaturation)
            "TRIAD" -> newTriadSet(luminance,decrementingSaturation)
            else -> newLuminanceSet(0,0.0)
        }
    }

    fun setSetSize(setSize: Int){this.setSize = setSize}
    fun getMainColor() = this.mainColor
    fun getColorList() = this.colorList
    fun getAccuracyList() = this.accuracyList
    fun setLevel(level: Int){this.level = level}
    fun setMode(mode: String){this.mode = mode}
    fun getSize() = this.colorList.size
    fun setDifficultLevel(difficultLevel: Int) {this.difficultLevel=difficultLevel}

}