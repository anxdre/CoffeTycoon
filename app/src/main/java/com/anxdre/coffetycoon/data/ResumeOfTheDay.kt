package com.anxdre.coffetycoon.data

import java.io.Serializable

data class ResumeOfTheDay(
    var totalItemSold: Int = 0,
    var totalCustServed: Int = 0,
    var totalCustUnserved: Int = 0,
    var totalEarnings: Long = 0
): Serializable