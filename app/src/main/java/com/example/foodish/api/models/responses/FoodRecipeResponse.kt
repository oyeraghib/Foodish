package com.example.foodish.api.models.responses


import com.example.foodish.api.models.data.Result
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FoodRecipeResponse(
    @Json(name = "results")
    val results: List<Result>?,
)