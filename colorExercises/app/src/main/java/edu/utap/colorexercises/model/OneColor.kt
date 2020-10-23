package edu.utap.colorexercises.model

import android.graphics.Color
import android.util.Log
import androidx.core.graphics.ColorUtils
import kotlin.math.abs
import kotlin.math.min

//named OneColor instead of Color to avoid conflict with Android class
class OneColor(var hsl: FloatArray) {
    // Changed input from hex to hsl, hsl is more easily used within the application for conversion
    // hex is not supported in Android Color except for parsing step as following:
    // private val intColor = Color.parseColor(id)
    private var luminance = 0.toDouble()
    private val intColor = ColorUtils.HSLToColor(hsl)

    init {
        //ColorUtils.colorToHSL(intColor,hsl)
        luminance = ColorUtils.calculateLuminance(intColor)
    }

    companion object{
        private fun Contrast(firstColor: OneColor, secondColor: OneColor): Double {

            val firstLuminance = firstColor.getLuminance()
            val secondLuminance = secondColor.getLuminance()
            val contrast: Double
            if (firstLuminance>= secondLuminance){
                //formula for calculating relative contrast, commonly between 1 to 21
                contrast = (firstLuminance+0.05)/(secondLuminance+0.05)
            }else{
                contrast =  (secondLuminance+0.05)/(firstLuminance+0.05)
            }
            //Log.d("XXX OneColor: ", "Contrast is $contrast")
            return contrast
        }

        private fun LuminanceDifference(firstColor: OneColor, secondColor: OneColor): Double {

            return abs(firstColor.getLuminance()-secondColor.getLuminance())
        }

        private fun HueDistance(firstColor: OneColor, secondColor: OneColor): Double{
            //calculate distance on hue wheel, return number between 0-1
            val degreeDifference = abs(firstColor.getHSL()[0]-secondColor.getHSL()[0])
            val degreeDistance = min(degreeDifference, 360-degreeDifference)
            return (degreeDistance/360).toDouble()
        }

        fun match(criteria:String, firstColor: OneColor, secondColor: OneColor, Threshold: Float): Boolean {
            //TODO: moving functions for judging colors to separate class may make more sense
            val value: Double = when (criteria){
                "MATCHVALUE"-> LuminanceDifference(firstColor, secondColor)
                "MATCHHUE"-> HueDistance(firstColor, secondColor)
                //complementary color has hue distance 0.5
                "COMPLEMENTARY" -> abs(0.5-HueDistance(firstColor, secondColor))
                //triad color has hue distance
                "TRIAD" -> min(abs(0.33-HueDistance(firstColor, secondColor)), abs(0.67-HueDistance(firstColor, secondColor)))
                else -> 0.toDouble()
            }
            //Log.d("XXX OneColor: ", "value: $value")
            return value <= Threshold
        }
    }



    fun getHSL() = this.hsl
    fun getInt() = this.intColor

    fun getLuminance(): Double {
        return luminance
    }
}