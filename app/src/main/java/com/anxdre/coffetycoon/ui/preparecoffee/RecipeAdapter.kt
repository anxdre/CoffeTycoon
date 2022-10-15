package com.anxdre.coffetycoon.ui.preparecoffee

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.anxdre.coffetycoon.R
import com.anxdre.coffetycoon.data.RecipeItem
import com.anxdre.coffetycoon.util.showSortToast
import kotlinx.android.synthetic.main.layout_item_recipe.view.*

class RecipeAdapter : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    private val dataset = mutableListOf<RecipeItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder =
        RecipeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_item_recipe, parent, false)
        )

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) =
        holder.bindView(dataset[position])

    override fun getItemCount(): Int = dataset.size

    fun addItemToRecipe(recipe: RecipeItem, position: Int? = null) {
        if (dataset.contains(recipe)) {
            if (position != null) {
                dataset[position].quantity++
            }else{
                throw Exception("Item telah ditambahkan sebelumnya")
            }
        } else {
            recipe.quantity++
            dataset.add(recipe)
        }
        notifyDataSetChanged()
    }

    private fun removeItemFromRecipe(position: Int) {
        dataset[position].quantity -= 1
    }

    fun getRecipePrice(): Int {
        var price = 0
        for (data in dataset) {
            price += (data.price * data.quantity)
        }
        return price
    }

    fun getRecipeDataset() = dataset

    inner class RecipeViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bindView(recipe: RecipeItem) {
            when (recipe.type) {
                1 -> {
                    itemView.tv_item_title.text = recipe.name
                    itemView.tv_item_price.text = "Harga : IDR ${recipe.price} /10g"
                    itemView.iv_icon_recipe.setImageDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.ic_item_beans
                        )
                    )
                }

                2 -> {
                    itemView.tv_item_title.text = recipe.name
                    itemView.tv_item_price.text = "Harga : IDR ${recipe.price} /120g"
                    itemView.iv_icon_recipe.setImageDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.ic_item_liquid
                        )
                    )

                }

                3 -> {
                    itemView.tv_item_title.text = recipe.name
                    itemView.tv_item_price.text = "Harga : IDR ${recipe.price} /5g"
                    itemView.iv_icon_recipe.setImageDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.ic_item_sugar
                        )
                    )

                }
            }

            itemView.btn_add.setOnClickListener {
                try {
                    addItemToRecipe(recipe, layoutPosition)
                    updateQuantity(recipe)
                }catch (e:Exception){
                    showSortToast(itemView.context,e.message ?: "Something error occurred")
                }

            }

            itemView.btn_subtract.setOnClickListener {
                removeItemFromRecipe(layoutPosition)
                updateQuantity(recipe)
            }
        }

        private fun updateQuantity(recipe: RecipeItem) {
            if (recipe.quantity > 0) {
                itemView.tv_quantity.text = recipe.quantity.toString()
            } else {
                dataset.remove(recipe)
                notifyDataSetChanged()
            }
        }
    }
}