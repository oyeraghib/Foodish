package com.example.foodx.app.ui.fragments.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodx.api.utils.NetworkResults
import com.example.foodx.app.adapters.FoodRecipeAdapter
import com.example.foodx.app.utils.observeOnce
import com.example.foodx.app.viewmodels.MainViewModel
import com.example.foodx.app.viewmodels.RecipesViewModel
import com.example.foodx.databinding.FragmentRecipeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

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

        //Read Recipes from Database
        loadFoodRecipes()

        return binding.root
    }

    private fun setUpRecyclerView() {
        setAdapter()
        binding.rvRecipe.showShimmer()
        binding.rvRecipe.layoutManager = LinearLayoutManager(requireContext())
    }


    private fun loadFoodRecipes() {
        lifecycleScope.launch {
            viewModel.readRecipes.observeOnce(viewLifecycleOwner, Observer {
                if (it.isNotEmpty()) {
                    Timber.d("Read Database called")
                    adapter.setData(it[0].foodRecipe)
                    hideShimmerWhenDataReceived()
                } else {
                    //If database is empty requests new data from database   and store it locally.
                    requestApiData()
                }
            })
        }
    }

    private fun readFromCache() {
        lifecycleScope.launch {
            viewModel.readRecipes.observe(viewLifecycleOwner, Observer {
                if (it.isNotEmpty()) {
                    adapter.setData(it[0].foodRecipe)
                }
            })
        }
    }


    private fun requestApiData() {
        Timber.d("Request New Data From API")
        viewModel.getRecipes(recipesViewModel.applyQueries())

        viewModel.recipeResponse.observe(viewLifecycleOwner, Observer { results ->
            when (results) {
                is NetworkResults.Success -> {
                    hideShimmerWhenDataReceived()
                    results.data.let {
                        adapter.setData(it!!)
                    }
                }

                is NetworkResults.Error -> {
                    readFromCache()
                    hideShimmerWhenNoInternet()
                    Toast.makeText(requireContext(), results.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }

                is NetworkResults.Loading -> {
                    showShimmer()
                }
            }
        })
    }

    private fun setAdapter() {
        binding.rvRecipe.adapter = adapter
    }

    private fun showShimmer() {
        binding.rvRecipe.showShimmer()
    }

    private fun hideShimmerWhenDataReceived() {
        binding.rvRecipe.hideShimmer()
        binding.ivNoInternet.visibility = View.INVISIBLE
        binding.tvNoInternet.visibility = View.INVISIBLE
    }

    private fun hideShimmerWhenNoInternet() {
        binding.rvRecipe.hideShimmer()
        binding.ivNoInternet.visibility = View.VISIBLE
        binding.tvNoInternet.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}