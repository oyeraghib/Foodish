package com.example.foodx.api.models.data


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExtendedIngredient(
    @Json(name = "amount")
    val amount: Double?,
    @Json(name = "consistency")
    val consistency: String?,
    @Json(name = "image")
    val image: String?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "original")
    val original: String?,
    @Json(name = "unit")
    val unit: String?
)