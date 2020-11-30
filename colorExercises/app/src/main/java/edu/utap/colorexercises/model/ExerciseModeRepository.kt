package edu.utap.colorexercises.model

data class ExerciseMode(val id: String, val displayName: String, val description: String, val help: String)
class ExerciseModesRepository {
    companion object{
        private var ExerciseModes = listOf(
            ExerciseMode("MATCHVALUE"
                ,"Match Value"
                , "Match Value: choose on one of outer colors whose value that matches the value of center color"
            , "https://thevirtualinstructor.com/Value.html"),

            ExerciseMode("MATCHHUE"
            ,"Match Hue"
            ,"Match Hue: click on one of colors in the outer circle whose hue matches the center color"
            ,"https://en.wikipedia.org/wiki/Hue"),

            ExerciseMode("COMPLEMENTARY"
                ,"Complementary"
                ,"Complementary: click on one of outer colors complementary to center color"
                ,"https://en.wikipedia.org/wiki/Complementary_colors"),

            ExerciseMode("TRIAD"
                ,"Triadic"
                ,"Triadic: click on one of outer color which is in triad relation with the center color"
                ,"http://paletton.com/wiki/index.php?title=Triad")
        )
    }

    fun fetchModesList(): List<ExerciseMode> {
        return ExerciseModes
    }
}