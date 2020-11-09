package edu.utap.colorexercises.model

import android.app.Application
import android.os.Environment
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import edu.utap.colorexercises.MainActivity


class MainViewModel(application: Application,
    // Modified from Firechat FC
                    private val state: SavedStateHandle
)
    : AndroidViewModel(application) {
    companion object {
    }
    private val appContext = getApplication<Application>().applicationContext
    private var storageDir =
        getApplication<Application>().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var firebaseAuthLiveData = FirestoreAuthLiveData()
    private lateinit var crashMe: String
    private var palettes = MutableLiveData<List<Palette>>()


    fun observeFirebaseAuthLiveData(): LiveData<FirebaseUser?> {
        return firebaseAuthLiveData
    }
    private fun myUid(): String? {
        return firebaseAuthLiveData.value?.uid
    }
    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    fun observePalettes(): LiveData<List<Palette>>{
        return palettes
    }

    fun savePalette(palette: Palette){
        Log.d(
            "HomeViewModel",
            String.format(
                "savePalette name(%s)",
                palette.name
            )
        )
        palette.id = db.collection("palettes").document().id
        db.collection("palettes")
            .document(palette.id)
            .set(palette)
            .addOnSuccessListener {
                Log.d(
                    javaClass.simpleName,
                    "Palette create id: ${palette.id}"
                )
                getAllPalettes()
            }
            .addOnFailureListener { e ->
                Log.d(javaClass.simpleName, "savePalette FAILED")
                Log.w(javaClass.simpleName, "Error ", e)
            }
    }

    fun getAllPalettes(){
        // get list of palettes from database
        if(FirebaseAuth.getInstance().currentUser == null) {
            Log.d(javaClass.simpleName, "Can't get chat, no one is logged in")
            palettes.value = listOf()
            return
        }
        db.collection("palettes")
            .orderBy("timeStamp")
            .limit(20)
            .addSnapshotListener { querySnapshot, ex ->
                if (ex != null) {
                    Log.w(MainActivity.TAG, "listen:error", ex)
                    return@addSnapshotListener
                }
                Log.d(MainActivity.TAG, "fetch ${querySnapshot!!.documents.size}")
                palettes.value = querySnapshot.documents.mapNotNull {
                    it.toObject(Palette::class.java)
                }
            }
    }
}