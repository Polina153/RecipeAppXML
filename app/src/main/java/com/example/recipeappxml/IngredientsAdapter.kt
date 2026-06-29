package com.example.recipeappxml

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeappxml.databinding.ItemIngredientBinding


class IngredientsAdapter(private val dataSet: List<Ingredient>) :
    RecyclerView.Adapter<IngredientsAdapter.ViewHolder>() {


    private var quantity: Int = 1
    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    class ViewHolder(private val binding: ItemIngredientBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ingredient: Ingredient, multiplier: Int) {
            val rawValue = ingredient.quantity.toDouble() * multiplier
            val formatted = if (rawValue == rawValue.toLong().toDouble()) {
                rawValue.toLong().toString()
            } else {
                String.format("%.1f", rawValue)
            }
            binding.ingredient.text = "$formatted ${ingredient.unitOfMeasure} ${ingredient.description}"

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        // Create new views (invoked by the layout manager)
        val binding =
            ItemIngredientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val ingredient = dataSet[position]
        viewHolder.bind(ingredient, quantity)
        viewHolder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(dataSet[position])
        }
    }

    fun interface OnItemClickListener {
        fun onItemClick(ingredient: Ingredient)
    }

    fun updateIngredients(progress: Int) {
        quantity = progress
        notifyDataSetChanged()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}