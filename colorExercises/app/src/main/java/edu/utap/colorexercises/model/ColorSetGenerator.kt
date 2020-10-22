package edu.utap.colorexercises.model

import android.graphics.Color
import androidx.core.graphics.ColorUtils

class ColorSetGenerator {
    companion object{

    }
    //Code copied from https://stackoverflow.com/questions/4849174/how-to-generate-different-colors-of-same-luminance-for-line-chart-in-java
    // A function to generate color from given luminance and color coordinates
    fun makeARGB(luminance: Double, colorCoordinate_x: Double, colorCoordinate_y: Double): String {


        // Out of gamut colour
        var rgb = -0x7f7f80
        val X = luminance * colorCoordinate_x / colorCoordinate_y
        val Z = luminance * (1 - colorCoordinate_x - colorCoordinate_y) / colorCoordinate_y
        val rlin = +3.2046 * X + -1.5372 * luminance + -0.4986 * Z
        val r: Double = gamma(rlin)
        val ir = (r * 255.0).toInt()
        if (ir >= 0 && ir < 256) {
            val glin = -0.9689 * X + +1.8758 * luminance + +0.0415 * Z
            val g: Double = gamma(glin)
            val ig = (g * 255.0).toInt()
            if (ig >= 0 && ig < 256) {
                val blin = +0.0557 * X + -0.2040 * luminance + +1.0570 * Z
                val b: Double = gamma(blin)
                val ib = (b * 255.0).toInt()
                if (ib >= 0 && ib < 256) {
                    rgb = -0x1000000 + (ir shl 16) + (ig shl 8) + (ib shl 0)
                }
            }
        }
        val red = rgb shr 16 and 0xFF
        val green = rgb shr 8 and 0xFF
        val blue = rgb and 0xFF
        var hsl = floatArrayOf(0.toFloat(),0.toFloat(),0.toFloat())
        //need to convert to HSL here due to limitation of ColorUtils
        //ColorUtils.RGBToHSL(red,green,blue, hsl)
        // return integer color coding used by default in Kotlin
        val hex = String.format("#%02x%02x%02x", red, green, blue)
        return hex
    }
    private fun gamma(l: Double): Double {
        return if (l < 0.0031308) {
            l * 12.92
        } else {
            1.055 * Math.pow(l, 1.0 / 2.4) - 0.055
        }
    }
}