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


    fun getHSL() = this.hsl
    fun getInt() = this.intColor

    fun getLuminance(): Double {
        return luminance
    }
}