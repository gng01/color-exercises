package edu.utap.colorexercises.model

data class ExerciseMode(val id: String, val displayName: String, val description: String, val help: String)
class ExerciseModesRepository {
    companion object{
        private var ExerciseModes = listOf(
            ExerciseMode("MATCHVALUE"
                ,"Match Value"
                , "Match Value: click on one of outer circles whose value that matches the value of center circle"
            , "https://thevirtualinstructor.com/Value.html"),

            ExerciseMode("MATCHHUE"
            ,"Match Hue"
            ,"Match Hue: click on one of outer circles whose hue that matches the hue of center circle"
            ,"https://en.wikipedia.org/wiki/Hue"),

            ExerciseMode("COMPLEMENTARY"
                ,"Complementary"
                ,"Complementary: click on one of outer circles whose hue that is complementary to the hue of center circle"
                ,"https://en.wikipedia.org/wiki/Hue"),

            ExerciseMode("TRIAD"
                ,"Triad"
                ,"Triad: click on one of outer circles whose hue that is in triad relation with the hue of center circle"
                ,"https://en.wikipedia.org/wiki/Hue")
        )
    }

    fun fetchModesList(): List<ExerciseMode> {
        return ExerciseModes
    }
}