package com.example.recipeappxml.ui.recipes.recipeslist

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeappxml.databinding.ItemRecipeBinding
import com.example.recipeappxml.model.Recipe
import java.io.IOException

class RecipesListAdapter(private val recipes: List<Recipe>) :
    RecyclerView.Adapter<RecipesListAdapter.ViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    fun interface OnItemClickListener {
        fun onItemClick(recipeId: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemRecipeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(recipe.id)
        }
    }

    override fun getItemCount(): Int = recipes.size

    class ViewHolder(private val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: Recipe) {
            binding.itemTitle.text = recipe.title

            try {
                itemView.context.assets.open(recipe.imageUrl).use {
                    val drawable = Drawable.createFromStream(it, null)
                    binding.itemImage.setImageDrawable(drawable)
                }
            } catch (e: IOException) {
                Log.e("RecipeListAdapter", "Ошибка загрузки изображения", e)
            }
        }
    }
}