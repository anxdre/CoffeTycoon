package com.anxdre.coffetycoon.data

import java.io.Serializable

data class Product(val name: String, val price: Long, var quantity: Int = 1) : Serializable