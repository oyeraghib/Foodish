package com.example.foodx.app.utils

class Constants {

    companion object {
        //API QUERY KEYS
        const val QUERY_NUMBER = "number"
        const val QUERY_API_KEY = "apiKey"
        const val QUERY_DIET = "diet"
        const val QUERY_TYPE = "type"
        const val QUERY_ADD_RECIPE_INFORMATION = "addRecipeInformation"
        const val QUERY_FILL_INGREDIENTS = "fillIngredients"

        //ROOM
        const val DATABASE_NAME = "recipe_database"
        const val TABLE_NAME = "recipe_table"

        //Bottom Sheet and Preferences
        const val DEFAULT_MEAL_TYPE = "main course"
        const val DEFAULT_DIET_TYPE = "gluten free"
        const val DEFAULT_RECIPE_QUERY_NUMBER = "50"

        //Preferences DataStore
        const val PREFERENCES_MEAL_TYPE = "mealType"
        const val PREFERENCES_MEAL_TYPE_ID = "mealTypeId"
        const val PREFERENCES_DIET_TYPE = "dietType"
        const val PREFERENCES_DIET_TYPE_ID = "dietTypeID"
        const val PREFERENCES_BACK_ONLINE = "backOnline"

        const val PREFERENCES_NAME = "foodishPreferences"
    }
}