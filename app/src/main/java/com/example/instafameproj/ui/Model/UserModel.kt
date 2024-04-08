package com.example.instafameproj.ui.Model

// Firebase insists we have a no argument constructor
data class UserModel(
    // Auth information
    var userName: String = "",
    var email : String ="",
    var uuid: String = "",
    var quotes: String = "",
    var profilePic : String = "",
    var followerList : MutableList<String> = mutableListOf(),
    var followingList : MutableList<String> = mutableListOf(),
    var videoUrl: List<String> = emptyList(),
)
