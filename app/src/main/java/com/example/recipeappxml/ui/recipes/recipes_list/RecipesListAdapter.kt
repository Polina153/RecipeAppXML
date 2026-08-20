package com.example.recipeappxml.ui.recipes.recipes_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeappxml.R
import com.example.recipeappxml.databinding.ItemRecipeBinding
import com.example.recipeappxml.model.Recipe

// Наследуемся от ListAdapter вместо RecyclerView.Adapter
class RecipesListAdapter : ListAdapter<Recipe, RecipesListAdapter.ViewHolder>(RECIPE_COMPARATOR) {

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    // Вызов этого метода теперь будет обновлять список
    // submitList(null) очистит список, если нужно

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
        // getItem(position) берем из ListAdapter
        val recipe = getItem(position)
        holder.bind(recipe)

        // Клики лучше вешать в bind или сразу после inflate для оптимизации
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(recipe.id)
        }
    }

    // DiffUtil вычисляет разницу между старым и новым списком
    companion object {
        private val RECIPE_COMPARATOR = object : DiffUtil.ItemCallback<Recipe>() {
            override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
                // Сравниваем по ID — уникальному идентификатору сущности
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
                // Если весь объект равен (все поля совпадают), пересоздавать ViewHolder не нужно
                return oldItem == newItem
            }
        }
    }

    class ViewHolder(private val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: Recipe) {
            binding.itemTitle.text = recipe.title

            Glide.with(binding.itemImage)
                .load(recipe.imageUrl)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_error)
                .into(binding.itemImage)

            //Log.e("RecipeListAdapter", "Ошибка загрузки изображения ${recipe.imageUrl}", e)
        }
    }
}