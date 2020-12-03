package edu.utap.colorexercises.model

import android.app.Application
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.utap.colorexercises.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.internal.waitMillis


class MainViewModel(application: Application,
    // Modified from Firechat FC
                    private val state: SavedStateHandle
)
    : AndroidViewModel(application) {
    companion object {
        private var user = User()
        private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
        private var firebaseAuthLiveData = FirestoreAuthLiveData()
    }

    private var userPalettes = MutableLiveData<List<Palette>>()
    private var allPalettes = MutableLiveData<List<Palette>>()

    private var TAG = "MainViewModel"
    public  var isShowingFavorites = false

    fun observeFirebaseAuthLiveData(): LiveData<FirebaseUser?> {
        return firebaseAuthLiveData
    }

    fun setLocalUserID(Uid: String?){
        user.id = Uid;
    }

    fun getUid() : String?{
        return user.id
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }



    fun observePalettes(): LiveData<List<Palette>>{
        return userPalettes
    }


    fun getUser(){
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
                getUserPalettes(null, {})
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

    fun AddToFavoritePalettes(palette: FavoritedPalette) {
        if (user.favoritePalettes?.filter{it.id == palette.id}?.count() == 0) {
            user.favoritePalettes?.add(palette)
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
                getUserPalettes(null, {})
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "savePalette FAILED")
                Log.w(TAG, "Error ", e)

            }
    }


    fun getUserPalettes(userId: String?, callback: (palettes: List<Palette>?)->Unit) {
        // get list of palettes from database
        if(FirebaseAuth.getInstance().currentUser == null) {
            Log.d(javaClass.simpleName, "Can't get Palettes, no one is logged in")
            userPalettes.value = listOf()
            callback(userPalettes.value)
            return
        }

        var query = db.collection("palettes")

        // wondering if we can re-use these blocks of code. they are the same except for one line:
        // Should not be able to retrieve palette if userID is null
        if (!userId.isNullOrEmpty()) {
            query
                .whereEqualTo("ownerUserID", userId)
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
                    callback(userPalettes.value)
                }
        }
    }

    fun getAllPalettes(){
        // get list of all palettes from database

        var query = db.collection("palettes")
        query
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .limit(100)
            .addSnapshotListener { querySnapshot, ex ->
                if (ex != null) {
                    Log.w(TAG, "listen:error", ex)
                    return@addSnapshotListener
                }
                Log.d(TAG, "fetch all ${querySnapshot!!.documents.size}")
                allPalettes.value= querySnapshot.documents.mapNotNull {
                    it.toObject(Palette::class.java)
                }
            }


    }

    private fun removeAllPalettes(){
        allPalettes.value = listOf()
    }

    private var searchTerm = MutableLiveData<String>("")

    fun setSearchTerm(s: String){
        searchTerm.value = s
    }

    fun filterList(field: String) {
        if(getUid()==null && field=="favoritedUsersList"){
            removeAllPalettes()
            return
        }
        if(searchTerm==null || searchTerm.value!!.isEmpty() && field=="keywords"){
            getAllPalettes()
            return
        }
        val searchString = when(field){
            "favoritedUsersList"-> user.id
            "keywords" -> searchTerm.value?.toLowerCase()
            else -> null
        }

        Log.d(TAG, "searchString: $searchString")



        (searchString)?.let {
            var query = db.collection("palettes")

            query
                .whereArrayContains(field, it)
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .limit(100)
                .addSnapshotListener { querySnapshot, ex ->
                    if (ex != null) {
                        Log.w(TAG, "listen:error", ex)
                        return@addSnapshotListener
                    }
                    Log.d(TAG, "fetch searched ${querySnapshot!!.documents.size}")
                    allPalettes.value = querySnapshot.documents.mapNotNull {
                        it.toObject(Palette::class.java)
                    }
                }

        }
        return
    }


    fun observeLivePalettes(): LiveData<List<Palette>> {
        return allPalettes
    }

}