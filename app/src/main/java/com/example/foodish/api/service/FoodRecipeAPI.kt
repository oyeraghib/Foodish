package com.example.foodish.api.service

import com.example.foodish.api.models.responses.FoodRecipeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface FoodRecipeAPI {

    @GET("recipes/complexSearch")
    suspend fun getRecipes(
        @QueryMap queries: Map<String, String>
    ): Response<FoodRecipeResponse>

    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @QueryMap searchQueries: Map<String, String>
    ): Response<FoodRecipeResponse>
}