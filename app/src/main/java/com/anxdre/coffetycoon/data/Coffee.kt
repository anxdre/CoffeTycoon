package com.anxdre.coffetycoon.data

import java.io.Serializable
import java.text.DecimalFormat


data class Coffee(
    val name: String,
    private var price: Double = 0.0,
    val listOfRecipe: MutableList<RecipeItem>
) : Serializable {
    fun getPrice(): Double = price

    fun setPrice(newPrice: Double): Boolean {
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

    private fun checkMinimumPrice(): Double {
        var price: Double = 0.0
        for (recipe in listOfRecipe) {
            price += (recipe.price * recipe.quantity)
        }
        return price
    }
}