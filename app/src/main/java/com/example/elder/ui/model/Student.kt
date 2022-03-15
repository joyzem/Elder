package com.example.elder.ui.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class Student(
    val name: String,
    var checked: MutableState<Boolean> = mutableStateOf(false)
)
