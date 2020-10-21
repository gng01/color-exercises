package edu.utap.colorexercises.api

import android.graphics.Color
import android.graphics.ColorSpace
import androidx.core.graphics.ColorUtils
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.min

//named OneColor instead of Color to avoid conflict with Android class
class OneColor(var id: String) {
    private val intColor = Color.parseColor(id)
    private lateinit var hsl: FloatArray
    private var luminance = 0.toDouble()

    init {
        ColorUtils.colorToHSL(intColor,hsl)
        luminance = ColorUtils.calculateLuminance(intColor)
    }
    fun Contrast(other: OneColor): Double {
        val otherLuminance = other.getLuminance()
        if (this.luminance>= otherLuminance){
            //formula for calculating relative contrast, commonly between 1 to 21
            return (this.luminance+0.05)/(otherLuminance+0.05)
        }else{
            return (otherLuminance+0.05)/(this.luminance+0.05)
        }
    }

    fun HueDistance(other: OneColor): Double{
        //calculate distance on hue wheel, return number between 0-1
        val degreeDifference = abs(this.hsl[0]-other.getHSL()[0])
        val degreeDistance = min(degreeDifference, 360-degreeDifference)
        return (degreeDistance/360).toDouble()
    }

    fun match(criteria: (OneColor)->Double, other: OneColor, lowerThreshold: Float,upperThreshold: Float): Boolean {
        return criteria(other) in lowerThreshold..upperThreshold
    }

    fun getHSL() = this.hsl

    fun getLuminance(): Double {
        return luminance
    }
}