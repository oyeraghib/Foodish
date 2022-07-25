package com.example.foodx.app.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import com.example.foodx.app.repo.FoodRecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FoodRecipeViewModel @Inject constructor(
    private val repository: FoodRecipeRepository,
    application: Application
) : AndroidViewModel(application) {

    fun checkInternet(): Boolean {
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

    val repo = repository
}