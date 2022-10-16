package com.anxdre.coffetycoon.util

import android.content.Context
import com.anxdre.coffetycoon.data.User

class SharedPrefHelper(context: Context) {
    private val userSharedPref = context.getSharedPreferences("User", Context.MODE_PRIVATE)

    fun saveUser(user: User): Boolean {
        return with(userSharedPref.edit()) {
            putString("username", user.username)
            putString("password", user.password)
            putLong("balance", user.balance.toLong())
            putInt("day", user.dayOfSell ?: 1)
        }.commit()
    }

    fun updateUserBalance(balance: Long): Boolean {
        return with(userSharedPref.edit()) {
            putLong("balance", balance)
        }.commit()
    }

    fun updateUserDay(day:Int): Boolean {
        return with(userSharedPref.edit()) {
            putInt("day", day)
        }.commit()
    }

    fun getUser(): User {
        val user: User
        with(userSharedPref.edit()) {
            user = User(
                userSharedPref.getString("username", "")!!,
                userSharedPref.getString("password", "")!!,
                userSharedPref.getLong("balance", 0),
                userSharedPref.getInt("day", 0)
            )
        }
        return user
    }

    fun removeUser(): Boolean {
        return with(userSharedPref.edit()) {
            remove("username")
            remove("password")
            remove("balance")
        }.commit()
    }
}