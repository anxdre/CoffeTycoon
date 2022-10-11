package com.anxdre.coffetycoon.data

import java.io.Serializable


data class User(
    val username: String,
    val password: String,
    val balance: Double,
): Serializable