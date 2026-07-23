package com.example.recipeappxml.ui.recipes.recipe

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipeappxml.R
import com.example.recipeappxml.data.Constants
import com.example.recipeappxml.databinding.FragmentRecipeBinding
import com.example.recipeappxml.model.Recipe
import com.google.android.material.divider.MaterialDividerItemDecoration
import java.io.IOException

class RecipeFragment : Fragment() {

    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: RecipeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipe: Recipe? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable(Constants.ARG_RECIPE, Recipe::class.java)
        } else {
            @Suppress("DEPRECATION")
            requireArguments().getParcelable(Constants.ARG_RECIPE)
        }
        initUI(recipe)
        initRecycler(recipe)

        viewModel.selectedRecipe.observe(viewLifecycleOwner, Observer{
            Log.i("!!!", it.isFavorite.toString())
        })


    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        val iconRes = if (isFavorite) R.drawable.ic_heart else R.drawable.ic_heart_empty
        binding.favoriteButton.setImageResource(iconRes)
    }


    fun initUI(recipe: Recipe?) {

        binding.recipeName.text = recipe?.title

        try {
            recipe?.imageUrl?.let { requireContext().assets.open(it) }.use {
                val drawable = Drawable.createFromStream(it, null)
                binding.recipeImage.setImageDrawable(drawable)
            }
        } catch (e: IOException) {
            Log.e("CategoriesListAdapter", "Ошибка загрузки изображения", e)
        }

        val recipeId = recipe?.id?.toString() ?: return
        val favorites = getFavorites()
        val isFavorite = favorites.contains(recipeId)
        updateFavoriteIcon(isFavorite)

        // 2. Вешаем клик на сердечко
        binding.favoriteButton.setOnClickListener {
            val currentFavorites = getFavorites()
            val updatedSet: Set<String>
            val newState: Boolean

            if (currentFavorites.contains(recipeId)) {
                // Удаляем из избранного
                updatedSet = currentFavorites - recipeId
                newState = false
            } else {
                // Добавляем в избранное
                updatedSet = currentFavorites + recipeId
                newState = true
            }
            saveFavorites(updatedSet)
            updateFavoriteIcon(newState)
        }
    }

    fun initRecycler(recipe: Recipe?) {
        val ingredientsAdapter =
            recipe?.ingredients?.let { IngredientsAdapter(it) }
        binding.rvIngredients.adapter = ingredientsAdapter

        val methodAdapter =
            recipe?.method?.let { MethodAdapter(it) }
        binding.rvMethod.adapter = methodAdapter

        val divider = MaterialDividerItemDecoration(
            requireContext(),
            LinearLayoutManager.VERTICAL
        )
        binding.rvIngredients.addItemDecoration(divider)
        binding.rvMethod.addItemDecoration(divider)
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                ingredientsAdapter?.updateIngredients(progress)
                binding.amount.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //TODO("Not yet implemented")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //TODO("Not yet implemented")
            }

        })
    }

    fun saveFavorites(idCollection: Set<String>) {
        val sharedPref =
            requireActivity().getSharedPreferences(
                Constants.FAVORITES_PREFS_NAME,
                Context.MODE_PRIVATE
            )
        sharedPref.edit {
            putStringSet(Constants.FAVORITES_KEY, idCollection)
            apply()
        }
    }

    fun getFavorites(): MutableSet<String> {
        val sharedPref =
            requireActivity().getSharedPreferences(
                Constants.FAVORITES_PREFS_NAME,
                Context.MODE_PRIVATE
            )
        val storedSet = sharedPref.getStringSet(Constants.FAVORITES_KEY, emptySet()) ?: emptySet()
        return HashSet(storedSet)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}