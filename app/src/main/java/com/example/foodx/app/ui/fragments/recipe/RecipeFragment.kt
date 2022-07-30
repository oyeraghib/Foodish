package com.example.foodx.app.ui.fragments.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodx.api.utils.Constants.Companion.API_KEY
import com.example.foodx.api.utils.NetworkResults
import com.example.foodx.app.adapters.FoodRecipeAdapter
import com.example.foodx.app.viewmodels.FoodRecipeViewModel
import com.example.foodx.databinding.FragmentRecipeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.set

@AndroidEntryPoint
class RecipeFragment : Fragment() {

    private var _binding: FragmentRecipeBinding? = null
    private val binding: FragmentRecipeBinding get() = _binding!!

    // Viewmodel instance
    private lateinit var viewModel: FoodRecipeViewModel

    // Adapter instance
    private val adapter by lazy { FoodRecipeAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRecipeBinding.inflate(layoutInflater)

        //Initialising ViewModel
        viewModel = ViewModelProvider(requireActivity())[FoodRecipeViewModel::class.java]

        //Starts the recycler View
        setUpRecyclerView()

        //Sets Data In Recycler View
        requestApiData()
        return binding.root
    }


    private fun requestApiData() {
        viewModel.getRecipes(applyQueries())

        viewModel.recipeResponse.observe(viewLifecycleOwner, Observer { results ->
            when (results) {
                is NetworkResults.Success -> {
                    hideShimmer()
                    results.data.let {
                        adapter.setData(it!!)
                    }
                }

                is NetworkResults.Error -> {
                    hideShimmer()
                    Toast.makeText(requireContext(), results.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }

                is NetworkResults.Loading -> {
                    showShimmer()
                }
            }
        })
    }

    fun setUpRecyclerView() {
        setAdapter()
        binding.rvRecipe.showShimmer()

        binding.rvRecipe.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun applyQueries(): HashMap<String, String> {

        val queries: HashMap<String, String> = HashMap()
        queries["number"] = "50"
        queries["apiKey"] = API_KEY
        queries["type"] = "snack"
        queries["diet"] = "vegan"
        queries["addRecipeInformation"] = "true"
        queries["fillIngredients"] = "true"

        return queries
    }

    private fun setAdapter() {
        binding.rvRecipe.adapter = adapter
    }

    fun showShimmer() {
        binding.rvRecipe.showShimmer()
    }

    fun hideShimmer() {
        binding.rvRecipe.hideShimmer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}