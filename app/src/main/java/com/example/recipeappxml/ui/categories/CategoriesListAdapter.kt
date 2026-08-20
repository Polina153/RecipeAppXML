package com.example.recipeappxml.ui.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeappxml.R
import com.example.recipeappxml.databinding.ItemCategoryBinding
import com.example.recipeappxml.model.Category

class CategoriesListAdapter :
    ListAdapter<Category, CategoriesListAdapter.ViewHolder>(CATEGORY_COMPARATOR) {

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    // DiffUtil вычисляет разницу между старым и новым списком
    companion object {
        private val CATEGORY_COMPARATOR = object : DiffUtil.ItemCallback<Category>() {
            override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
                // Сравниваем по ID — уникальному идентификатору сущности
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
                // Если весь объект равен (все поля совпадают), пересоздавать ViewHolder не нужно
                return oldItem == newItem
            }
        }
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.itemTitle.text = category.title
            binding.itemDescription.text = category.description

            Glide.with(binding.itemImage)
                .load(category.imageUrl)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_error)
                .into(binding.itemImage)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val binding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val category = getItem(position)
        viewHolder.bind(category)
        viewHolder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(category.id)
        }
    }

    fun interface OnItemClickListener {
        fun onItemClick(categoryId: Int)
    }

}