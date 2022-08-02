package com.example.foodx.app.ui.fragments.recipe.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.foodx.databinding.FragmentRecipeBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RecipeBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentRecipeBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRecipeBottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}