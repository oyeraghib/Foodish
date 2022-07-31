package com.example.foodx.app.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foodx.api.models.responses.FoodRecipeResponse
import com.example.foodx.app.utils.Constants.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
class RecipesEntity(
    var foodRecipe: FoodRecipeResponse
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}