package com.anxdre.coffetycoon.ui.mainmenu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anxdre.coffetycoon.R
import com.anxdre.coffetycoon.data.User
import com.anxdre.coffetycoon.ui.auth.LoginActivity
import com.anxdre.coffetycoon.ui.preparecoffee.PrepareCoffeeActivity
import com.anxdre.coffetycoon.util.SharedPrefHelper
import com.anxdre.coffetycoon.util.showAlertConfirmation
import com.anxdre.coffetycoon.util.showLongSnackBar
import com.anxdre.coffetycoon.util.showSortSnackBar
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainMenuActivity : AppCompatActivity() {
    private lateinit var userData: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        tv_me.setOnClickListener {
            showSortSnackBar(cl_parent_main, "Love you ...")
        }

        iv_linkedin.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.linkedin.com/in/anxdre/")))
        }

        try {
            userData = SharedPrefHelper(applicationContext).getUser()
            GlobalScope.launch(Dispatchers.IO) {
                launch(Dispatchers.Main) {
                    showLongSnackBar(
                        cl_parent_main,
                        "Selamat datang ${userData!!.username}"
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

        btn_sell.setOnClickListener {
            openPrepareActivity()
        }

        btn_logout.setOnClickListener {
            closeStoreConfirm()
        }

        tv_cash.text = "IDR ${userData.balance}"

        tv_profile.text = userData.username

        tv_dayOfSale.text = userData.dayOfSell.toString()

    }

    private fun openPrepareActivity() {
        startActivity(
            Intent(
                this@MainMenuActivity,
                PrepareCoffeeActivity::class.java
            ).putExtra("user", userData)
        )
    }

    private fun closeStoreConfirm() {
        showAlertConfirmation(
            context = this,
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