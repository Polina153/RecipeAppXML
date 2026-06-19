package com.example.recipeappxml

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.recipeappxml.databinding.FragmentListCategoriesBinding


class CategoriesListFragment : Fragment() {

    private var _binding: FragmentListCategoriesBinding? = null
    private val binding get() = _binding!!

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
            CategoriesListAdapter(RecipesRepositoryStub.getCategories())
        categoriesListAdapter.setOnItemClickListener {
            openRecipesByCategoryId()
        }
        binding.rvCategories.adapter = categoriesListAdapter
    }

    fun openRecipesByCategoryId() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, RecipesListFragment())
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}