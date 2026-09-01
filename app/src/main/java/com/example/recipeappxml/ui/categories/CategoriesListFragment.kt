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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.recipeappxml.data.RecipeApplication
import com.example.recipeappxml.databinding.FragmentListCategoriesBinding
import com.example.recipeappxml.di.CategoriesListViewModelFactory
import kotlinx.coroutines.launch

class CategoriesListFragment : Fragment() {

    private var _binding: FragmentListCategoriesBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: CategoriesListViewModel by viewModels {
        val appContainer = (requireActivity().applicationContext as RecipeApplication).appContainer
        CategoriesListViewModelFactory(appContainer.repository)
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.categoryListUiState.collect { state ->
                        adapter.submitList(state.categories)
                        binding.rvCategories.isVisible = state.categories.isNotEmpty()
                        binding.tvEmptyState.isVisible =
                            state.categories.isEmpty() && !state.isLoading
                    }
                }
                launch {
                    viewModel.errorEvent.collect { error ->
                        Log.e("!!!", "Ошибка с загрузкой списка категорий", error)
                        Toast.makeText(
                            requireContext(),
                            "Ошибка: ${error.message ?: "Нет сети"}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
        viewModel.triggerRefresh()
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