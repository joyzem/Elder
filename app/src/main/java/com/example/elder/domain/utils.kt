package com.example.elder.domain

fun convertFullNameListToSurnameList(list: List<String>): List<String> {
    val surnameList = list.map { fullName ->
        fullName.split(" ")[0]
    }
    return surnameList
}