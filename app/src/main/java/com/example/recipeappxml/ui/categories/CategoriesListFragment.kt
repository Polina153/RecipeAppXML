package com.example.recipeappxml.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.recipeappxml.R
import com.example.recipeappxml.data.Constants
import com.example.recipeappxml.data.RecipesRepositoryStub
import com.example.recipeappxml.databinding.FragmentListCategoriesBinding
import com.example.recipeappxml.ui.recipes.recipeslist.RecipesListFragment

class CategoriesListFragment : Fragment() {

    private var _binding: FragmentListCategoriesBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
    }

    fun initRecycler() {
        val categoriesListAdapter =
            CategoriesListAdapter(RecipesRepositoryStub.Companion.getCategories())
        categoriesListAdapter.setOnItemClickListener {
            openRecipesByCategoryId(it)
        }
        binding.rvCategories.adapter = categoriesListAdapter
    }

    fun openRecipesByCategoryId(categoryId: Int) {

        val categories = RecipesRepositoryStub.Companion.getCategories()
        val category = categories.find { it.id == categoryId } ?: return

        val categoryName: String = category.title
        val categoryImage = category.imageUrl

        val bundle = Bundle().apply {
            putInt(Constants.ID_KEY, categoryId)
            putString(Constants.NAME_KEY, categoryName)
            putString(Constants.IMAGE_KEY, categoryImage)
        }

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, RecipesListFragment().apply { arguments = bundle })
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}