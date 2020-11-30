package edu.utap.colorexercises.model

import edu.utap.colorexercises.R

class ButtonsRepository {
    companion object{
        private var buttons = listOf(
            R.drawable.ic_button_0,
            R.drawable.ic_button_1,
            R.drawable.ic_button_2,
            R.drawable.ic_button_3,
            R.drawable.ic_button_4,
            R.drawable.ic_button_5,
            R.drawable.ic_button_6,
            R.drawable.ic_button_7,
            R.drawable.ic_button_8,
            R.drawable.ic_button_9,
            R.drawable.ic_button_10
        )
    }

    fun randomButton(): Int {
        return buttons.shuffled().take(1)[0]
    }
}