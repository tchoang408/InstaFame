package com.example.instafameproj.ui.Model

import com.google.firebase.Timestamp

// Firebase insists we have a no argument constructor
data class VideoModel(
    // Auth information
    var videoId : String = "",
    var title : String = "",
    var url: String = "",
    var uuid : String = "",
    val createdTime: Timestamp = Timestamp.now(),
)
