package edu.utap.colorexercises.model

import android.graphics.Color
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import kotlin.collections.mutableListOf as mutableListOf

data class FavoritedPalette (
    var id: String? = null,
    var name: String? = null,
    var colors: MutableList<String> = mutableListOf<String>("#AAAAAA"),
)