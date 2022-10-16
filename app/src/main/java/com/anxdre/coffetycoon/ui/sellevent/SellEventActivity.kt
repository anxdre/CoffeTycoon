package com.anxdre.coffetycoon.ui.sellevent

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.utils.MDUtil.getStringArray
import com.anxdre.coffetycoon.R
import com.anxdre.coffetycoon.data.Coffee
import com.anxdre.coffetycoon.data.Customer
import com.anxdre.coffetycoon.data.ResumeOfTheDay
import com.anxdre.coffetycoon.data.Weather
import com.anxdre.coffetycoon.util.SharedPrefHelper
import com.anxdre.coffetycoon.util.showAlert
import com.anxdre.coffetycoon.util.showAlertConfirmation
import kotlinx.android.synthetic.main.activity_sell_event_activity.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

class SellEventActivity : AppCompatActivity(), CoroutineScope {
    //coroutines lifecycle
    private val backJob = Job()
    val custAdapter = CustAdapter()

    private val sharedPrefHelper by lazy { SharedPrefHelper(applicationContext) }
    private lateinit var weather: Weather
    private lateinit var coffee: Coffee
    private var stock: Int = 0
    private var locationTresshold: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sell_event_activity)


        rv_customer.adapter = custAdapter

        btn_stop_sell.setOnClickListener {
            closeConfirmation()
        }

        //retrive data
        with(intent) {
            coffee = getSerializableExtra("coffee") as Coffee
            weather = getSerializableExtra("weather") as Weather
            locationTresshold = getLongExtra("location", 0)
            stock = getIntExtra("stock", 0)
        }

        //Coroutines task
        val task = launch {
            val listOfCustomer = getStringArray(R.array.customer)

            val timerTask = launch {
                for (elapsedTime in 6..22) {
                    delay(5000)
                    tv_timer.text = "$elapsedTime:00"

                    if (elapsedTime % 3 == 0) {
                        weather = randomizeWeather()
                        tv_weather.text = weather.name
                        tv_weather.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            null,
                            ContextCompat.getDrawable(this@SellEventActivity, weather.bgIcon),
                            null
                        )
                    }
                }
            }

            timerTask.start() //start timer coroutines

            //start
            run {
                while (timerTask.isActive) {
                    //delay weather
                    delay(weather.threshold)
                    val customerName = listOfCustomer[Random.nextInt(0, 3)]
                    val customerItemPurchase = Random.nextInt(1, 10)
                    custAdapter.addCustomer(
                        Customer(
                            customerName,
                            customerItemPurchase,
                            checkServedOrNot(customerItemPurchase)
                        )
                    )
                    stock -= customerItemPurchase
                    //delay location
                    delay(locationTresshold)
                }
            }
            joinAll()
            showEndOfDay()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                closeConfirmation()
            }
        })
    }

    private fun checkServedOrNot(itemPurchase: Int): Boolean {
        if (itemPurchase > stock) {
            return false
        }
        return true
    }

    private fun calculateSoldItem(): ResumeOfTheDay {
        var totalItem = 0
        var totalCustServed = 0
        var totalCustUnserved = 0
        var totalEarnings: Long = 0

        for (data in custAdapter.getCustomer()) {
            totalItem += data.itemQuantity

            if (data.served) {
                totalCustServed += 1
            }

            if (!data.served) {
                totalCustUnserved += 1
            }

            totalEarnings += data.itemQuantity * coffee.getPrice()
        }
        return ResumeOfTheDay(totalItem, totalCustServed, totalCustUnserved, totalEarnings)
    }

    private fun closeConfirmation() {
        showAlertConfirmation(
            context = this,
            title = "Kamu yakin ingin berhenti ?",
            message = "yah..., Penjualan hari ini akan dihentikan. apa kamu setuju ?",
            trueButtonEvent = { dialog ->
                dialog.hide()
                showEndOfDay()
            },
            falseButtonEvent = {
                it.hide()
            })
    }

    private fun showEndOfDay() {
        val resumeOfTheDay = calculateSoldItem()
        showAlert(
            this,
            title = "Catatan Hari Ini",
            message = "Hari ini kamu berhasil menjual sebanyak ${resumeOfTheDay.totalItemSold} pcs" +
                    " dengan total pelanggan dilayani : ${resumeOfTheDay.totalCustServed} org" +
                    " juga total pelanggan Tidak dilayani : ${resumeOfTheDay.totalCustUnserved} org" +
                    " serta total pendapatan : IDR ${resumeOfTheDay.totalEarnigs}"
        ) {
            SharedPrefHelper(applicationContext).also {
                it.updateUserBalance(it.getUser().balance.plus(resumeOfTheDay.totalEarnigs))
            }
            onStop()
            finish()
        }
    }

    private fun randomizeWeather(): Weather {
        val listOfWeather = arrayListOf<Weather>()
        listOfWeather.add(
            Weather(
                resources.getStringArray(R.array.weather)[0],
                R.drawable.ic_weather_sunny,
                5000
            )
        )
        listOfWeather.add(
            Weather(
                resources.getStringArray(R.array.weather)[1],
                R.drawable.ic_weather_cloudy,
                5800
            )
        )
        listOfWeather.add(
            Weather(
                resources.getStringArray(R.array.weather)[2],
                R.drawable.ic_weather_rainy,
                10000
            )
        )
        listOfWeather.add(
            Weather(
                resources.getStringArray(R.array.weather)[3],
                R.drawable.ic_weather_storm,
                20000
            )
        )
        return listOfWeather.random()
    }

    override fun onStop() {
        super.onStop()
        SharedPrefHelper(applicationContext).also {
            val incrementDays = it.getUser().dayOfSell?.plus(1) ?: 1
            it.updateUserDay(incrementDays)
        }
    }

    override val coroutineContext: CoroutineContext
        get() = backJob + Dispatchers.Main
}