package com.example.foodx.app.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodx.api.utils.Constants.Companion.API_KEY
import com.example.foodx.app.data.DataStoreRepository
import com.example.foodx.app.utils.Constants.Companion.DEFAULT_DIET_TYPE
import com.example.foodx.app.utils.Constants.Companion.DEFAULT_MEAL_TYPE
import com.example.foodx.app.utils.Constants.Companion.DEFAULT_RECIPE_QUERY_NUMBER
import com.example.foodx.app.utils.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.example.foodx.app.utils.Constants.Companion.QUERY_API_KEY
import com.example.foodx.app.utils.Constants.Companion.QUERY_DIET
import com.example.foodx.app.utils.Constants.Companion.QUERY_FILL_INGREDIENTS
import com.example.foodx.app.utils.Constants.Companion.QUERY_NUMBER
import com.example.foodx.app.utils.Constants.Companion.QUERY_TYPE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    application: Application,
    private val dataStoreRepository: DataStoreRepository
) : AndroidViewModel(application) {

    //variable for meal type
    private var mealType: String = DEFAULT_MEAL_TYPE
    private var dietType: String = DEFAULT_DIET_TYPE

    val readMealAndDietType = dataStoreRepository.readMealAndDietType

    fun saveMealAndDietType(mealType: String, mealTypeId: Int, dietType: String, dietTypeId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveMealAndDietType(
                mealType,
                mealTypeId,
                dietType,
                dietTypeId,
                getApplication()
            )
        }

    fun applyQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()

        viewModelScope.launch {
            readMealAndDietType.collect {
                mealType = it.selectedMealType
                dietType = it.selectedDietType
            }
        }
        queries[QUERY_NUMBER] = DEFAULT_RECIPE_QUERY_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_TYPE] = mealType
        queries[QUERY_DIET] = dietType
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"

        return queries
    }
}
