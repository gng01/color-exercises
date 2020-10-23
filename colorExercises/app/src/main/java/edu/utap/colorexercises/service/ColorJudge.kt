package edu.utap.colorexercises.service

import edu.utap.colorexercises.model.OneColor
import kotlin.math.abs
import kotlin.math.min

class ColorJudge {
    companion object{
        fun Contrast(firstColor: OneColor, secondColor: OneColor): Double {

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

        fun LuminanceDifference(firstColor: OneColor, secondColor: OneColor): Double {

            return abs(firstColor.getLuminance()-secondColor.getLuminance())
        }

        fun HueDistance(firstColor: OneColor, secondColor: OneColor): Double{
            //calculate distance on hue wheel, return number between 0-1
            val degreeDifference = abs(firstColor.getHSL()[0]-secondColor.getHSL()[0])
            val degreeDistance = min(degreeDifference, 360-degreeDifference)
            return (degreeDistance/360).toDouble()
        }

        fun Complementary(firstColor: OneColor, secondColor: OneColor): Double{
            return abs(0.5-HueDistance(firstColor, secondColor))
        }

        fun Triad(firstColor: OneColor, secondColor: OneColor): Double{
            return min(abs(0.33-HueDistance(firstColor, secondColor)), abs(0.67-HueDistance(firstColor, secondColor)))
        }

        fun match(firstColor: OneColor, secondColor: OneColor, threshold: Float,criteria: (OneColor, OneColor)-> Double): Boolean {
            //TODO: moving functions for judging colors to separate class may make more sense
            val value: Double = criteria(firstColor,secondColor)
            //Log.d("XXX OneColor: ", "value: $value")
            return value <= threshold
        }
    }
}