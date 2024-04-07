package com.example.instafameproj.ui

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

// Firebase insists we have a no argument constructor
data class UserMeta(
    // Auth information
    var ownerName: String = "",
    var ownerUid: String = "",
    var uuid : String = "",
    var quotes: String = "",
    // Written on the server
    @ServerTimestamp val timeStamp: Timestamp? = null,
    // firestoreID is generated by firestore, used as primary key
    @DocumentId var firestoreID: String = ""
)
