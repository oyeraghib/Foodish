package com.example.foodish.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.foodish.api.models.data.Result
import com.example.foodish.api.models.responses.FoodRecipeResponse
import com.example.foodish.app.utils.FoodRecipeDiffUtil
import com.example.foodish.databinding.ListItemRecipeBinding

class FoodRecipeAdapter : RecyclerView.Adapter<FoodRecipeAdapter.MyViewHolder>() {

    private var recipe = emptyList<Result>()

    class MyViewHolder(private val binding: ListItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(result: Result) {
            binding.result = result
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemRecipeBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentRecipe = recipe[position]
        holder.bind(currentRecipe)
    }

    override fun getItemCount(): Int {
        return recipe.size
    }

    fun setData(newData: FoodRecipeResponse) {
        val recipesDiffUtil = FoodRecipeDiffUtil(recipe, newData.results!!)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)

        //Setting only new data in the list
        recipe = newData.results

        diffUtilResult.dispatchUpdatesTo(this)
    }
}