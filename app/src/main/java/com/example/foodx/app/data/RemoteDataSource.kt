package com.example.foodx.app.data

import com.example.foodx.api.models.responses.FoodRecipeResponse
import com.example.foodx.api.service.FoodRecipeAPI
import retrofit2.Response
import javax.inject.Inject


class RemoteDataSource @Inject constructor(private val foodRecipeAPI: FoodRecipeAPI) {

    suspend fun getRecipes(queries: Map<String, String>): Response<FoodRecipeResponse> {
        return foodRecipeAPI.getRecipes(queries = queries)
    }
}