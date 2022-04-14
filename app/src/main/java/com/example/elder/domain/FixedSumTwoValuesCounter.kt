package com.example.elder.domain

import androidx.compose.ui.state.ToggleableState

/**
 * This class is used when there are two numbers that are
 * related with the following dependence:
 * const totalAmount = firstValue + secondValue
 * @param totalAmount Const of sum of firstValue and secondValue
 */
class FixedSumTwoValuesCounter(private var totalAmount: Int) {

    private var firstValue = totalAmount
    private var secondValue = 0

    fun getToggleableState(): ToggleableState {
        if (firstValue == 0) return ToggleableState.Off
        if (secondValue == 0) return ToggleableState.On
        return ToggleableState.Indeterminate
    }

    fun increaseFirstOrSecondValue(isFirst: Boolean) {
        if (isFirst) {
            firstValue++
            secondValue--
        } else {
            firstValue--
            secondValue++
        }
    }

    fun setValuesByFirstValue(firstValue: Int) {
        this.firstValue = firstValue
        secondValue = totalAmount.minus(firstValue)
    }
}