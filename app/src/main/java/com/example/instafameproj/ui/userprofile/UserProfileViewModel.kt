package com.example.instafameproj.ui.userprofile

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.instafameproj.R
import com.example.instafameproj.User
import com.example.instafameproj.ViewModelDBHelper
import com.example.instafameproj.invalidUser
import com.example.instafameproj.ui.Model.UserModel
import com.example.instafameproj.ui.Model.VideoModel
import com.example.instafameproj.ui.dashboard.Storage
import com.google.firebase.Timestamp


class UserProfileViewModel : ViewModel() {

    private var CurrentAuthUser = invalidUser
    private var userName = MutableLiveData<String>()
    private var Quotes = MutableLiveData<String>()
    private val dbHelp = ViewModelDBHelper()
    private var currentUser = MutableLiveData<UserModel>()
    private var videoList = mutableListOf<VideoModel>()
    private var videoModelList = mutableListOf<VideoModel>()
    private val storage = Storage()


    private fun createUserMeta(name: String, email: String, uuid : String) {
        var userModel = UserModel(
            ownerName = name,
            uuid = uuid,
            email = email,
        )
        dbHelp.createUserMeta(userModel) {
            this.currentUser.postValue(it)
        }

    }
    fun setCurrentAuthUser(user: User) {
        CurrentAuthUser = user
    }
    fun getCurrentAuthUser(): User{
        return CurrentAuthUser
    }

    fun setUserName(name: String){
        userName.postValue(name)
    }
    fun setProfilePic(pic: String, view:ImageView){
       // UserName.postValue(name)
        Glide.with(view).load(pic)
            .circleCrop()
            .apply(
                RequestOptions().placeholder(R.drawable.icon_profile)
            )
            .into(view)
    }
    fun setQuotes(quotes: String){
        Quotes.postValue(quotes)
    }

    fun observeUserName(): LiveData<String>{
        return userName
    }

    fun observeQuotes(): LiveData<String>{
        return Quotes
    }

    fun addUser(name: String, email: String, uid: String){
        createUserMeta(name,email,uid)
    }

    fun updateCurrentUserQuote(quotes:String){

        currentUser.value?.quotes = quotes
        var uid = currentUser.value?.uuid
        dbHelp.updateUserMetaQuotes(quotes,uid.toString())
    }

    fun updateCurrentUserName(name:String){
        currentUser.value?.ownerName = name
        var uid = currentUser.value?.uuid
        dbHelp.updateUserMetaName(name,uid.toString())
    }

    fun observeUserMeta(): LiveData<UserModel>{
        Log.d("userMeta", "updated")
        return currentUser
    }

    fun getUserMeta(): UserModel {
        return currentUser.value!!
    }

    fun setUserMeta(user: UserModel){
        currentUser.postValue(user)
    }

    fun uploadVideos(uri: Uri, uuid:String){
        val videoModel = VideoModel(
            videoId =  uuid + "_" +Timestamp.now().seconds.toString(),
            title = "",
            url = "",
            uuid = uuid,
            createdTime =  Timestamp.now())


        storage.uploadVideoStorage(uri,videoModel
        ) {
           dbHelp.fetchUserMeta(uuid) {
               Log.d("set_user_data", "got data")
               setUserMeta(it)
           }
        }

    }

    fun fetchVideos( uuid:String,resultListener: (List<VideoModel>)->Unit){

        storage.getVideos(uuid){

            for( ref in it){
                ref.downloadUrl.addOnSuccessListener {
                    var video = VideoModel(
                        videoId = ref.name,
                        url = it.toString()
                    )
                    videoList.add(video)
                    resultListener(videoList)
                }

            }
        }
    }
    fun uploadPics(uri: Uri, uuid:String){
        storage.uploadProfilePicsStorage(uri,uuid
        ) {
            dbHelp.fetchUserMeta(uuid) {
                Log.d("set_user_data", "got data")
                setUserMeta(it)
            }
        }

    }
    fun uploadSuccess(sizeBytes: Long){
        Log.d("upload_Success", sizeBytes.toString())

    }

}