package edu.utap.colorexercises.model

class ExerciseSet {
    private lateinit var centerColor: OneColor
    private lateinit var colorList: List<OneColor>

    //TODO: Add Title to exercise Set
    fun AddColorList(centerColor: OneColor, colorList: List<OneColor>){
        this.centerColor = centerColor
        this.colorList = colorList
    }

    fun CorrectPosition(criteria: String, threshold: Double): List<Boolean> {
        val correctList = mutableListOf<Boolean>()
        this.colorList.forEachIndexed { i, color ->
            correctList.add(i,OneColor.match(criteria,this.centerColor,color,threshold.toFloat()))
        }
        return correctList.toList()
    }

    fun getCenter() = this.centerColor
    fun getColorList() = this.colorList
}