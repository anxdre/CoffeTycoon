package com.anxdre.coffetycoon.ui.mainmenu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.afollestad.materialdialogs.utils.MDUtil.getStringArray
import com.anxdre.coffetycoon.R
import com.anxdre.coffetycoon.data.LocationRent
import com.anxdre.coffetycoon.data.Product
import com.anxdre.coffetycoon.data.User
import com.anxdre.coffetycoon.ui.auth.LoginActivity
import com.anxdre.coffetycoon.util.*
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainMenuActivity : AppCompatActivity() {
    private lateinit var userData: User
    private val listOfRecipeItem = mutableListOf<Product>()
    private val listOfLocation by lazy { prepareDataLocation() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        prepareAccount()
        prepareDataProduct()
        changeDescText()

        tv_me.setOnClickListener {
            showSortSnackBar(cl_parent_main, "Love you ...")
        }

        iv_linkedin.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.linkedin.com/in/anxdre/")))
        }

        tv_cash.text = "IDR ${userData.balance}"

        tv_profile.text = userData.username

        tv_dayOfSale.text = userData.dayOfSell.toString()

        sp_location.adapter = baseSpinnerAdapter(this, getStringArray(R.array.locations))

        sp_location.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                sellingValidation()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        btn_add_coffee.setOnClickListener {
            val quantity = addQuantity(
                tv_coffee_quantity.text.toString().toInt()
            )
            tv_coffee_quantity.text = quantity.toString()
            listOfRecipeItem[0].quantity = quantity
            changeDescText()
        }
        btn_add_milk.setOnClickListener {
            val quantity = addQuantity(tv_milk_quantity.text.toString().toInt())
            tv_milk_quantity.text = quantity.toString()
            listOfRecipeItem[1].quantity = quantity
            changeDescText()
        }
        btn_add_water.setOnClickListener {
            val quantity = addQuantity(tv_water_quantity.text.toString().toInt())
            tv_water_quantity.text = quantity.toString()
            listOfRecipeItem[2].quantity = quantity
            changeDescText()
        }

        btn_min_coffee.setOnClickListener {
            val quantity = minQuantity(tv_coffee_quantity.text.toString().toInt())
            tv_coffee_quantity.text = quantity.toString()
            listOfRecipeItem[0].quantity = quantity
            changeDescText()
        }
        btn_min_milk.setOnClickListener {
            val quantity = minQuantity(tv_milk_quantity.text.toString().toInt())
            tv_milk_quantity.text = quantity.toString()
            listOfRecipeItem[1].quantity = quantity
            changeDescText()
        }
        btn_min_water.setOnClickListener {
            val quantity = minQuantity(tv_water_quantity.text.toString().toInt())
            tv_water_quantity.text = quantity.toString()
            listOfRecipeItem[2].quantity = quantity
            changeDescText()
        }

        et_price_cup.doOnTextChanged { text, start, before, count ->
            sellingValidation()
        }
        et_total_cup.doOnTextChanged { text, start, before, count ->
            sellingValidation()
        }
    }

    private fun sellingValidation() {
        if (validateField()) {
            btn_start_sell.isEnabled = true
            showAllCost(
                et_total_cup.text.toString().toInt(),
                sp_location.selectedItemPosition
            )
        } else {
            viewVisible(tv_cost_warn)
            viewVisible(tv_cup_warn)
            btn_start_sell.isEnabled = false
            showAllCost(
                0,
                sp_location.selectedItemPosition
            )
        }
    }


    private fun addQuantity(value: Int): Int = value + 1

    private fun minQuantity(value: Int): Int {
        return if (value > 1) {
            value - 1
        } else {
            1
        }
    }

    private fun prepareDataProduct() {
        listOfRecipeItem.addAll(
            listOf(
                Product("Coffee", 500),
                Product("Milk", 100),
                Product("Water", 200)
            )

        )
    }

    private fun prepareDataLocation(): List<LocationRent> {
        val listOfLocation = mutableListOf<LocationRent>()
        val listOfNameLocation = getStringArray(R.array.locations)

        listOfLocation.add(LocationRent(listOfNameLocation[0], 100000, 1200))
        listOfLocation.add(LocationRent(listOfNameLocation[1], 350000, 800))
        listOfLocation.add(LocationRent(listOfNameLocation[2], 500000, 500))

        return listOfLocation
    }

    private fun prepareAccount() {
        try {
            userData = SharedPrefHelper(applicationContext).getUser()
            GlobalScope.launch(Dispatchers.IO) {
                launch(Dispatchers.Main) {
                    showLongSnackBar(
                        cl_parent_main, "Selamat datang ${userData!!.username}"
                    )
                }
                delay(4000)
                launch(Dispatchers.Main) {
                    showLongSnackBar(cl_parent_main, "Semoga hari ini laris maris ya...")
                }

            }

        } catch (e: Exception) {
            showSortSnackBar(cl_parent_main, "Toko tidak ditemukan silahkan login ulang")
            startActivity(Intent(this@MainMenuActivity, LoginActivity::class.java))
            finish()
        }
    }

    private fun showAllCost(totalOfCup: Int, locationId: Int) {
        val recipeCost = calculateTotalRecipeCost()
        tv_total_cup.text = "Total cup \n@IDR ${recipeCost}"
        tv_price_total_cup.text = "IDR ${recipeCost * totalOfCup}"

        tv_location_rent.text = "Location Rent \n@${sp_location.selectedItem}"
        tv_price_rent.text = "IDR ${listOfLocation[locationId].price}"

        tv_total_cost.text = "IDR ${(recipeCost * totalOfCup) + listOfLocation[locationId].price}"
    }

    private fun changeDescText() {
        tv_desc_prepare.text =
            "cost per cup of coffee is IDR ${calculateTotalRecipeCost()} \nHow many cup do you want to sell ?"
    }

    private fun calculateTotalRecipeCost(): Long {
        var price: Long = 0
        for (data in listOfRecipeItem) {
            price += data.price * data.quantity
        }
        return price
    }

    private fun validateField(): Boolean {
        if (et_total_cup.text.isNullOrBlank()) {
            return false
        }
        if (et_price_cup.text.isNullOrBlank()) {
            return false
        }
        if (et_total_cup.text.toString().toInt() < 1) {
            return false
        }
        if (et_price_cup.text.toString().toInt() < 1) {
            return false
        }
        if (et_price_cup.text.toString().toInt() < calculateTotalRecipeCost()) {
            return false
        }
        viewGone(tv_cost_warn)
        viewGone(tv_cup_warn)
        return true
    }

    private fun logoutUser() {
        showAlertConfirmation(context = this,
            title = "Anda yakin ingin menutup toko ?",
            message = "Waroeng akan dianggap bangkrut dan dimusnahkan",
            trueButtonEvent = {
                SharedPrefHelper(applicationContext).removeUser()
                startActivity(Intent(this@MainMenuActivity, LoginActivity::class.java))
                finish()
            },
            falseButtonEvent = {
                it.hide()
            })
    }


    override fun onResume() {
        super.onResume()
        SharedPrefHelper(applicationContext).also {
            userData = it.getUser()
            tv_dayOfSale.text = userData.dayOfSell.toString()
            tv_cash.text = "IDR  ${userData.balance}"
        }
    }

}