package edu.utap.colorexercises.model

import android.graphics.Color
import kotlin.collections.mutableListOf as mutableListOf

class Palette {
    var id : Int = 0
    lateinit var name : String
    lateinit var user : User
    lateinit var colors : MutableList<String>


}