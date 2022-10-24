package com.anxdre.coffetycoon.ui.sellevent

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.utils.MDUtil.getStringArray
import com.anxdre.coffetycoon.R
import com.anxdre.coffetycoon.data.Customer
import com.anxdre.coffetycoon.data.DayOfSell
import com.anxdre.coffetycoon.data.getListOfWeather
import com.anxdre.coffetycoon.data.randomizeWeather
import com.anxdre.coffetycoon.ui.mainmenu.MainMenuActivity
import com.anxdre.coffetycoon.util.SharedPrefHelper
import com.anxdre.coffetycoon.util.showAlertConfirmation
import kotlinx.android.synthetic.main.activity_sell_event.*
import kotlinx.android.synthetic.main.layout_resume_of_the_day.view.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

class SellEventActivity : AppCompatActivity(), CoroutineScope {
    private val backJob = Job()
    private val custAdapter = CustAdapter()
    private val dayOfSell by lazy { intent.getSerializableExtra("dayOfSell") as DayOfSell }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sell_event)

        rv_customer.adapter = custAdapter

        tv_day_sell.text = "Day ${dayOfSell.day}"

        btn_stop_sell.setOnClickListener {
            closeConfirmation()
        }

        //Coroutines task
        launch(backJob) {
            val timerTask = launch {
                startTimer()
            }
            timerTask.start() //start timer coroutines

            //start customer coroutines
            run {
                startCustomer(timerTask)
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

    private suspend fun startTimer() {
        val listOfWeather = getListOfWeather(applicationContext)
        listOfWeather.add(dayOfSell.weather)

        //start with prediction first
        tv_weather.text = dayOfSell.weather.name
        tv_weather.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            ContextCompat.getDrawable(this@SellEventActivity, dayOfSell.weather.bgIcon),
            null
        )

        for (elapsedTime in 7..18) {
            delay(6000)
            tv_timer.text = "$elapsedTime:00"

            //randomize every 3 hours elapsed
            if (elapsedTime % 3 == 0) {
                dayOfSell.weather = randomizeWeather(applicationContext, listOfWeather)
                tv_weather.text = dayOfSell.weather.name
                tv_weather.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(this@SellEventActivity, dayOfSell.weather.bgIcon),
                    null
                )
            }
        }
    }

    private suspend fun startCustomer(timerTask: Job) {
        val listOfCustomer = getStringArray(R.array.customer)
        var stock = dayOfSell.stock
        while (timerTask.isActive) {
            //delay weather
            delay(dayOfSell.weather.threshold)
            val customerName = listOfCustomer[Random.nextInt(0, 3)]
            val customerItemPurchase = Random.nextInt(1, 10)

            if (checkServedOrNot(customerItemPurchase, stock)) {
                stock -= customerItemPurchase
                custAdapter.addCustomer(
                    Customer(
                        customerName,
                        customerItemPurchase,
                        true
                    )
                )
            } else {
                custAdapter.addCustomer(
                    Customer(
                        customerName,
                        customerItemPurchase,
                        true
                    )
                )
            }
            sv_sell_event.scrollTo(0, sv_sell_event.bottom);
            //delay location
            delay(dayOfSell.location.tressHold)
        }
    }

    private fun checkServedOrNot(itemPurchase: Int, stock: Int): Boolean {
        if (itemPurchase > stock) {
            return false
        }
        return true
    }

    private fun closeConfirmation() {
        showAlertConfirmation(
            context = this,
            title = "Kamu yakin ingin berhenti ?",
            message = "yah..., Penjualan hari ini akan dihentikan. apa kamu setuju ?",
            trueButtonEvent = { dialog ->
                dialog.dismiss()
                showEndOfDay()
            },
            falseButtonEvent = {
                it.dismiss()
            })
    }


    private fun showEndOfDay() {
        coroutineContext.cancelChildren()
        dayOfSell.calculateResumeOfTheDay(custAdapter.getCustomer())
        val dialog = MaterialDialog(
            this, BottomSheet()
        ).customView(R.layout.layout_resume_of_the_day).noAutoDismiss()
        dialog.lifecycleOwner(this)
        dialog.cornerRadius(16f)
        dialog.cancelable(false)
        dialog.show()

        val itemView = dialog.view
        val costOfTheDay = (dayOfSell.costPrice * dayOfSell.stock) + dayOfSell.location.price

        itemView.tv_day_resume.text = "Hasil Penjualan Hari Ke ${dayOfSell.day}"
        itemView.tv_total_cup_revenues.text =
            "${dayOfSell.resumeOfTheDay.totalItemSold} Cup Of Coffee \n @IDR ${dayOfSell.sellPrice}"
        itemView.tv_price_total_cup_revenues.text = "IDR ${dayOfSell.resumeOfTheDay.totalEarnings}"

        itemView.tv_total_cup_expense.text =
            "Location Rent @${dayOfSell.location.name} & Ingredient Cost @IDR ${dayOfSell.costPrice}"
        itemView.tv_price_total_cup_expense.text = "IDR ${costOfTheDay}"

        itemView.rv_customer_resume.adapter = custAdapter
        itemView.tv_total_cust_served.text = "${dayOfSell.resumeOfTheDay.totalCustServed} Cust"
        itemView.tv_total_cust_unserved.text = "${dayOfSell.resumeOfTheDay.totalCustUnserved} Cust"

        itemView.tv_profit_day.text = "IDR ${dayOfSell.resumeOfTheDay.totalEarnings - costOfTheDay}"

        itemView.btn_continue_resume.setOnClickListener {
            SharedPrefHelper(applicationContext).also {
                it.updateUserBalance(it.getUser().balance.plus(dayOfSell.resumeOfTheDay.totalEarnings))
            }
            startActivity(Intent(this, MainMenuActivity::class.java))
            finish()
        }
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