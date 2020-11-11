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
        private var user = User()
    }
    private val appContext = getApplication<Application>().applicationContext
    private var storageDir =
        getApplication<Application>().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var firebaseAuthLiveData = FirestoreAuthLiveData()
    private lateinit var crashMe: String
    private var palettes = MutableLiveData<List<Palette>>()

    private var TAG = "MainViewModel"


    fun observeFirebaseAuthLiveData(): LiveData<FirebaseUser?> {
        return firebaseAuthLiveData
    }
    private fun cloudUid(): String? {
        return firebaseAuthLiveData.value?.uid
    }
    private fun cloudUserName(): String? {
        return firebaseAuthLiveData.value?.displayName
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }



    fun observePalettes(): LiveData<List<Palette>>{
        return palettes
    }


    fun getUser(){
        user.id = cloudUid()
        if (user.id==null){
            Log.d(TAG,"getUser Failed: no user logged in")
            return
        }
        //If the document does not exist, it will be created.
        // If the document does exist, its contents will be overwritten
        // with the newly provided data
        val userRef = db.collection("users").document(user.id!!)
        userRef.get()
            . addOnSuccessListener {
                if (it?.toObject(User::class.java) != null){
                    user = it.toObject(User::class.java)!!

                }else{
                    updateUser()
                }
                Log.d(
                    javaClass.simpleName,
                    "getUser id: ${user.id}"
                )
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "getUser FAILED")
                Log.w(TAG, "Error ", e)
            }

    }


    private fun updateUser(){
        //TODO:
        // BUG sometimes happen: when signed in modes is overriden to 0
        if (user.id==null){
            Log.d(TAG,"createUser Failed: no user logged in")
            return
        }
        db.collection("users")
            .document(user.id!!)
            .set(user)
            .addOnSuccessListener {
                Log.d(
                    TAG,
                    "updateUser success, id: ${user.id}"
                )
                getAllPalettes()
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "createUser FAILED")
                Log.w(TAG, "Error ", e)
            }
    }

    fun getModeLevels(mode: String): MutableList<Int> {
        return if (user.modes.containsKey(mode)){
            (0..user.modes[mode]!!).toMutableList()
        }else{
            mutableListOf(0)
        }
    }

    fun setMode(mode: String, level: Int){
        if (user.modes[mode]==null||user.modes[mode]!! <level){
            user.modes[mode] = level
            updateUser()
        }
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
                Log.d(TAG, "savePalette FAILED")
                Log.w(TAG, "Error ", e)
            }
    }


    fun getAllPalettes(){
        // get list of palettes from database
        if(FirebaseAuth.getInstance().currentUser == null) {
            Log.d(javaClass.simpleName, "Can't get Palettes, no one is logged in")
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