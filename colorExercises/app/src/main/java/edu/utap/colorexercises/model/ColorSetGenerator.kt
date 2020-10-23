package edu.utap.colorexercises.model

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import kotlin.random.Random

class ColorSetGenerator {
    private val invalidColorHexCode = "#808080"
    companion object{


    }

    fun RandomColorGeneratorFromLuminance(luminance: Double){
        var xBound = mutableListOf<Double>(0.0,1.0)
        var yBound = mutableListOf<Double>(0.0,1.0)
        var colorCoordinate_x = 0.0
        var colorCoordinate_y = 0.0
        var generatedColorInHex = this.invalidColorHexCode
        while (NotValidColor(generatedColorInHex)){
            //Random generation within given bound
                colorCoordinate_x = Random.nextDouble(xBound[0],xBound[1])
                colorCoordinate_y = Random.nextDouble(yBound[0],yBound[1])
            //shrink range every time generate invalid color, thus overall generation should be quick
            //Challenge: change upper or lower boundary?

        }


    }

    private fun NotValidColor(colorHexCode: String): Boolean {
        return colorHexCode==this.invalidColorHexCode
    }
    //Code modified from https://stackoverflow.com/questions/4849174/how-to-generate-different-colors-of-same-luminance-for-line-chart-in-java
    // A function to generate color from given luminance and color coordinates
    // issue with RGB generation scheme: not all x,y value in [0,1] for colorCoordinate will give displayable color
    // select values between 0.3-0.5 to start with, but there are some colors out side this region
    // valid region is an irregular color space plot, it changes with luminance setting
    // invalid color returns: color: #808080, luminance: 0.21586050011389923
    // regenerate color if see this behavior

    fun MakeARGB(luminance: Double, colorCoordinate_x: Double, colorCoordinate_y: Double): String {
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
        //TODO: decide whether use hex or integer color as key to generate OneColor object
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