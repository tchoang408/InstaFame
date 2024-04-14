package com.example.instafameproj.ui.Model

// Firebase insists we have a no argument constructor
data class UserModel(
    // Auth information
    var ownerName: String = "",
    var email : String ="",
    var uuid: String = "",
    var quotes: String = "",
    var profilePic : String = "",
    var followerList : MutableList<String> = mutableListOf(),
    var followingList : MutableList<String> = mutableListOf(),
    var likesList : MutableList<String> = mutableListOf(),
    var likesCount : Long,
    var videoUrl: MutableList<String> = mutableListOf(),
    var videoId: MutableList<String> = mutableListOf(),

    )
