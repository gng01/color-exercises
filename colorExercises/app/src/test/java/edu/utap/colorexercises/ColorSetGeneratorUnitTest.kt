package edu.utap.colorexercises

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import edu.utap.colorexercises.model.ColorSetGenerator
import edu.utap.colorexercises.model.OneColor
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.random.Random

@RunWith(
    RobolectricTestRunner::class
)
class ColorSetGeneratorUnitTest {
    @Test
    fun makeARGB() {
        val generator = ColorSetGenerator()
        var color = generator.makeARGB(0.5,0.3, 0.5)
        var colorObject  = OneColor(color)
        print("color: $color, luminance: ${colorObject.getLuminance()}\n")
        color = generator.makeARGB(0.5,0.35, 0.35)
        colorObject  = OneColor(color)
        print("color: $color, luminance: ${colorObject.getLuminance()}\n")
        color = generator.makeARGB(0.5,0.8, 0.9)
        colorObject  = OneColor(color)
        print("color: $color, luminance: ${colorObject.getLuminance()}\n")

    }
}