package com.jm.calendar.ext

fun Int.formatToTwoDigits(): String {
    return String.format("%02d", this)
}