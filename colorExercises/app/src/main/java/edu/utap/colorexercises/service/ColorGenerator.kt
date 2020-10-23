package edu.utap.colorexercises.service

import android.annotation.SuppressLint
import androidx.core.graphics.ColorUtils
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.abs

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
            // randomize the disection to get more random numbers
            lightness = (lowerBound+upperBound)/ThreadLocalRandom.current().nextInt(1,5)
        }
        return hsl
    }

    fun ColorFromRandomHue(saturation: Double,luminance: Double,tolerance: Double): FloatArray{
        return FindHSLWithLuminance(ThreadLocalRandom.current().nextInt(hueRange[0], hueRange[1]), saturation, luminance, tolerance)
    }
    fun ColorFromRandomSaturation(hue: Int,luminance: Double,tolerance: Double): FloatArray{
        return FindHSLWithLuminance(hue, ThreadLocalRandom.current().nextDouble(saturationRange[0], saturationRange[1]), luminance, tolerance)
    }
    fun ColorFromRandomLuminance(hue: Int, saturation: Double,tolerance: Double): FloatArray{
        // reason why we use ThreadLocalRandom.current():
        // This takes care of seeding that varies. Due to concurrency issue, our program may call this function from the start seed again after a while.
        return FindHSLWithLuminance(hue, saturation, ThreadLocalRandom.current().nextDouble(
            luminanceRange[0], luminanceRange[1]), tolerance)
    }

}