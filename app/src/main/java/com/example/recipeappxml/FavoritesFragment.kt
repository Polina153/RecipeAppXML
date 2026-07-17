package com.example.recipeappxml

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.recipeappxml.Constants.ARG_RECIPE
import com.example.recipeappxml.Constants.FAVORITES_KEY
import com.example.recipeappxml.Constants.FAVORITES_PREFS_NAME
import com.example.recipeappxml.Constants.RECIPE_ID_KEY
import com.example.recipeappxml.databinding.FragmentFavoritesBinding


class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
    }

    private fun initRecycler() {
        val favoriteIds = getFavorites()  // Set<String>
        /*  val favoriteRecipes = favoriteIds.mapNotNull { id ->
              RecipesRepositoryStub.getRecipeById(id.toIntOrNull() ?: return@mapNotNull null)
          }*/
        val ids = favoriteIds.mapNotNull { it.toIntOrNull() }.toSet() // Set<Int>
        val favoriteRecipes = RecipesRepositoryStub.getRecipesByIds(ids)

        val recipesListAdapter = RecipesListAdapter(favoriteRecipes)
        recipesListAdapter.setOnItemClickListener { recipeId ->
            openRecipeByRecipeId(recipeId)
        }
        binding.rvRecipes.adapter = recipesListAdapter
        val isEmpty = favoriteRecipes.isEmpty()
        binding.rvRecipes.isVisible = !isEmpty        // скрываем RecyclerView
        binding.tvEmptyState.isVisible = isEmpty
    }

    private fun openRecipeByRecipeId(recipeId: Int) {
        val recipe = RecipesRepositoryStub.getRecipeById(recipeId)
        val bundle = Bundle()
        bundle.putInt(RECIPE_ID_KEY, recipeId)
        bundle.putParcelable(ARG_RECIPE, recipe)
        val fragment = RecipeFragment()
        fragment.arguments = bundle

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, fragment)
            .commit()
    }

    private fun getFavorites(): MutableSet<String> {
        val sharedPref =
            requireActivity().getSharedPreferences(FAVORITES_PREFS_NAME, Context.MODE_PRIVATE)
        val storedSet = sharedPref.getStringSet(FAVORITES_KEY, emptySet()) ?: emptySet()
        return HashSet(storedSet)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}