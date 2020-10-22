package edu.utap.colorexercises.model

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import kotlin.math.abs
import kotlin.math.min

//named OneColor instead of Color to avoid conflict with Android class
class OneColor(var id: String) {
    private val intColor = Color.parseColor(id)
    private var hsl = floatArrayOf(0.toFloat(),0.toFloat(),0.toFloat())
    private var luminance = 0.toDouble()

    init {
        ColorUtils.colorToHSL(intColor,hsl)
        luminance = ColorUtils.calculateLuminance(intColor)
    }

    companion object{
        private fun Contrast(firstColor: OneColor, secondColor: OneColor): Double {
            val firstLuminance = firstColor.getLuminance()
            val secondLuminance = secondColor.getLuminance()
            if (firstLuminance>= secondLuminance){
                //formula for calculating relative contrast, commonly between 1 to 21
                return (firstLuminance+0.05)/(secondLuminance+0.05)
            }else{
                return (secondLuminance+0.05)/(firstLuminance+0.05)
            }
        }

        private fun HueDistance(firstColor: OneColor, secondColor: OneColor): Double{
            //calculate distance on hue wheel, return number between 0-1
            val degreeDifference = abs(firstColor.getHSL()[0]-secondColor.getHSL()[0])
            val degreeDistance = min(degreeDifference, 360-degreeDifference)
            return (degreeDistance/360).toDouble()
        }

        fun match(criteria:String, firstColor: OneColor, secondColor: OneColor, Threshold: Float): Boolean {
            val value: Double = when (criteria){
                "MATCHVALUE"-> Contrast(firstColor, secondColor)
                "MATCHHUE"-> HueDistance(firstColor, secondColor)
                //complementary color has hue distance 0.5
                "COMPLEMENTARY" -> abs(0.5-HueDistance(firstColor, secondColor))
                //triad color has hue distance
                "TRIAD" -> min(abs(0.33-HueDistance(firstColor, secondColor)), abs(0.67-HueDistance(firstColor, secondColor)))
                else -> 0.toDouble()
            }
            return value <= Threshold
        }
    }



    fun getHSL() = this.hsl
    fun getInt() = this.intColor

    fun getLuminance(): Double {
        return luminance
    }
}