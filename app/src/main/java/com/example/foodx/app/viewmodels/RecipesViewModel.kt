package com.example.foodx.app.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
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
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    application: Application,
    private val dataStoreRepository: DataStoreRepository
) : AndroidViewModel(application) {

    //variable for meal & diet type
    private var mealType: String = DEFAULT_MEAL_TYPE
    private var dietType: String = DEFAULT_DIET_TYPE

    //Check network status
    var networkStatus: Boolean = false

    var backOnline = false

    //variable for reading meal and diet type from data store
    val readMealAndDietType = dataStoreRepository.readMealAndDietType

    //Getting information of if Internet is back
    val readBackOnline = dataStoreRepository.readBackOnline.asLiveData()

    /* TODO: Find a better approach to provide context as it may lead to memory leaks */

    //Context to be passed in DataStore.
    val context = getApplication<Application>().applicationContext!!

    /**
     * Function for saving meal type, meal type id, diet type and diet type id.
     */
    fun saveMealAndDietType(mealType: String, mealTypeId: Int, dietType: String, dietTypeId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveMealAndDietType(
                mealType,
                mealTypeId,
                dietType,
                dietTypeId,
                context = context
            )
        }

    //Saving back online (when internet is back)
    fun saveBackOnline(backOnline: Boolean) {
        viewModelScope.launch {
            dataStoreRepository.saveBackOnline(backOnline)
        }
    }

    fun applyQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()

        viewModelScope.launch {
            readMealAndDietType.collect {
                mealType = it.selectedMealType
                dietType = it.selectedDietType
            }
        }

        Timber.d("Meal Type: $mealType, Diet Type: $dietType")
        queries[QUERY_NUMBER] = DEFAULT_RECIPE_QUERY_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_TYPE] = mealType
        queries[QUERY_DIET] = dietType
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"

        return queries
    }

    fun getNetworkStatus() {
        if(!networkStatus) {
            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
            saveBackOnline(true)
        } else if(networkStatus) {
            if(backOnline) {
                Toast.makeText(context, "We are back again", Toast.LENGTH_SHORT).show()
                saveBackOnline(false)
            }
        }

    }
}
