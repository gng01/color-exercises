package edu.utap.colorexercises.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class User(
    var id: String? = null,
    @ServerTimestamp val timeStamp: Timestamp? = null,

    var modes: MutableMap<String, Int> = mutableMapOf()
)