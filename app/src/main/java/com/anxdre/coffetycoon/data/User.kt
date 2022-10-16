package com.anxdre.coffetycoon.data

import java.io.Serializable


data class User(
    val username: String,
    val password: String,
    val balance: Long,
    val dayOfSell: Int? = 1,
): Serializable