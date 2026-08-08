package com.example.recipeappxml.ui.recipes.recipes_list

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipeappxml.R
import com.example.recipeappxml.data.Constants
import com.example.recipeappxml.databinding.FragmentRecipesListBinding
import com.example.recipeappxml.ui.recipes.recipe.RecipeFragment
import java.io.IOException

class RecipesListFragment : Fragment() {

    private var _binding: FragmentRecipesListBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: RecipesListViewModel by viewModels {
        RecipesViewModelFactory(requireArguments().getInt(Constants.ID_KEY))
    }
    private val adapter by lazy { RecipesListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvRecipes.adapter = adapter
        adapter.setOnItemClickListener { recipeId ->
            openRecipeByRecipeId(recipeId)
        }

        viewModel.recipeList.observe(viewLifecycleOwner) { state ->
            if (state.error != null) {
                Log.e("!!!", "Ошибка с загрузкой списка рецептов")
            } else {
                adapter.submitList(state.recipes)
                val isEmpty = state.recipes.isEmpty()
                binding.rvRecipes.isVisible = !isEmpty
                binding.tvEmptyState.isVisible = isEmpty
            }
        }

        val categoryName = requireArguments().getString(Constants.NAME_KEY)
        val categoryImage = requireArguments().getString(Constants.IMAGE_KEY)
        binding.recipesHeadingText.text = categoryName.orEmpty()

        try {
            binding.recipeImage.context.assets.open(categoryImage.orEmpty()).use {
                val drawable = Drawable.createFromStream(it, null)
                binding.recipeImage.setImageDrawable(drawable)
            }
        } catch (e: IOException) {
            Log.e("RecipesHeadingImage", "Ошибка загрузки изображения", e)
        }
    }

    private fun openRecipeByRecipeId(recipeId: Int) {
        val bundle = Bundle()
        bundle.putInt(Constants.RECIPE_ID_KEY, recipeId)
        val fragment = RecipeFragment()
        fragment.arguments = bundle

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, fragment)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class RecipesViewModelFactory(
    private val categoryId: Int
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipesListViewModel::class.java)) {
            return RecipesListViewModel(categoryId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}