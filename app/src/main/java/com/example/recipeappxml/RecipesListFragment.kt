package com.example.recipeappxml

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.recipeappxml.Constants.RECIPE_ID_KEY
import com.example.recipeappxml.Constants.RECIPE_KEY
import com.example.recipeappxml.databinding.FragmentRecipesListBinding
import java.io.IOException

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
        binding.recipesHeadingText.text = categoryName

        try {
            binding.recipeImage.context.assets.open(categoryImage.orEmpty()).use {
                val drawable = Drawable.createFromStream(it, null)
                binding.recipeImage.setImageDrawable(drawable)
            }
        } catch (e: IOException) {
            Log.e("RecipesHeadingImage", "Ошибка загрузки изображения", e)
        }
        initRecycler()
    }

    private fun initRecycler() {
        val recipesListAdapter = RecipesListAdapter(
            RecipesRepositoryStub.getRecipesByCategoryId(/*categoryId ?:*/ 0)
        )
        recipesListAdapter.setOnItemClickListener { recipeId ->
            openRecipeByRecipeId(recipeId)
        }
        binding.rvRecipes.adapter = recipesListAdapter
    }

    private fun openRecipeByRecipeId(recipeId: Int) {
        val recipe = RecipesRepositoryStub.getRecipesByCategoryId(recipeId)
        val bundle = Bundle()
        bundle.putInt(RECIPE_ID_KEY, recipeId)
        bundle.putParcelable(RECIPE_KEY, recipe as Parcelable?)
        val fragment = RecipeFragment()
        fragment.arguments = bundle

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, fragment)
            .commit()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}