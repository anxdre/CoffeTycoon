package com.anxdre.coffetycoon.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Recipe : ArrayList<RecipeItem>()

data class RecipeItem(
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Int,
    @SerializedName("type")
    val type: Int,
    var quantity: Int = 0
) : Serializable