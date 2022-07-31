package com.example.foodx.app.database

import androidx.room.TypeConverter
import com.example.foodx.api.models.responses.FoodRecipeResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.moshi.Moshi

class RecipesTypeConverter {

    var gson = Gson()

    @TypeConverter
    fun foodRecipeToString(foodRecipeResponse: FoodRecipeResponse): String {
        return gson.toJson(foodRecipeResponse)
    }

    @TypeConverter
    fun stringToFoodRecipe(data: String): FoodRecipeResponse {
        val listType = object: TypeToken<FoodRecipeResponse>() {}.type
        return gson.fromJson(data, listType)
    }
}