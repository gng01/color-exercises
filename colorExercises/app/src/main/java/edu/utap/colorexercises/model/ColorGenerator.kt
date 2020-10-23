package edu.utap.colorexercises.model

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import androidx.core.graphics.ColorUtils
import kotlin.math.abs
import kotlin.random.Random

class ColorGenerator {
    private val invalidColorHexCode = "#808080"
    companion object{
        val hueRange  = listOf<Int>(0,360)
        val saturationRange = listOf<Double>(0.0,1.0)
        val luminanceRange = listOf<Double>(0.0,1.0)

    }
    // A better idea for generate color with given luminance than analytic calculation: binary search in l space of hsl:
    // perceived luminance and lightness given in hsl space are NOT THE SAME!
    // tolerance: absolute difference between the two luminance value
    @SuppressLint("Range")
    private fun FindHSLWithLuminance(hue: Int, saturation: Double, luminance: Double, tolerance: Double): FloatArray {
        var lightness = 0.5
        var hsl = FloatArray(3)
        var curLuminance = -1.0
        var lowerBound = 0.0
        var upperBound = 1.0
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
            lightness = (lowerBound+upperBound)/2
        }
        return hsl
    }

    fun ColorFromRandomHue(saturation: Double,luminance: Double,tolerance: Double): FloatArray{
        return FindHSLWithLuminance(Random.nextInt(hueRange[0], hueRange[1]), saturation, luminance, tolerance)
    }
    fun ColorFromRandomSaturation(hue: Int,luminance: Double,tolerance: Double): FloatArray{
        return FindHSLWithLuminance(hue, Random.nextDouble(saturationRange[0], saturationRange[1]), luminance, tolerance)
    }
    fun ColorFromRandomLuminance(hue: Int, saturation: Double,tolerance: Double): FloatArray{
        return FindHSLWithLuminance(hue, saturation, Random.nextDouble(luminanceRange[0], luminanceRange[1]), tolerance)
    }

}