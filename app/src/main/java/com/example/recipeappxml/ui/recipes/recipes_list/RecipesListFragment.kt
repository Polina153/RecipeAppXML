package com.example.recipeappxml.ui.recipes.recipes_list

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
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.recipeappxml.R
import com.example.recipeappxml.data.RecipeApplication
import com.example.recipeappxml.databinding.FragmentRecipesListBinding
import com.example.recipeappxml.di.RecipesListViewModelFactory
import kotlinx.coroutines.launch

class RecipesListFragment : Fragment() {

    private var _binding: FragmentRecipesListBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val args: RecipesListFragmentArgs by navArgs()
    private val adapter by lazy { RecipesListAdapter() }

    private val viewModel: RecipesListViewModel by viewModels {
        val appContainer = (requireActivity().applicationContext as RecipeApplication).appContainer
        RecipesListViewModelFactory(args.category.id, appContainer.repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvRecipes.adapter = adapter
        adapter.setOnItemClickListener { recipeId ->
            openRecipeByRecipeId(recipeId)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.recipeList.collect { state ->
                        adapter.submitList(state.recipes)
                        val isEmpty = state.recipes.isEmpty()
                        binding.rvRecipes.isVisible = !isEmpty
                        binding.tvEmptyState.isVisible = isEmpty
                        if (state.error != null) {
                            Log.e("!!!", "Ошибка с загрузкой списка рецептов", state.error)
                            Toast.makeText(
                                requireContext(),
                                "Ошибка: ${state.error.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }

        binding.recipesHeadingText.text = args.category.title

        Glide.with(binding.recipeImage)
            .load(args.category.imageUrl)
            .placeholder(R.drawable.img_placeholder)
            .error(R.drawable.img_error)
            .into(binding.recipeImage)

    }

    private fun openRecipeByRecipeId(recipeId: Int) {
        view?.findNavController()?.navigate(
            RecipesListFragmentDirections.actionRecipesListFragmentToRecipeFragment(recipeId)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}