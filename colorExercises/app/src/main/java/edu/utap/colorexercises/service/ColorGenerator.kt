package edu.utap.colorexercises.service

import android.annotation.SuppressLint
import androidx.core.graphics.ColorUtils
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.abs

class ColorGenerator {
    private val invalidColorHexCode = "#808080"

    val hueRange  = listOf<Int>(0,360)
    val saturationRange = listOf<Double>(0.0,1.0)
    val luminanceRange = listOf<Double>(0.1,0.9)


    // A better idea for generate color with given luminance than analytic calculation: binary search in l space of hsl:
    // perceived luminance and lightness given in hsl space are NOT THE SAME!
    // tolerance: absolute difference between the two luminance value
    @SuppressLint("Range")
    fun findHSLWithLuminance(hue: Int, saturation: Double, luminance: Double, tolerance: Double): FloatArray {
        var lightness = 0.5
        var hsl = FloatArray(3)
        var curLuminance = -1.0
        var lowerBound = this.luminanceRange[0]
        var upperBound = this.luminanceRange[1]
        while (abs(luminance-curLuminance)>tolerance){
            hsl[0] = hue.toFloat()
            hsl[1] = saturation.toFloat()
            hsl[2] = lightness.toFloat()
            val color = ColorUtils.HSLToColor(hsl)
            curLuminance = ColorUtils.calculateLuminance(color)
            if (curLuminance>luminance){
                upperBound = lightness
            }else{
                lowerBound = lightness
            }

            val randomCuttingPosition = ThreadLocalRandom.current().nextInt(1, 5)
            lightness = lowerBound + (upperBound-lowerBound)/ randomCuttingPosition
        }
        return hsl
    }

    fun colorFromRandomHue(saturation: Double, luminance: Double, tolerance: Double): FloatArray{
        val randomHue = ThreadLocalRandom.current().nextInt(this.hueRange[0], this.hueRange[1])
        return findHSLWithLuminance(randomHue, saturation, luminance, tolerance)
    }
    fun colorFromRandomSaturation(hue: Int, luminance: Double, tolerance: Double): FloatArray{
        val randomSaturation =
            ThreadLocalRandom.current().nextDouble(this.saturationRange[0], this.saturationRange[1])
        return findHSLWithLuminance(hue, randomSaturation, luminance, tolerance)
    }
    fun colorFromRandomLuminance(hue: Int, saturation: Double, tolerance: Double): FloatArray{
        // reason why we use ThreadLocalRandom.current():
        // This takes care of seeding that varies. Due to concurrency issue, our program may call this function from the start seed again after a while.
        val randomLuminance = ThreadLocalRandom.current().nextDouble(
            this.luminanceRange[0], this.luminanceRange[1]
        )
        return findHSLWithLuminance(hue, saturation, randomLuminance, tolerance)
    }

    fun colorSetFromRandomHue(setSize: Int, saturation: Double, luminance: Double, tolerance: Double): List<FloatArray> {
        val newColorList = mutableListOf<FloatArray>()
        for (i in 0 until setSize){
            newColorList.add(colorFromRandomHue(saturation, luminance, tolerance))
        }
        if(luminance.toInt()==-1 || saturation.toInt()==-1){newColorList.shuffle()}
        return newColorList.toList()
    }

    fun colorSetFromUniformLuminance(setSize: Int, hue: Int, saturation: Double, tolerance: Double): List<FloatArray> {
        val randomizeHue = hue == -1
        val randomizeSaturation = saturation.toInt() == -1
        val newColorList = mutableListOf<FloatArray>()
        val luminanceStep = (this.luminanceRange[1]-this.luminanceRange[0])/setSize
        var luminance = this.luminanceRange[0]
        for (i in 0 until setSize){
            var color = when(true){
                randomizeHue-> this.colorFromRandomHue(saturation, luminance, tolerance)
                randomizeSaturation -> this.colorFromRandomSaturation(hue, luminance, tolerance)
                else -> this.findHSLWithLuminance(hue, saturation,luminance,tolerance)
            }
            newColorList.add(i,color)
            luminance+=luminanceStep
        }
        if(randomizeHue || randomizeSaturation){newColorList.shuffle()}

        return newColorList.toList()
    }

    fun saturationForLevelLow2High(numSteps: Int, position: Int): Double {
        // used for getting saturation setting for specific level in ExerciseSet
        val saturationStep = (this.saturationRange[1]-this.saturationRange[0])/numSteps
        return this.saturationRange[0] + saturationStep*position

    }

    fun saturationForLevelHigh2Low(numSteps: Int, position: Int, minSaturation: Double): Double {
        val saturationStep = (this.saturationRange[1]-(this.saturationRange[0]+minSaturation))/numSteps
        return this.saturationRange[1] - saturationStep*position

    }

    fun hueForLevel(level: Int, randomizeStartFromLevel: Int): Int {
        // used for getting saturation setting for specific level in ExerciseSet
        return if (level>= randomizeStartFromLevel){
            -1
        } else{
            ThreadLocalRandom.current().nextInt(this.hueRange[0], this.hueRange[1])
        }
    }

    fun luminanceForLevel(level: Int, difficultLevel: Int): Double {
        return if (level>= difficultLevel){
            -1.toDouble()
        } else{
            ThreadLocalRandom.current().nextDouble(this.luminanceRange[0], this.luminanceRange[1])
        }
    }

    fun complementaryColor(targetHue: Int, mainSaturation: Double, tolerance: Double): FloatArray {
        val complementaryHue = (targetHue+180)%360
        return colorFromRandomLuminance(complementaryHue,mainSaturation,tolerance)
    }

    fun triadColor(targetHue: Int, mainSaturation: Double, tolerance: Double): FloatArray {
        val complementaryHue = (targetHue+90)%360
        return colorFromRandomLuminance(complementaryHue,mainSaturation,tolerance)
    }

}