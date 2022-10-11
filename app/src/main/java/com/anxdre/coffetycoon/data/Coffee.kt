package com.anxdre.coffetycoon.data

import java.io.Serializable


data class Coffee(
    val name: String,
    private var price: Double = 0.0,
    val listOfRecipe: ArrayList<Recipe>
) : Serializable {

    fun setPrice(newPrice: Double): Boolean {
        if (newPrice < checkMinimumPrice()) {
            throw IllegalArgumentException("Price must be higher than recipe cost")
        }
        price = newPrice
        return true
    }

    fun checkMinimumPrice(): Double {
        var price: Double = 0.0
        for (recipe in listOfRecipe) {
            price += recipe.price
        }
        return price
    }
}