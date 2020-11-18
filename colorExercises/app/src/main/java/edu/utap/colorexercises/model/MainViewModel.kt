package edu.utap.colorexercises.model

import android.app.Application
import android.os.Environment
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
    private var userPalettes = MutableLiveData<List<Palette>>()
    private var allPalettes = listOf<Palette>()

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
        return userPalettes
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
                getUserPalettes(null)
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

    fun savePalette(palette: Palette, callback: ()->Unit){
        Log.d(
            "HomeViewModel",
            String.format(
                "savePalette name(%s)",
                palette.name
            )
        )
        palette.id = palette.id ?: db.collection("palettes").document().id
        palette.keywords.map { it.toLowerCase() }
        db.collection("palettes")
            .document(palette.id!!)
            .set(palette)
            .addOnSuccessListener {
                Log.d(
                    javaClass.simpleName,
                    "Palette create id: ${palette.id}"
                )
                callback()

                getUserPalettes(null)
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "savePalette FAILED")
                Log.w(TAG, "Error ", e)
            }
    }


    fun getUserPalettes(userId: String?){
        // get list of palettes from database
        if(FirebaseAuth.getInstance().currentUser == null) {
            Log.d(javaClass.simpleName, "Can't get Palettes, no one is logged in")
            userPalettes.value = listOf()
            return
        }

        var query = db.collection("palettes")

        // wondering if we can re-use these blocks of code. they are the same except for one line
        if (userId.isNullOrEmpty()) {
            query
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .limit(20)
                .addSnapshotListener { querySnapshot, ex ->
                    if (ex != null) {
                        Log.w(MainActivity.TAG, "listen:error", ex)
                        return@addSnapshotListener
                    }
                    Log.d(MainActivity.TAG, "fetch ${querySnapshot!!.documents.size}")
                    userPalettes.value = querySnapshot.documents.mapNotNull {
                        it.toObject(Palette::class.java)
                    }
                }
        } else {
            query
                .whereEqualTo("ownerUserID", userId) // only difference between this block of code and the one in the other condition
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .limit(20)
                .addSnapshotListener { querySnapshot, ex ->
                    if (ex != null) {
                        Log.w(MainActivity.TAG, "listen:error", ex)
                        return@addSnapshotListener
                    }
                    Log.d(MainActivity.TAG, "fetch ${querySnapshot!!.documents.size}")
                    userPalettes.value = querySnapshot.documents.mapNotNull {
                        it.toObject(Palette::class.java)
                    }
                }
        }
    }

    fun getAllPalettes(){
        // get list of palettes from database

        var query = db.collection("palettes")

        query
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .limit(20)
            .addSnapshotListener { querySnapshot, ex ->
                if (ex != null) {
                    Log.w(TAG, "listen:error", ex)
                    return@addSnapshotListener
                }
                Log.d(TAG, "fetch ${querySnapshot!!.documents.size}")
                allPalettes = querySnapshot.documents.mapNotNull {
                    it.toObject(Palette::class.java)
                }
            }

    }

    private var searchTerm = MutableLiveData<String>("")

    fun setSearchTerm(s: String){
        searchTerm.value = s
    }

    private fun filterList(): List<Palette>{
        Log.d(javaClass.simpleName,
            "Filter ${searchTerm.value}")
        getAllPalettes()
        val searchTermValue = searchTerm.value!!.toLowerCase()
        return allPalettes.        filter {
            it.keywords.contains(searchTermValue)
        }
    }

    private var livePalettes = MediatorLiveData<List<Palette>>().apply {
        addSource(searchTerm){ value = filterList()}
        value = allPalettes
    }

    fun observeLivePalettes(): LiveData<List<Palette>>{
        return livePalettes
    }

}