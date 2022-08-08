package com.example.foodx.app.ui.fragments.recipe.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.foodx.app.utils.Constants.Companion.DEFAULT_DIET_TYPE
import com.example.foodx.app.utils.Constants.Companion.DEFAULT_MEAL_TYPE
import com.example.foodx.app.viewmodels.RecipesViewModel
import com.example.foodx.databinding.FragmentRecipeBottomSheetBinding
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

        recipesViewModel.readMealAndDietType.asLiveData()
            .observe(viewLifecycleOwner, Observer { value ->
                mealTypeChip = value.selectedMealType
                dietTypeChip = value.selectedDietType

                updateChip(value.selectedMealTypeId, binding.layoutMealType.mealTypeChipGroup)
                updateChip(value.selectedDietTypeId, binding.layoutDietType.dietTypeChipGroup)
            })

        binding.layoutMealType.mealTypeChipGroup.setOnCheckedChangeListener { group, selectedChipId ->
            val chip = group.findViewById<Chip>(selectedChipId)
            val selectedMealType = chip.text.toString().lowercase()

            mealTypeChip = selectedMealType
            mealTypeChipId = selectedChipId
        }

        binding.layoutDietType.dietTypeChipGroup.setOnCheckedChangeListener { group, selectedChipId ->
            val chip = group.findViewById<Chip>(selectedChipId)
            val selectedDietType = chip.text.toString().lowercase()

            dietTypeChip = selectedDietType
            dietTypeChipId = selectedChipId
        }

        binding.btnApply.setOnClickListener {
            recipesViewModel.saveMealAndDietType(
                mealTypeChip,
                mealTypeChipId,
                dietTypeChip,
                dietTypeChipId
            )
        }
        return binding.root
    }

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