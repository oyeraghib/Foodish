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
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodx.R
import com.example.foodx.api.utils.NetworkResults
import com.example.foodx.app.adapters.FoodRecipeAdapter
import com.example.foodx.app.utils.NetworkListener
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

    //Arguments from Recipes Bottom Sheet
    private val args by navArgs<RecipeFragmentArgs>()

    // ViewModel instance
    private lateinit var viewModel: MainViewModel

    //Recipes ViewModel
    private lateinit var recipesViewModel: RecipesViewModel

    // Adapter instance
    private val adapter by lazy { FoodRecipeAdapter() }

    //Network Listener instance
    private lateinit var networkListener: NetworkListener

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

        recipesViewModel.readBackOnline.observe(viewLifecycleOwner) {
            recipesViewModel.saveBackOnline(it)
        }

        //Kotlin coroutines {lifecycle} scope is required because collect is a suspend function
        lifecycleScope.launch {
            //initialising network listener
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext())
                .collect { status ->
                    Timber.d("$status")
                    recipesViewModel.networkStatus = status
                    recipesViewModel.getNetworkStatus()

                    //Read Recipes from Database
                    loadFoodRecipes()
                }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAddRecipe.setOnClickListener {
            if (recipesViewModel.networkStatus) {
                Navigation.findNavController(requireView())
                    .navigate(R.id.actionRecipeFragmentToRecipeBottomSheet)
            } else {
                recipesViewModel.getNetworkStatus()
            }
        }
    }

    private fun setUpRecyclerView() {
        setAdapter()
        binding.rvRecipe.showShimmer()
        binding.rvRecipe.layoutManager = LinearLayoutManager(requireContext())
    }


    private fun loadFoodRecipes() {
        lifecycleScope.launch {
            viewModel.readRecipes.observeOnce(viewLifecycleOwner, Observer {
                if (it.isNotEmpty() && !args.backFromBottomSheet) {
                    Timber.d("Read Database called")
                    adapter.setData(it[0].foodRecipe)
                    hideShimmer()
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
        Timber.d("${recipesViewModel.applyQueries()}")

        viewModel.recipeResponse.observe(viewLifecycleOwner, Observer { results ->
            when (results) {
                is NetworkResults.Success -> {
                    hideShimmer()
                    results.data.let {
                        adapter.setData(it!!)
                    }
                }

                is NetworkResults.Error -> {
                    readFromCache()
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

    private fun setAdapter() {
        binding.rvRecipe.adapter = adapter
    }

    private fun showShimmer() {
        binding.rvRecipe.showShimmer()
    }

    private fun hideShimmer() {
        binding.rvRecipe.hideShimmer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}