package com.example.recipeappxml.ui.categories

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
import com.example.recipeappxml.databinding.FragmentListCategoriesBinding
import com.example.recipeappxml.utils.GenericViewModelFactory

class CategoriesListFragment : Fragment() {

    private var _binding: FragmentListCategoriesBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: CategoriesListViewModel by viewModels {
        GenericViewModelFactory {
            CategoriesListViewModel()
        }
    }
    private val adapter by lazy { CategoriesListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvCategories.adapter = adapter
        adapter.setOnItemClickListener {
            openRecipesByCategoryId(it)
        }

        viewModel.categoryList.observe(viewLifecycleOwner) { state ->
            if (state.error != null) {
                Log.e("!!!", "Ошибка с загрузкой списка категорий")
                Toast.makeText(requireContext(), "Ошибка с загрузкой списка категорий", Toast.LENGTH_LONG).show()
            } else {
                adapter.submitList(state.categories)
                val isEmpty = state.categories.isEmpty()
                binding.rvCategories.isVisible = !isEmpty
                binding.tvEmptyState.isVisible = isEmpty
            }
        }
    }

    private fun openRecipesByCategoryId(categoryId: Int) {

        val category = adapter.currentList.find { it.id == categoryId }
            ?: throw IllegalArgumentException("Category with id $categoryId not found")

        findNavController().navigate(
            CategoriesListFragmentDirections.actionCategoriesListFragmentToRecipesListFragment(
                category
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}