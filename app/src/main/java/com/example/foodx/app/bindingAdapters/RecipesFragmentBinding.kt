package com.example.foodx.app.bindingAdapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.foodx.api.models.responses.FoodRecipeResponse
import com.example.foodx.api.utils.NetworkResults
import com.example.foodx.app.database.entity.RecipesEntity

class RecipesFragmentBinding {

    companion object {

        @BindingAdapter("apiResponse", "databaseResponse", requireAll = true)
        @JvmStatic
        fun errorImageViewVisibility(
            imageView: ImageView,
            apiResponse: NetworkResults<FoodRecipeResponse>?,
            database: List<RecipesEntity>?
        ) {
            if (apiResponse is NetworkResults.Error && database.isNullOrEmpty()) {
                imageView.visibility = View.VISIBLE
            } else if (apiResponse is NetworkResults.Loading) {
                imageView.visibility = View.INVISIBLE
            } else if (apiResponse is NetworkResults.Success) {
                imageView.visibility = View.INVISIBLE
            }
        }

        @BindingAdapter("apiResponse2", "databaseResponse2", requireAll = true)
        @JvmStatic
        fun errorTextViewVisibility(
            textView: TextView,
            apiResponse: NetworkResults<FoodRecipeResponse>?,
            database: List<RecipesEntity>?
        ) {
            if(apiResponse is NetworkResults.Error && database.isNullOrEmpty()) {
                textView.visibility = View.VISIBLE
                textView.text = apiResponse.message.toString()
            } else if(apiResponse is NetworkResults.Loading) {
                textView.visibility = View.INVISIBLE
            } else if(apiResponse is NetworkResults.Success) {
                textView.visibility = View.INVISIBLE
            }
        }
    }
}