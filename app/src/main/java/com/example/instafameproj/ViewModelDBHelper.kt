package com.example.instafameproj

import android.util.Log
import com.example.instafameproj.ui.userprofile.SortInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.example.instafameproj.ui.userprofile.UserMeta
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject

class ViewModelDBHelper {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val rootCollection = "Users"

    fun createUserMeta(
        userMeta: UserMeta,
        resultListener: (UserMeta)->Unit
    ) {
        var a = listOf( ("ownerUid"), ("uuid"))
        db.collection(rootCollection).document(userMeta.uuid)
            .set(userMeta, SetOptions.mergeFields(a))
            .addOnSuccessListener {
                Log.d("user_added",
                    "user_create "  + userMeta.ownerName + " id: " + userMeta.firestoreID
                )
                fetchUserMeta(userMeta.uuid,resultListener)
            }
            .addOnFailureListener { e ->
                Log.d("user_added",
                    "user_failed "  + userMeta.ownerName + " id: " + userMeta.firestoreID
                )
                Log.w(javaClass.simpleName, "Error ", e)
            }
    }

    fun fetchUserMeta(
        uuid: String,
        resultListener: (UserMeta) -> Unit
    ) {

        var userRef = db.collection(rootCollection).document(uuid)
        userRef.get()
            .addOnSuccessListener {
                resultListener(it.toObject(UserMeta::class.java)!!)
            }
    }


    fun updateUserMetaQuotes(quotes: String,uid:String){
        val userRef = db.collection(rootCollection).document(uid)
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
        val userRef = db.collection(rootCollection).document(uid)
        Log.d("uid", uid.toString())
        userRef
            .update("ownerName", name)
            .addOnSuccessListener{
                Log.d("User_Update", "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e -> Log.w("User_Update", "Error updating document", e) }

    }
}