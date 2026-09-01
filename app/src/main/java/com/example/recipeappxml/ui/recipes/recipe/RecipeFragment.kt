package com.example.recipeappxml.ui.recipes.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.recipeappxml.R
import com.example.recipeappxml.data.RecipeApplication
import com.example.recipeappxml.databinding.FragmentRecipeBinding
import com.example.recipeappxml.di.RecipeViewModelFactory
import com.google.android.material.divider.MaterialDividerItemDecoration

class RecipeFragment : Fragment() {

    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = requireNotNull(_binding)
    private lateinit var viewModel: RecipeViewModel
    private var ingredientsAdapter: IngredientsAdapter? = null
    private var methodAdapter: MethodAdapter? = null
    private val recipeFragmentArgs: RecipeFragmentArgs by navArgs<RecipeFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appContainer = (requireActivity().applicationContext as RecipeApplication).appContainer
        viewModel =
            RecipeViewModelFactory(recipesRepository = appContainer.repository).create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recipeId = recipeFragmentArgs.recipeId
        viewModel.loadRecipe(recipeId)
        initFavoriteButton()
        initUI()
    }

    private fun initFavoriteButton() {
        binding.favoriteButton.setOnClickListener {
            viewModel.onFavoritesClicked(recipeFragmentArgs.recipeId)
        }
    }

    private fun initUI() {
        ingredientsAdapter = IngredientsAdapter(mutableListOf())
        methodAdapter = MethodAdapter(mutableListOf())

        val divider = MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        binding.rvIngredients.addItemDecoration(divider)
        binding.rvMethod.addItemDecoration(divider)
        binding.rvIngredients.adapter = ingredientsAdapter
        binding.rvMethod.adapter = methodAdapter

        binding.seekBar.setOnSeekBarChangeListener(PortionSeekBarListener { progress ->
            viewModel.onPortionsCountChanged(
                progress
            )
        })

        viewModel.selectedRecipe.observe(viewLifecycleOwner) { state ->

            if (state.error != null) {
                Toast.makeText(requireContext(), state.error, Toast.LENGTH_LONG).show()
                return@observe
            }
            // UI
            binding.recipeName.text = state.title

            Glide.with(binding.recipeImage)
                .load(state.imageUrl)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_error)
                .into(binding.recipeImage)

            updateFavoriteIcon(state.isFavorite)

            // Синхронизируем SeekBar и счётчик со стейтом
            binding.seekBar.progress = state.portionsCount
            binding.amount.text = state.portionsCount.toString()

            ingredientsAdapter?.updateData(state.ingredients, state.portionsCount)
            methodAdapter?.updateData(state.method)
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        val iconRes = if (isFavorite) R.drawable.ic_heart else R.drawable.ic_heart_empty
        binding.favoriteButton.setImageResource(iconRes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class PortionSeekBarListener(val onChangeIngredients: (Int) -> Unit) :
    SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            onChangeIngredients(progress)
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
    }

}