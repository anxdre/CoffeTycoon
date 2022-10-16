package com.anxdre.coffetycoon.ui.preparecoffee

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.anxdre.coffetycoon.R
import com.anxdre.coffetycoon.data.*
import com.anxdre.coffetycoon.ui.sellevent.SellEventActivity
import com.anxdre.coffetycoon.util.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_prepare_coffee.*
import kotlinx.android.synthetic.main.layout_confirmation_price.view.*
import kotlinx.android.synthetic.main.layout_item_add_recipe.view.*

class PrepareCoffeeActivity : AppCompatActivity() {
    private val weather by lazy { randomizeWeather() }
    private val listOfItem by lazy { getRecipeList() }
    private val recipeAdapter = RecipeAdapter()
    private lateinit var userdata: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prepare_coffee)

        userdata = intent.getSerializableExtra("user") as User
        rv_recipe.adapter = recipeAdapter

        sp_location.adapter = baseSpinnerAdapter(this, resources.getStringArray(R.array.locations))
        setWeather()

        btn_add_recipe.setOnClickListener {
            addRecipe()
        }

        btn_start_sell.setOnClickListener {
            if (validateCoffeeRecipe()) {
                setCoffeePriceConfirmation()
            }
        }

        //handle back button
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backConfirmation()
            }
        })
    }

    private fun getRecipeList(): MutableList<RecipeItem> {
        //read json to string
        val coffeeJsonString =
            this.assets.open("CoffeeBeans.json").bufferedReader().use { it.readText() }
        val liquidJsonString =
            this.assets.open("Liquid.json").bufferedReader().use { it.readText() }
        val sugarJsonString = this.assets.open("Sugar.json").bufferedReader().use { it.readText() }

        //create object from string
        val listOfCoffee = Gson().fromJson(coffeeJsonString, Recipe::class.java)
        val listOfLiquid = Gson().fromJson(liquidJsonString, Recipe::class.java)
        val listOfSugar = Gson().fromJson(sugarJsonString, Recipe::class.java)

        //add all of item object to list
        val listOfItem = mutableListOf<RecipeItem>()
        listOfItem.addAll(listOfCoffee)
        listOfItem.addAll(listOfLiquid)
        listOfItem.addAll(listOfSugar)

        return listOfItem
    }

    private fun randomizeWeather(): Weather {
        //add list of weather data
        val weatherData = arrayListOf<Weather>()
        weatherData.add(
            Weather(
                resources.getStringArray(R.array.weather)[0],
                R.drawable.ic_weather_sunny,
                5000
            )
        )
        weatherData.add(
            Weather(
                resources.getStringArray(R.array.weather)[1],
                R.drawable.ic_weather_cloudy,
                5800
            )
        )
        weatherData.add(
            Weather(
                resources.getStringArray(R.array.weather)[2],
                R.drawable.ic_weather_rainy,
                10000
            )
        )
        weatherData.add(
            Weather(
                resources.getStringArray(R.array.weather)[3],
                R.drawable.ic_weather_storm,
                20000
            )
        )
        //return random weather from list
        return weatherData.random()
    }

    private fun setWeather() {
        tv_weather.text = weather.name
        iv_weather.setImageDrawable(ContextCompat.getDrawable(this, weather.bgIcon))
    }

    private fun addRecipe() {
        //show dialog
        val dialog = MaterialDialog(this)
            .customView(R.layout.layout_item_add_recipe)
        dialog.view.setBackgroundColor(Color.TRANSPARENT)
        dialog.show()

        //define layout
        val customView = dialog.getCustomView()
        val btnAdd = customView.btn_add_item_recipe
        val spItem = customView.sp_item

        //convert object to string
        val listOfNameItem = arrayListOf<String>()
        for (data in listOfItem) {
            listOfNameItem.add(data.name)
        }

        //add item to spinner
        spItem.adapter = baseSpinnerAdapter(this, listOfNameItem.toTypedArray())

        btnAdd.setOnClickListener {
            try {
                recipeAdapter.addItemToRecipe(listOfItem[spItem.selectedItemPosition])
                dialog.hide()
            } catch (e: Exception) {
                showSortToast(this, e.message ?: "Something Error Occurred")
            }
        }
    }

    private fun validateCoffeeRecipe(): Boolean {
        if (recipeAdapter.getRecipePrice() == 0) {
            showSortSnackBar(findViewById(android.R.id.content), "Resep tidak boleh kosong")
            return false
        }
        if (et_name_coffee.text.isNullOrBlank()) {
            showSortSnackBar(findViewById(android.R.id.content), "Nama kopi tidak boleh kosong")
            return false
        }
        if (et_total_item.text.isNullOrBlank()) {
            showSortSnackBar(findViewById(android.R.id.content), "Jumlah kopi tidak boleh 0")
            return false
        }
        return true
    }

    private fun setCoffeePriceConfirmation() {
        val coffeeNeed = recipeAdapter.getRecipePrice()
        val totalCoffeePrice = coffeeNeed * et_total_item.text.toString().toInt()

        showAlertConfirmation(
            context = this,
            title = "Konfirmasi data penjualan",
            message = buildString {
                append("Item kamu membutuhkan biaya IDR $coffeeNeed / pcs")
                append("\ndengan total biaya keseluruhan item sebesar IDR $totalCoffeePrice")
            },
            btnTrueText = "Konfirmasi",
            inputMode = true,
            trueButtonEvent = { dialog ->
                if (totalCoffeePrice > userdata.balance
                ) {
                    showSortSnackBar(
                        findViewById(android.R.id.content),
                        "Uang kas kamu tidak cukup"
                    )
                } else {
                    //dual confirmation
                    val coffee = Coffee(
                        name = et_name_coffee.text.toString(),
                        listOfRecipe = recipeAdapter.getRecipeDataset()
                    ).also {
                        try {
                            it.setPrice(dialog.view.et_price_confirmation.text.toString().toLong())
                            startSellConfirmation(it, et_total_item.text.toString().toInt())
                            dialog.hide()
                        } catch (e: Exception) {
                            showSortSnackBar(
                                findViewById(android.R.id.content),
                                e.message ?: "Something error"
                            )
                        }
                    }

                }
            },
            falseButtonEvent = {
                it.hide()
            })
    }

    private fun startSellConfirmation(coffee: Coffee, stock: Int) {
        //define coffee price
        val coffeeNeed = recipeAdapter.getRecipePrice()
        showAlertConfirmation(
            context = this,
            title = "Konfirmasi data penjualan",
            message = buildString {
                append("Item ")
                append(coffee.name)
                append(" akan dijual sebanyak ")
                append(stock)
                append(" pcs, seharga IDR ")
                append(coffee.getPrice())
                append(" / pcs. jual sekarang ?")
            },
            btnTrueText = "Jual",
            trueButtonEvent = {
                val totalCoffeePrice = coffeeNeed * et_total_item.text.toString().toInt()
                SharedPrefHelper(applicationContext).updateUserBalance((userdata.balance - totalCoffeePrice))

                val intent = Intent(this@PrepareCoffeeActivity, SellEventActivity::class.java)
                val locationTresshold:Long = ((sp_location.selectedItemPosition + 10) * 5 * 100).toLong()
                startActivity(
                    with(intent) {
                        putExtra("coffee", coffee)
                        putExtra("weather", weather)
                        putExtra("location", locationTresshold)
                        putExtra("stock", stock)
                    }
                )
                finish()
            },
            falseButtonEvent = {
                it.hide()
            })
    }

    private fun backConfirmation() {
        showAlertConfirmation(
            context = this,
            title = "Kamu yakin ingin keluar ?",
            message = "Hari ini akan dilewati dan semua data pada hari ini akan direset",
            trueButtonEvent = {
                onStop()
                finish()
            },
            falseButtonEvent = {
                it.hide()
            })
    }

    override fun onStop() {
        super.onStop()
        SharedPrefHelper(applicationContext).also {
            val incrementDays = userdata.dayOfSell?.plus(1) ?: 1
            it.updateUserDay(incrementDays)
        }
    }
}