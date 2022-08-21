package com.example.foodish.app.ui.fragments.recipe.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.example.foodish.app.utils.Constants.Companion.DEFAULT_DIET_TYPE
import com.example.foodish.app.utils.Constants.Companion.DEFAULT_MEAL_TYPE
import com.example.foodish.app.viewmodels.RecipesViewModel
import com.example.foodish.databinding.FragmentRecipeBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import timber.log.Timber

class RecipeBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentRecipeBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var mealTypeChip = DEFAULT_MEAL_TYPE
    private var mealTypeChipId = 0
    private var dietTypeChip = DEFAULT_DIET_TYPE
    private var dietTypeChipId = 0

    //view model
    private lateinit var recipesViewModel: RecipesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
    }

    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRecipeBottomSheetBinding.inflate(layoutInflater)

        //Read stored values for {meal} and {diet} type for data store
        recipesViewModel.readMealAndDietType.asLiveData()
            .observe(viewLifecycleOwner, Observer { value ->
                mealTypeChip = value.selectedMealType
                dietTypeChip = value.selectedDietType

                updateChip(value.selectedMealTypeId, binding.layoutMealType.mealTypeChipGroup)
                updateChip(value.selectedDietTypeId, binding.layoutDietType.dietTypeChipGroup)
            })

        //On checking a new chip in meal type
        binding.layoutMealType.mealTypeChipGroup.setOnCheckedChangeListener { group, selectedChipId ->
            val chip = group.findViewById<Chip>(selectedChipId)
            val selectedMealType = chip.text.toString().lowercase()

            //Selected meal name and Id will be set to global variable
            mealTypeChip = selectedMealType
            mealTypeChipId = selectedChipId
        }

        //On checking a new chip in diet type
        binding.layoutDietType.dietTypeChipGroup.setOnCheckedChangeListener { group, selectedChipId ->
            val chip = group.findViewById<Chip>(selectedChipId)
            val selectedDietType = chip.text.toString().lowercase()

            //Selected diet name and Id will be set to global variable
            dietTypeChip = selectedDietType
            dietTypeChipId = selectedChipId
        }

        //Save {meal} and {diet} type to data store on pressing apply button
        binding.btnApply.setOnClickListener {
            recipesViewModel.saveMealAndDietType(
                mealTypeChip,
                mealTypeChipId,
                dietTypeChip,
                dietTypeChipId
            )

            val action = RecipeBottomSheetDirections.actionRecipeBottomSheetToRecipeFragment()
            action.backFromBottomSheet = true
            Timber.d("${action.backFromBottomSheet}")
            findNavController().navigate(action)
        }
        return binding.root
    }

    /**
     * After making any new changes in the chip group selection we update the chip
     * The initial value of {chipId = 0} when we first run the application.
     */
    private fun updateChip(chipId: Int, chipGroup: ChipGroup) {
        if (chipId != 0) {
            try {
                chipGroup.findViewById<Chip>(chipId).isChecked = true
            } catch (e: Exception) {
                Timber.e("Exception: ${e.message.toString()}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}