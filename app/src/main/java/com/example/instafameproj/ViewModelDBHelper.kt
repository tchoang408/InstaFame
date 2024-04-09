package com.example.instafameproj

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.example.instafameproj.ui.Model.UserModel
import com.example.instafameproj.ui.Model.VideoModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions

class ViewModelDBHelper {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userRootCollection = "Users"
    private val videoRootCollection = "Videos"

    fun createUserMeta(
        userModel: UserModel,
        resultListener: (UserModel)->Unit
    ) {
        var a = listOf(("uuid"), ("email"))
        db.collection(userRootCollection).document(userModel.uuid)
            .set(userModel, SetOptions.mergeFields(a))
            .addOnSuccessListener {

                fetchUserMeta(userModel.uuid,resultListener)
            }
            .addOnFailureListener { e ->

                Log.w(javaClass.simpleName, "Error ", e)
            }
    }

    fun fetchUserMeta(
        uuid: String,
        resultListener: (UserModel) -> Unit
    ) {

        var userRef = db.collection(userRootCollection).document(uuid)
        userRef.get()
            .addOnSuccessListener {
                resultListener(it.toObject(UserModel::class.java)!!)
            }
    }


    fun updateUserMetaQuotes(quotes: String,uid:String){
        val userRef = db.collection(userRootCollection).document(uid)
        Log.d("uid", uid.toString())
        // Set the "isCapital" field of the city 'DC'
        userRef
            .update("quotes", quotes)
            .addOnSuccessListener{
                Log.d("User_Update", "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e -> Log.w("User_Update", "Error updating document", e) }

    }

    fun updateUserMetaName(name: String,uid:String){
        val userRef = db.collection(userRootCollection).document(uid)
        Log.d("uid", uid.toString())
        userRef
            .update("ownerName", name)
            .addOnSuccessListener{
                Log.d("User_Update", "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e -> Log.w("User_Update", "Error updating document", e) }

    }
    fun updateUserVideoUrl(url: String,uid:String, resultListener: () -> Unit){
        val userRef = db.collection(userRootCollection).document(uid)
        Log.d("videoUrl", uid.toString())
        userRef
            .update("videoUrl", FieldValue.arrayUnion(url))
            .addOnSuccessListener{
                Log.d("User_Update", "DocumentSnapshot successfully updated!")
                resultListener()
            }
            .addOnFailureListener { e -> Log.w("User_Update", "Error updating document", e) }

    }

    fun updateUserPicsUrl(url: String,uid:String, resultListener: () -> Unit) {
        val userRef = db.collection(userRootCollection).document(uid)
        Log.d("videoUrl", uid.toString())
        userRef
            .update("profilePic", url)
            .addOnSuccessListener {
                Log.d("User_Update", "DocumentSnapshot successfully updated!")
                resultListener()
            }
            .addOnFailureListener { e -> Log.w("User_Update", "Error updating document", e) }

    }
        fun fetchVideoMeta(
        uuid: String,
        resultListener: (VideoModel) -> Unit
    ) {

        var userRef = db.collection(videoRootCollection).document(uuid)
        userRef.get()
            .addOnSuccessListener {
                resultListener(it.toObject(VideoModel::class.java)!!)
            }
    }
    fun createVideoMeta(
        videoModel: VideoModel,
        resultListener: (VideoModel)->Unit
    ) {
        var a = listOf(("uuid"))

        db.collection(videoRootCollection).document(videoModel.videoId)
            .set(videoModel)
            .addOnSuccessListener {
                fetchVideoMeta(videoModel.videoId,resultListener)
            }
            .addOnFailureListener { e ->
                Log.w("Upload_video_model", "Error ", e)
            }
    }
}