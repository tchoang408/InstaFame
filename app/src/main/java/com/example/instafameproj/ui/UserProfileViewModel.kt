package com.example.instafameproj.ui

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.instafameproj.R
import com.example.instafameproj.ui.Model.UserModel
import com.example.instafameproj.ui.Model.VideoModel
import com.google.firebase.Timestamp


class UserProfileViewModel : ViewModel() {

    private var currentAuthUser = MutableLiveData<User>()
    private var userName = MutableLiveData<String>()
    private var quotes = MutableLiveData<String>()
    private val dbHelp = ViewModelDBHelper()
    private var currentUser = MutableLiveData<UserModel>()
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
        currentAuthUser.value = user
    }
    fun getCurrentAuthUser(): User? {
        return currentAuthUser.value
    }

    fun observeAuthUser(): LiveData<User>{
       return currentAuthUser
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
        this.quotes.postValue(quotes)
    }

    fun observeUserName(): LiveData<String>{
        return userName
    }

    fun observeQuotes(): LiveData<String>{
        return quotes
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

    fun uploadVideos(uri: Uri, uuid:String, title:String,resultListener: (Boolean)->Unit ){
        val videoModel = VideoModel(
            videoId =  uuid + "_" +Timestamp.now().seconds.toString(),
            title = title,
            url = "",
            uuid = uuid,
            createdTime =  Timestamp.now())


        storage.uploadVideoStorage(uri,videoModel
        ) {
           dbHelp.fetchUserMeta(uuid) {
               Log.d("set_user_data", "got data")
               setUserMeta(it)
               resultListener(true)
           }
        }

    }
    fun fetchUserMeta(resultListener: (List<VideoModel>)->Unit){
        currentUser.value?.let {
            dbHelp.fetchUserMeta(it.uuid){
                currentUser.postValue(it)
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
    fun deleteVideosMeta(videoId: String, uuid: String, url: String, resultListener: (Boolean)->Unit){


        dbHelp.deleteUserVideoMeta(url,uuid,videoId){
            if(it) {
                currentUser.value?.videoUrl?.remove(url)
                currentUser.postValue(currentUser.value)

                dbHelp.deleteVideoVideoId(videoId){

                }


                storage.deleteVideo(uuid,videoId){

                }
            }
        }


    }
    fun addUserFollowing(followingUid:String, resultListener: () -> Unit) {
       dbHelp.addUserFollowing(followingUid, currentUser.value?.uuid!!) {

           var userModel = currentUser.value
           userModel?.followingList!!.add(followingUid)
           currentUser.postValue(userModel!!)

       }

        dbHelp.addUserFollower(followingUid, currentUser.value?.uuid!!) {

        }
    }

    fun removeUserFollower(followerUid:String, resultListener: () -> Unit) {
        dbHelp.removeUserFollower(followerUid, currentUser.value?.uuid!!) {

            var userModel = currentUser.value
            userModel?.followerList!!.remove(followerUid)
            currentUser.postValue(userModel!!)
        }
    }

    fun addUserLikes(videoId:String, videoUid: String,resultListener: () -> Unit) {
        dbHelp.addUserLikes(videoId, currentUser.value?.uuid!!) {
            var userModel = currentUser.value
            userModel?.likesList!!.add(videoId)
            currentUser.postValue(userModel!!)

        }

        dbHelp.addUserLikesCount(videoUid){
            var userModel = currentUser.value
            userModel?.likesCount?.inc()
            currentUser.postValue(userModel!!)
        }
    }

    fun removeUserLikes(videoId:String,videoUid: String, resultListener: () -> Unit) {
        dbHelp.removeUserLikes(videoUid, currentUser.value?.uuid!!) {

            var userModel = currentUser.value
            userModel?.likesList!!.remove(videoId)
            currentUser.postValue(userModel!!)
        }


        dbHelp.removeUserLikesCount(videoUid){
            var userModel = currentUser.value
            userModel?.likesCount?.dec()
            currentUser.postValue(userModel!!)
        }
    }

}