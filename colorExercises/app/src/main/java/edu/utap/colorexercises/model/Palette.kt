package edu.utap.colorexercises.model

import android.graphics.Color
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import kotlin.collections.mutableListOf as mutableListOf

data class Palette(
    var id : String = "",
    var name : String? = null,
    var keywords: MutableList<String> = mutableListOf<String>(""),
    var colors : MutableList<String> = mutableListOf<String>("#AAAAAA"),
    @ServerTimestamp
    val timeStamp: Timestamp? = null,


    var ownerUserID : String?= null,
    var ownerUserName: String?= null,
    var favoritedUsersList: MutableList<String> = mutableListOf<String>(""),


    )