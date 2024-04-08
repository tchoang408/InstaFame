package com.example.instafameproj.ui.userprofile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instafameproj.User
import com.example.instafameproj.VideoMeta
import com.example.instafameproj.ViewModelDBHelper
import com.example.instafameproj.invalidUser
import com.example.instafameproj.ui.dashboard.Storage
import com.google.firebase.Timestamp
import com.google.firebase.storage.StorageReference


enum class SortColumn {
    TITLE,
    SIZE
}
data class SortInfo(val sortColumn: SortColumn, val ascending: Boolean)
class UserProfileViewModel : ViewModel() {

    private var CurrentAuthUser = invalidUser
    private var UserName = MutableLiveData<String>()
    private var Quotes = MutableLiveData<String>()
    private val dbHelp = ViewModelDBHelper()
    private var userMetaLocal = MutableLiveData<UserMeta>()
    private var videoMeta = MutableLiveData<List<VideoMeta>>()
    private var videoList = mutableListOf<VideoMeta>()
    private val storage = Storage()


    private var sortInfo = MutableLiveData(
        SortInfo(SortColumn.TITLE, true))

    // Function to update user data (call from your activity)

    private fun createUserMeta(name: String, email: String, uuid : String) {
        val currentUser = CurrentAuthUser
        var userMeta = UserMeta(
            ownerName = currentUser.name,
            ownerUid = currentUser.uid,
            uuid = uuid,
        )

        Log.d("user", currentUser.name)
        Log.d("user", currentUser.uid)
        Log.d("user", uuid)
        dbHelp.createUserMeta(userMeta) {
            userMetaLocal.postValue(it)
        }

    }
    fun setCurrentAuthUser(user: User) {
        CurrentAuthUser = user
    }
    fun getCurrentAuthUser(): User{
        return CurrentAuthUser
    }

    fun setUserName(name: String){
        UserName.postValue(name)
    }

    fun setQuotes(quotes: String){
        Quotes.postValue(quotes)
    }

    fun observeUserName(): LiveData<String>{
        return UserName
    }

    fun observeQuotes(): LiveData<String>{
        return Quotes
    }

    fun addUser(name: String, email: String, uid: String){
        createUserMeta(name,email,uid)
    }

    fun updateUserMetaQuote(quotes:String){
        userMetaLocal.value?.quotes = quotes
        var uid = userMetaLocal.value?.ownerUid
        dbHelp.updateUserMetaQuotes(quotes,uid.toString())
    }

    fun updateUserMetaUserName(name:String){
        userMetaLocal.value?.ownerName = name
        var uid = userMetaLocal.value?.ownerUid
        dbHelp.updateUserMetaName(name,uid.toString())
    }

    fun observeUserMeta(): LiveData<UserMeta>{
        return userMetaLocal
    }

    fun getUserMeta(): UserMeta{
        return userMetaLocal.value!!
    }

    fun setUserMeta(user: UserMeta){
        userMetaLocal.postValue(user)
    }

    fun uploadVideos(uri: Uri){
           storage.uploadVideoStorage(uri, userMetaLocal.value?.uuid!!,
               Timestamp.now().seconds.toString()
           ) {
               dbHelp.fetchUserMeta(userMetaLocal.value!!.uuid) {
                   setUserMeta(it)
               }
        }
    }

    fun getVideoList(): List<VideoMeta>{
        return videoList
    }

    fun fetchVideos( resultListener: (List<VideoMeta>)->Unit){

        storage.getVideos(userMetaLocal.value?.uuid!!){

            for( ref in it){
                ref.downloadUrl.addOnSuccessListener {
                    var video = VideoMeta(
                        videoId = ref.name,
                        url = it.toString()
                    )
                    videoList.add(video)
                    resultListener(videoList)
                }

            }
        }
    }

    fun uploadSuccess(sizeBytes: Long){
        Log.d("upload_Success", sizeBytes.toString())

    }

}