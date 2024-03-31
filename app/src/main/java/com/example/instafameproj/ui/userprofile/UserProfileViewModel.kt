package com.example.instafameproj.ui.userprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class UserProfileViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is User Profile Fragment"
    }

    // LiveData objects to hold user data
    private val _userEmail = MutableLiveData<String>()
    private val _userId = MutableLiveData<String>()

    val text: LiveData<String> = _text

    // Expose LiveData as read-only properties
    val userEmail: LiveData<String>
        get() = _userEmail
    val userId: LiveData<String>
        get() = _userId

    // Function to update user data (call from your activity)
    fun updateUserData(email: String, userId: String) {
        _userEmail.value = email
        _userId.value = userId
    }

}