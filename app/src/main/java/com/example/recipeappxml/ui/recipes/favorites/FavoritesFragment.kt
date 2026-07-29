package com.example.recipeappxml.ui.recipes.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.recipeappxml.R
import com.example.recipeappxml.data.Constants
import com.example.recipeappxml.databinding.FragmentFavoritesBinding
import com.example.recipeappxml.ui.recipes.recipe.RecipeFragment
import com.example.recipeappxml.ui.recipes.recipes_list.RecipesListAdapter

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: FavoritesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModel.favoriteRecipes.observe(viewLifecycleOwner) { favoriteRecipes ->
            val adapter = RecipesListAdapter(favoriteRecipes)
            adapter.setOnItemClickListener { recipeId ->
                openRecipeByRecipeId(recipeId)
            }
            binding.rvRecipes.adapter = adapter

            val isEmpty = favoriteRecipes.isEmpty()
            binding.rvRecipes.isVisible = !isEmpty
            binding.tvEmptyState.isVisible = isEmpty
        }
    }

    private fun openRecipeByRecipeId(recipeId: Int) {
        val bundle = Bundle().apply { putInt(Constants.RECIPE_ID_KEY, recipeId) }
        val fragment = RecipeFragment().apply { arguments = bundle }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, fragment)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}