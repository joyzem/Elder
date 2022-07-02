package com.example.elder.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {

    var groupName: String by mutableStateOf("")
    private set

    fun onGroupNameChange(name: String) {
        groupName = name
    }

    fun getGroupList() {

    }

}