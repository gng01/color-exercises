package edu.utap.colorexercises.service

import edu.utap.colorexercises.model.OneColor
import kotlin.math.abs
import kotlin.math.min

class ColorJudge {
    companion object{
        fun contrast(firstColor: OneColor, secondColor: OneColor): Double {

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

        fun luminanceDifference(firstColor: OneColor, secondColor: OneColor): Double {

            return abs(firstColor.getLuminance()-secondColor.getLuminance())
        }

        fun hueDistance(firstColor: OneColor, secondColor: OneColor): Double{
            //calculate distance on hue wheel, return number between 0-1
            val degreeDifference = abs(firstColor.getHSL()[0]-secondColor.getHSL()[0])
            val degreeDistance = min(degreeDifference, 360-degreeDifference)
            return (degreeDistance/360).toDouble()
        }

        fun complementary(firstColor: OneColor, secondColor: OneColor): Double{
            return abs(0.5-hueDistance(firstColor, secondColor))
        }

        fun triad(firstColor: OneColor, secondColor: OneColor): Double{
            return min(abs(0.33-hueDistance(firstColor, secondColor)), abs(0.67-hueDistance(firstColor, secondColor)))
        }

        fun accuracy(firstColor: OneColor, secondColor: OneColor, criteria: (OneColor, OneColor)-> Double): Double {
            // The concepts here are commonly referred to as Dependency Injection,
            // and a related concept (which we've discussed) is Inversion of Control
            // you plug in the criteria as if it's a cartridge, and it does its job.

            // alternatively, you could have a separate class, like LuminanceJudgeable, ComplementaryJudgeable, etc,
            // all of which implement an interface called Judgeable, with a criteria function that needs to be implemented.
            // Then you'd have this ColorJudge do the judging on a Judgeable object.
            // adding a class for these would be unnecessary.  Just wanted to show an alternative implementation.
            val value: Double = criteria(firstColor,secondColor)
            //Log.d("XXX OneColor: ", "value: $value")
            return (1-value)*100 //100 full score scale
        }
    }
}