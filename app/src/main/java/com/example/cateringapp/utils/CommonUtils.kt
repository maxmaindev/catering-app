package com.example.cateringapp.utils

fun Int.makeTwoDigit(): String{
    if (this > 9)
        return this.toString()
    else
        return "0$this"
}