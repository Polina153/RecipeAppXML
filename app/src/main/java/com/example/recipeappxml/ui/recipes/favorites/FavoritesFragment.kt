package com.example.recipeappxml.ui.recipes.favorites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.recipeappxml.data.RecipeApplication
import com.example.recipeappxml.databinding.FragmentFavoritesBinding
import com.example.recipeappxml.di.FavoritesViewModelFactory
import com.example.recipeappxml.ui.recipes.recipes_list.RecipesListAdapter

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: FavoritesViewModel by viewModels {
        val appContainer = (requireActivity().applicationContext as RecipeApplication).appContainer
        FavoritesViewModelFactory(appContainer.repository)
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
        binding.backToCategories.setOnClickListener {
            findNavController().navigate(
                FavoritesFragmentDirections.actionFavoritesFragmentToCategoriesListFragment()
            )
        }
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModel.favoriteRecipes.observe(viewLifecycleOwner) { state ->
            if (state.error != null) {
                Log.e("!!!", "Ошибка с загрузкой списка избранных рецептов")
                Toast.makeText(
                    requireContext(),
                    "Ошибка с загрузкой списка избранных рецептов",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                adapter.submitList(state.recipes)
                val isEmpty = state.recipes.isEmpty()
                binding.rvRecipes.isVisible = !isEmpty
                binding.tvEmptyState.isVisible = isEmpty
            }
        }
    }


    private fun openRecipeByRecipeId(recipeId: Int) {
        findNavController().navigate(
            FavoritesFragmentDirections.actionFavoritesFragmentToRecipeFragment(recipeId)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}