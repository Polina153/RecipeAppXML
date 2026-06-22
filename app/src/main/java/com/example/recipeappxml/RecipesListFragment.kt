package com.example.recipeappxml

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.recipeappxml.databinding.FragmentRecipesListBinding

class RecipesListFragment : Fragment() {

    private var _binding: FragmentRecipesListBinding? = null
    private val binding get() = requireNotNull(_binding)

    var categoryId: Int? = null
    var categoryName: String? = null
    var categoryImage: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { args ->
            categoryId = args.getInt(Constants.ID_KEY)
            categoryName = args.getString(Constants.NAME_KEY)
            categoryImage = args.getString(Constants.IMAGE_KEY)
        }
        initRecycler()
    }

    fun initRecycler() {
        val recipesListAdapter = RecipesListAdapter(
            RecipesRepositoryStub.getRecipesByCategoryId(categoryId ?: 0)
        )
        recipesListAdapter.setOnItemClickListener { recipeId ->
            openRecipeByRecipeId(recipeId)
        }
        binding.rvRecipes.adapter = recipesListAdapter
    }

    private fun openRecipeByRecipeId(recipeId: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, RecipeFragment())
            .commit()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}