package com.anxdre.coffetycoon.data

import java.io.Serializable

data class DayOfSell(
    val day: Int,
    var weather: Weather,
    val location: LocationRent,
    var stock: Int,
    val sellPrice: Long,
    val costPrice: Long,
    val resumeOfTheDay: ResumeOfTheDay = ResumeOfTheDay()
) : Serializable {

    fun calculateResumeOfTheDay(listOfCustomer: MutableList<Customer>) {
        for (data in listOfCustomer) {
            if (data.served) {
                resumeOfTheDay.totalCustServed += 1
                resumeOfTheDay.totalItemSold += data.itemQuantity
            }

            if (!data.served) {
                resumeOfTheDay.totalCustUnserved += 1
            }

            resumeOfTheDay.totalEarnings += data.itemQuantity * sellPrice
        }
    }
}
