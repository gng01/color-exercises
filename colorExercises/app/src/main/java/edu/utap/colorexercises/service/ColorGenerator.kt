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
    fun FindHSLWithLuminance(hue: Int, saturation: Double, luminance: Double, tolerance: Double): FloatArray {
        var lightness = 0.5
        var hsl = FloatArray(3)
        var curLuminance = -1.0
        var lowerBound = this.luminanceRange[0]
        var upperBound = this.luminanceRange[1]
        print("started")
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
            //Log.d("XXX ColorSetGenerator:", "${lowerBound} - ${upperBound}")
            // randomize the disection to get more random numbers
            lightness = (lowerBound+upperBound)/ThreadLocalRandom.current().nextInt(1,5)
        }
        return hsl
    }

    fun ColorFromRandomHue(saturation: Double,luminance: Double,tolerance: Double): FloatArray{
        return FindHSLWithLuminance(ThreadLocalRandom.current().nextInt(this.hueRange[0], this.hueRange[1]), saturation, luminance, tolerance)
    }
    fun ColorFromRandomSaturation(hue: Int,luminance: Double,tolerance: Double): FloatArray{
        return FindHSLWithLuminance(hue, ThreadLocalRandom.current().nextDouble(this.saturationRange[0], this.saturationRange[1]), luminance, tolerance)
    }
    fun ColorFromRandomLuminance(hue: Int, saturation: Double,tolerance: Double): FloatArray{
        // reason why we use ThreadLocalRandom.current():
        // This takes care of seeding that varies. Due to concurrency issue, our program may call this function from the start seed again after a while.
        return FindHSLWithLuminance(hue, saturation, ThreadLocalRandom.current().nextDouble(
            this.luminanceRange[0], this.luminanceRange[1]), tolerance)
    }

    fun ColorSetFromUniformLuminance(setSize: Int, hue: Int, saturation: Double,tolerance: Double): List<FloatArray> {
        val newColorList = mutableListOf<FloatArray>()
        val luminanceStep = (this.luminanceRange[1]-this.luminanceRange[0])/setSize
        var luminance = this.luminanceRange[0]
        for (i in 0 until setSize){
            var color = when(-1){
                hue-> this.ColorFromRandomHue(saturation, luminance, tolerance)
                saturation.toInt() -> this.ColorFromRandomSaturation(hue, luminance, tolerance)
                else -> this.FindHSLWithLuminance(hue, saturation,luminance,tolerance)
            }
            newColorList.add(i,color)
            luminance+=luminanceStep
        }
        //if randomized: shuffle list randomly
        if(hue==-1 || saturation.toInt()==-1){newColorList.shuffle()}

        return newColorList.toList()
    }

    fun SaturationForLevel(numSteps: Int, position: Int): Double {
        // used for getting saturation setting for specific level in ExerciseSet
        val saturationStep = (this.saturationRange[1]-this.saturationRange[0])/numSteps
        return this.saturationRange[0] + saturationStep*position

    }

    fun HueForLevel(level: Int, difficultLevel: Int): Int {
        // used for getting saturation setting for specific level in ExerciseSet
        return if (level>= difficultLevel){
            -1
        } else{
            ThreadLocalRandom.current().nextInt(this.hueRange[0], this.hueRange[1])
        }
    }


}