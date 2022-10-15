package com.anxdre.coffetycoon.ui.mainmenu

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anxdre.coffetycoon.R
import com.anxdre.coffetycoon.data.User
import com.anxdre.coffetycoon.ui.auth.LoginActivity
import com.anxdre.coffetycoon.ui.preparecoffee.PrepareCoffeeActivity
import com.anxdre.coffetycoon.util.SharedPrefHelper
import com.anxdre.coffetycoon.util.showLongSnackBar
import com.anxdre.coffetycoon.util.showSortSnackBar
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class MainMenuActivity : AppCompatActivity() {
    private var userData: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        try {
            userData = intent.getSerializableExtra("user") as User
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
            SharedPrefHelper(applicationContext).removeUser()
            startActivity(Intent(this@MainMenuActivity, LoginActivity::class.java))
            finish()
        }

        tv_cash.text = "IDR ${DecimalFormat("#").format(userData?.balance ?: "Undefined")}"

    }

    private fun openPrepareActivity() {
        startActivity(
            Intent(
                this@MainMenuActivity,
                PrepareCoffeeActivity::class.java
            ).putExtra("user", userData)
        )
    }

}