package com.example.recipeappxml.ui.recipes.favorites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.recipeappxml.R
import com.example.recipeappxml.data.Constants
import com.example.recipeappxml.data.FavoritePrefsManager
import com.example.recipeappxml.databinding.FragmentFavoritesBinding
import com.example.recipeappxml.ui.recipes.recipes_list.RecipesListAdapter
import com.example.recipeappxml.utils.GenericViewModelFactory

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: FavoritesViewModel by viewModels {
        GenericViewModelFactory{
            FavoritesViewModel(
                FavoritePrefsManager(requireContext().applicationContext)
            )
        }

    }
    private val adapter by lazy { RecipesListAdapter() }

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
        binding.rvRecipes.adapter = adapter
        adapter.setOnItemClickListener { recipeId ->
            openRecipeByRecipeId(recipeId)
        }
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModel.favoriteRecipes.observe(viewLifecycleOwner) { state ->
            if (state.error != null) {
                Log.e("!!!", "Ошибка с загрузкой списка избранных рецептов")
            } else {
                adapter.submitList(state.recipes)
                val isEmpty = state.recipes.isEmpty()
                binding.rvRecipes.isVisible = !isEmpty
                binding.tvEmptyState.isVisible = isEmpty
            }
        }
    }


    private fun openRecipeByRecipeId(recipeId: Int) {
        val bundle = Bundle().apply { putInt(Constants.RECIPE_ID_KEY, recipeId) }
        view?.findNavController()?.navigate(R.id.recipeFragment, args = bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}