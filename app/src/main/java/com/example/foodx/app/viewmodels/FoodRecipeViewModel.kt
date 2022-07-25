package com.example.foodx.app.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.foodx.api.models.responses.FoodRecipeResponse
import com.example.foodx.api.utils.NetworkResults
import com.example.foodx.app.repo.FoodRecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FoodRecipeViewModel @Inject constructor(
    private val repository: FoodRecipeRepository,
    application: Application
) : AndroidViewModel(application) {

    val recipeResponse: MutableLiveData<NetworkResults<FoodRecipeResponse>> = MutableLiveData()

    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRecipesSafeCallQueries(queries)
    }

    private suspend fun getRecipesSafeCallQueries(queries: Map<String, String>) {

        // When data is loading but not yet fetched.
        recipeResponse.value = NetworkResults.Loading()

        // Checks for Internet and performs API fetching
        if (hasInternet()) {
            try {
                val response = repository.remote.getRecipes(queries)
                recipeResponse.value = handleFoodRecipesResponse(response)
            } catch (e: Exception) {
                recipeResponse.value =
                    NetworkResults.Error(message = "Recipes not found", data = null)
                Timber.d("No recipes found. Exception: $e")
            }
        } else
            recipeResponse.value =
                NetworkResults.Error(message = "No Internet Connection", data = null)
    }

    // Handles the different scenarios of the data comes from API
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
    private fun hasInternet(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_SUPL) -> true
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_FOTA) -> true
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_MMS) -> true

            else -> false
        }
    }
}