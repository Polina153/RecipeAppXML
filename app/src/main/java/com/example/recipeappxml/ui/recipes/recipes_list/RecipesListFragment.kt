package com.example.recipeappxml.ui.recipes.recipes_list

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.recipeappxml.databinding.FragmentRecipesListBinding
import com.example.recipeappxml.utils.GenericViewModelFactory
import java.io.IOException

class RecipesListFragment : Fragment() {

    private var _binding: FragmentRecipesListBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val args: RecipesListFragmentArgs by navArgs()
    private val viewModel: RecipesListViewModel by viewModels {
        GenericViewModelFactory {
            RecipesListViewModel(args.category.id)
        }
    }
    private val adapter by lazy { RecipesListAdapter() }

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

        viewModel.recipeList.observe(viewLifecycleOwner) { state ->
            if (state.error != null) {
                Log.e("!!!", "Ошибка с загрузкой списка рецептов")
                Toast.makeText(requireContext(), "Ошибка с загрузкой списка рецептов", Toast.LENGTH_LONG).show()
            } else {
                adapter.submitList(state.recipes)
                val isEmpty = state.recipes.isEmpty()
                binding.rvRecipes.isVisible = !isEmpty
                binding.tvEmptyState.isVisible = isEmpty
            }
        }

        binding.recipesHeadingText.text = args.category.title

        try {
            binding.recipeImage.context.assets.open(args.category.imageUrl).use {
                val drawable = Drawable.createFromStream(it, null)
                binding.recipeImage.setImageDrawable(drawable)
            }
        } catch (e: IOException) {
            Log.e("RecipesHeadingImage", "Ошибка загрузки изображения", e)
        }
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