package com.example.recipeappxml.ui.recipes.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipeappxml.R
import com.example.recipeappxml.data.Constants
import com.example.recipeappxml.databinding.FragmentRecipeBinding
import com.google.android.material.divider.MaterialDividerItemDecoration

class RecipeFragment : Fragment() {

    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: RecipeViewModel by viewModels()
    private var ingredientsAdapter: IngredientsAdapter? = null
    private var methodAdapter: MethodAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recipeId = requireArguments().getInt(Constants.RECIPE_ID_KEY, -1)
        viewModel.loadRecipe(recipeId)
        initFavoriteButton()
        initUI()
    }

    private fun initFavoriteButton() {
        binding.favoriteButton.setOnClickListener {
            val recipeId = requireArguments().getInt(Constants.RECIPE_ID_KEY, -1)
            viewModel.onFavoritesClicked(recipeId)
        }
    }

    private fun initUI() {
        ingredientsAdapter = IngredientsAdapter(mutableListOf())
        methodAdapter = MethodAdapter(mutableListOf())

        val divider = MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        binding.rvIngredients.addItemDecoration(divider)
        binding.rvMethod.addItemDecoration(divider)
        binding.rvIngredients.adapter = ingredientsAdapter
        binding.rvMethod.adapter = methodAdapter

        binding.seekBar.setOnSeekBarChangeListener(PortionSeekBarListener{progress -> viewModel.onPortionsCountChanged(progress)})

        viewModel.selectedRecipe.observe(viewLifecycleOwner) { state ->
            // UI
            binding.recipeName.text = state.title
            binding.recipeImage.setImageDrawable(state.recipeImage)

            updateFavoriteIcon(state.isFavorite)

            // Синхронизируем SeekBar и счётчик со стейтом
            binding.seekBar.progress = state.portionsCount
            binding.amount.text = state.portionsCount.toString()

            ingredientsAdapter?.updateData(state.ingredients, state.portionsCount)
            methodAdapter?.updateData(state.method)
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        val iconRes = if (isFavorite) R.drawable.ic_heart else R.drawable.ic_heart_empty
        binding.favoriteButton.setImageResource(iconRes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class PortionSeekBarListener(val onChangeIngredients: (Int) -> Unit): SeekBar.OnSeekBarChangeListener{
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            onChangeIngredients(progress)
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
    }

}