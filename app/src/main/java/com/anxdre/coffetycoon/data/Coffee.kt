package com.anxdre.coffetycoon.data

import java.io.Serializable
import java.text.DecimalFormat


data class Coffee(
    val name: String,
    private var price: Long = 0,
    val listOfRecipe: MutableList<RecipeItem>
) : Serializable {
    fun getPrice(): Long = price

    fun setPrice(newPrice: Long): Boolean {
        val requirePrice = checkMinimumPrice()
        if (newPrice < requirePrice) {
            throw IllegalArgumentException(
                "Price must be higher than recipe cost ${
                    DecimalFormat("#").format(
                        requirePrice
                    )
                }"
            )
        }
        price = newPrice
        return true
    }

    private fun checkMinimumPrice(): Long {
        var price: Long = 0
        for (recipe in listOfRecipe) {
            price += (recipe.price * recipe.quantity)
        }
        return price
    }
}