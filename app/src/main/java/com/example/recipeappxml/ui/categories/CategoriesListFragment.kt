package com.example.recipeappxml.ui.categories

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
            } else {
                adapter.submitList(state.categories)
                val isEmpty = state.categories.isEmpty()
                binding.rvCategories.isVisible = !isEmpty
                binding.tvEmptyState.isVisible = isEmpty
            }
        }
    }

    private fun openRecipesByCategoryId(categoryId: Int) {

        val category = adapter.currentList.find { it.id == categoryId } ?: return

        val categoryName: String = category.title
        val categoryImage = category.imageUrl

        val bundle = Bundle().apply {
            putInt(Constants.ID_KEY, categoryId)
            putString(Constants.NAME_KEY, categoryName)
            putString(Constants.IMAGE_KEY, categoryImage)
        }

        view?.findNavController()?.navigate(R.id.recipesListFragment, args = bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}