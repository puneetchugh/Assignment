package com.example.assignment.data.model

import com.google.gson.annotations.SerializedName

data class Language(
    val code: String,
    @SerializedName("iso639_2") val iso639Two: String,
    val name: String,
    val nativeName: String
)