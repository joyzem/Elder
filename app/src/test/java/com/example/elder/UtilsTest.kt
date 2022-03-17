package com.example.elder

import com.example.elder.domain.convertFullNameListToSurnameList
import org.junit.Test

class UtilsTest {
    @Test
     fun convertFullNamesListToSurnamesList() {
        val fullNameList = listOf("Винниченко Илья Николаевич", "Болотов Егор", "Газизов Родион")
        val surnamesList = convertFullNameListToSurnameList(fullNameList)
        surnamesList.forEach {
            assert(!it.contains(" "))
        }
    }
}