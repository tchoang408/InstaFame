package com.example.instafameproj.ui.userprofile

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.google.protobuf.Empty

// Firebase insists we have a no argument constructor
data class UserMeta(
    // Auth information
    var ownerName: String = "",
    var ownerUid: String = "",
    var uuid: String = "",
    var quotes: String = "",
    var videoUrl: List<String> = emptyList(),
    // Written on the server
    @ServerTimestamp val timeStamp: Timestamp? = null,
    // firestoreID is generated by firestore, used as primary key
    @DocumentId var firestoreID: String = ""
)