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
import com.example.foodx.app.viewmodels.MainViewModel
import com.example.foodx.app.viewmodels.RecipesViewModel
import com.example.foodx.databinding.FragmentRecipeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.set

@AndroidEntryPoint
class RecipeFragment : Fragment() {

    private var _binding: FragmentRecipeBinding? = null
    private val binding: FragmentRecipeBinding get() = _binding!!

    // ViewModel instance
    private lateinit var viewModel: MainViewModel

    //Recipes ViewModel
    private lateinit var recipesViewModel: RecipesViewModel

    // Adapter instance
    private val adapter by lazy { FoodRecipeAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRecipeBinding.inflate(layoutInflater)

        //Initialising ViewModel
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        //Initialising Recipes ViewModel
        recipesViewModel = ViewModelProvider(this)[RecipesViewModel::class.java]

        //Starts the recycler View
        setUpRecyclerView()

        //Sets Data In Recycler View
        requestApiData()
        return binding.root
    }


    private fun requestApiData() {
        viewModel.getRecipes(recipesViewModel.applyQueries())

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

    private fun setUpRecyclerView() {
        setAdapter()
        binding.rvRecipe.showShimmer()

        binding.rvRecipe.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setAdapter() {
        binding.rvRecipe.adapter = adapter
    }

    private fun showShimmer() {
        binding.rvRecipe.showShimmer()
    }

    private fun hideShimmer() {
        binding.rvRecipe.hideShimmer()
        binding.ivNoInternet.visibility = View.VISIBLE
        binding.tvNoInternet.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}