package edu.utap.colorexercises

import androidx.core.graphics.ColorUtils
import edu.utap.colorexercises.model.ColorSetGenerator
import edu.utap.colorexercises.model.OneColor
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(
    RobolectricTestRunner::class
)
class ColorSetGeneratorUnitTest {
    @Test
    fun makeARGB() {
        val setLuminance = 0.8
        val generator = ColorSetGenerator()
        var color = generator.MakeARGB(setLuminance,0.3, 0.5)
        var colorObject  = OneColor(color)
        print("color: $color, luminance: ${colorObject.getLuminance()}\n")
        color = generator.MakeARGB(setLuminance,0.35, 0.35)
        colorObject  = OneColor(color)
        print("color: $color, luminance: ${colorObject.getLuminance()}\n")
        //out of bound
        color = generator.MakeARGB(setLuminance,0.8, 0.9)
        colorObject  = OneColor(color)
        print("color: $color, luminance: ${colorObject.getLuminance()}\n")
    }
    @Test
    //attempt to construct color with given luminance using HSL, failed, seems not linear
    //online resources such as https://stackoverflow.com/questions/61525100/convert-from-relative-luminance-to-hsl
    //do this by incrementing l in hsl by small steps. Very inefficient, will continue to use RGB generation scheme.
    fun matchColorFromHSL() {
        val setHue = 50
        val hslBottom = FloatArray(3)
        hslBottom[0] = setHue.toFloat()
        hslBottom[1] = 1.toFloat()
        hslBottom[2] = 0.toFloat()
        val colorBottom = ColorUtils.HSLToColor(hslBottom)
        val luminanceBottom = ColorUtils.calculateLuminance(colorBottom)
        val hslTop = FloatArray(3)
        hslTop[0] = setHue.toFloat()
        hslTop[1] = 1.toFloat()
        hslTop[2] = 1.toFloat()
        val colorTop = ColorUtils.HSLToColor(hslTop)
        val luminanceTop = ColorUtils.calculateLuminance(colorTop)
        val targetLuminance= 0.5.toFloat()
        val setRelativeLuminance = (targetLuminance-luminanceBottom)/(luminanceTop-luminanceBottom)
        val hsl = FloatArray(3)
        hsl[0] = setHue.toFloat()
        hsl[1] = 1.toFloat()
        hsl[2] = setRelativeLuminance.toFloat()
        val color = ColorUtils.HSLToColor(hsl)
        val luminance = ColorUtils.calculateLuminance(color)
        print("Top luminance ${luminanceTop}, buttom luminance ${luminanceBottom} \n")
        print("Return color ${hsl.toList()} \n")
        print("Target luminance ${targetLuminance}, return luminance ${luminance} \n")
    }
}