package com.example.foodish.app.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.*
import com.example.foodish.api.models.responses.FoodRecipeResponse
import com.example.foodish.api.utils.NetworkResults
import com.example.foodish.app.database.entity.RecipesEntity
import com.example.foodish.app.repo.FoodRecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: FoodRecipeRepository,
    application: Application
) : AndroidViewModel(application) {

    /** ROOM DATABASE */

    val readRecipes: LiveData<List<RecipesEntity>> = repository.local.readDatabase().asLiveData()

    private fun insertRecipes(recipesEntity: RecipesEntity) = viewModelScope.launch(Dispatchers.Main) {
        repository.local.insertDao(recipesEntity)
    }

    /** REMOTE DATABASE */
    var recipeResponse: MutableLiveData<NetworkResults<FoodRecipeResponse>> = MutableLiveData()
    var searchedRecipeResponse: MutableLiveData<NetworkResults<FoodRecipeResponse>> =
        MutableLiveData()

    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch(Dispatchers.Main) {
        getRecipesSafeCallQueries(queries)
    }

    fun searchRecipes(searchQuery: Map<String, String>) = viewModelScope.launch(Dispatchers.Main) {
        searchRecipesSafeCall(searchQuery)
    }

    private suspend fun getRecipesSafeCallQueries(queries: Map<String, String>) {

        // When data is loading but not yet fetched.
        recipeResponse.value = NetworkResults.Loading()

         //Checks for Internet and performs API fetching
        if (hasInternet()) {
            try {
                val response = repository.remote.getRecipes(queries)
                recipeResponse.value = handleFoodRecipesResponse(response)

                //local caching
                val foodRecipe = recipeResponse.value!!.data
                if(foodRecipe != null) {
                    offlineCacheRecipe(foodRecipe)
                }
            } catch (e: Exception) {
                recipeResponse.value =
                    NetworkResults.Error(message = "Recipes not found", data = null)
                Timber.d("No recipes found. Exception: $e")
            }
        } else
            recipeResponse.value =
                NetworkResults.Error(message = "No Internet Connection", data = null)
    }

    private suspend fun searchRecipesSafeCall(searchQuery: Map<String, String>) {

        // When data is loading but not yet fetched.
        searchedRecipeResponse.value = NetworkResults.Loading()

        //Checks for Internet and performs API fetching
        if (hasInternet()) {
            try {
                val response = repository.remote.searchRecipes(searchQuery)
                searchedRecipeResponse.value = handleFoodRecipesResponse(response)
            } catch (e: Exception) {
                searchedRecipeResponse.value =
                    NetworkResults.Error(message = "Recipes not found", data = null)
                Timber.d("No recipes found. Exception: $e")
            }
        } else
            searchedRecipeResponse.value =
                NetworkResults.Error(message = "No Internet Connection", data = null)
    }

    private fun offlineCacheRecipe(foodRecipe: FoodRecipeResponse) {
        val recipesEntity = RecipesEntity(foodRecipe)
        insertRecipes(recipesEntity)
    }

    // Handles the different scenarios of the data coming from API
    private fun handleFoodRecipesResponse(response: Response<FoodRecipeResponse>): NetworkResults<FoodRecipeResponse> {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResults.Error(message = "Timeout", data = null)
            }

            response.code() == 402 -> {
                return NetworkResults.Error(message = "API Key is limited", data = null)
            }

            response.body()!!.results.isNullOrEmpty() -> {
                return NetworkResults.Error(message = "Recipe not found", data = null)
            }

            response.isSuccessful -> {
                val foodRecipes = response.body()
                return NetworkResults.Success(data = foodRecipes!!)
            }
            else -> {
                return NetworkResults.Error(data = null, message = "Message")
            }
        }
    }

    // Checks for Internet Connection availability from different sources like WIFI, Cellular and ethernet
    //TODO: This functionality is not working correctly at the moment [every time it is made to return true]
    private fun hasInternet(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_FOTA) -> true
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_SUPL) -> true
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_MMS) -> true

            else -> true
        }
    }
}