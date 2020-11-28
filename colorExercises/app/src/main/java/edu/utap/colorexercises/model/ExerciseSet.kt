package edu.utap.colorexercises.model

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
        val newColorListHSL = generator.colorSetFromUniformLuminance(setSize,hue,saturation,tolerance)
        val mainColorMatchTo = Random.nextInt(0,setSize)
        this.colorList = newColorListHSL.map{OneColor(it)        }
        val targetLuminance = this.colorList[mainColorMatchTo].getLuminance()
        this.mainColor = OneColor(generator.colorFromRandomHue(saturation,targetLuminance,tolerance))
        this.accuracyList = makeAccuracyList(ColorJudge::LuminanceDifference)
    }

    fun newHueSet(luminance: Double, saturation: Double){
        val newColorListHSL = generator.colorSetFromRandomHue(setSize,saturation,luminance,tolerance)
        val mainColorMatchTo = Random.nextInt(0,setSize)
        this.colorList = newColorListHSL.map{OneColor(it)}
        val targetHue = this.colorList[mainColorMatchTo].getHSL()[0].toInt()
        this.mainColor = OneColor(generator.colorFromRandomLuminance(targetHue,saturation,tolerance))
        this.accuracyList = makeAccuracyList(ColorJudge::HueDistance)
    }

    fun newComplementarySet(luminance: Double, saturation: Double){
        val newColorListHSL = generator.colorSetFromRandomHue(setSize,saturation,luminance,tolerance)
        val mainColorMatchTo = Random.nextInt(0,setSize)
        this.colorList = newColorListHSL.map{OneColor(it)}
        val targetHue = this.colorList[mainColorMatchTo].getHSL()[0].toInt()
        this.mainColor = OneColor(generator.complementaryColor(targetHue,saturation,tolerance))
        this.accuracyList = makeAccuracyList(ColorJudge::Complementary)
    }

    fun newTriadSet(luminance: Double, saturation: Double){
        val newColorListHSL = generator.colorSetFromRandomHue(setSize,saturation,luminance,tolerance)
        val mainColorMatchTo = Random.nextInt(0,setSize)
        this.colorList = newColorListHSL.map{OneColor(it)}
        val targetHue = this.colorList[mainColorMatchTo].getHSL()[0].toInt()
        this.mainColor = OneColor(generator.triadColor(targetHue,saturation,tolerance))
        this.accuracyList = makeAccuracyList(ColorJudge::Triad)
    }


    fun newSet() {
        setSetSize(level % 7 + 6)
        val incrementingSaturation = generator.saturationForLevelLow2High(10, level / 7)
        val decrementingSaturation = generator.saturationForLevelHigh2Low(10, level / 7, 0.1)
        val hueGenerator = generator.hueForLevel(level, difficultLevel)
        val luminanceGenerator = generator.luminanceForLevel(level, difficultLevel)

        return when(mode){
            "MATCHVALUE" -> newLuminanceSet(hueGenerator,incrementingSaturation)
            "MATCHHUE" ->newHueSet(luminanceGenerator,decrementingSaturation)
            "COMPLEMENTARY" -> newComplementarySet(luminanceGenerator,decrementingSaturation)
            "TRIAD" -> newTriadSet(luminanceGenerator,decrementingSaturation)
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