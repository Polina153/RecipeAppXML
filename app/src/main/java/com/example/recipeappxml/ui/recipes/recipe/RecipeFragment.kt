package com.example.recipeappxml.ui.recipes.recipe

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
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
import java.io.IOException

class RecipeFragment : Fragment() {

    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: RecipeViewModel by viewModels()

    private var ingredientsAdapter: IngredientsAdapter? = null
    private var methodAdapter: MethodAdapter? = null

    private var currentQuantity: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recipeId = requireArguments().getInt(Constants.RECIPE_ID_KEY, -1)
        viewModel.loadRecipe(recipeId)
        initRecycler()
        initFavoriteButton()
        observeRecipe()
    }

    private fun initFavoriteButton() {
        binding.favoriteButton.setOnClickListener {
            val recipeId = requireArguments().getInt(Constants.RECIPE_ID_KEY, -1)
            viewModel.onFavoritesClicked(recipeId)
        }
    }

    private fun initRecycler() {
        ingredientsAdapter = IngredientsAdapter(emptyList())
        methodAdapter = MethodAdapter(emptyList())

        val divider = MaterialDividerItemDecoration(
            requireContext(),
            LinearLayoutManager.VERTICAL
        )
        binding.rvIngredients.addItemDecoration(divider)
        binding.rvMethod.addItemDecoration(divider)
        binding.rvIngredients.adapter = ingredientsAdapter
        binding.rvMethod.adapter = methodAdapter

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                ingredientsAdapter?.updateIngredients(progress)
                currentQuantity = progress
                binding.amount.text = progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun observeRecipe() {
        viewModel.selectedRecipe.observe(viewLifecycleOwner) { state ->
            Log.i("!!!", state.isFavorite.toString())

            // UI
            binding.recipeName.text = state.title
            try {
                state.imageUrl?.let { requireContext().assets.open(it) }.use {
                    val drawable = Drawable.createFromStream(it, null)
                    binding.recipeImage.setImageDrawable(drawable)
                }
            } catch (e: IOException) {
                Log.e("RecipeFragment", "Ошибка загрузки изображения", e)
            }
            updateFavoriteIcon(state.isFavorite)

            ingredientsAdapter = IngredientsAdapter(state.ingredients).also {
                it.updateIngredients(currentQuantity)
            }
            binding.rvIngredients.adapter = ingredientsAdapter

            methodAdapter = MethodAdapter(state.method)
            binding.rvMethod.adapter = methodAdapter
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