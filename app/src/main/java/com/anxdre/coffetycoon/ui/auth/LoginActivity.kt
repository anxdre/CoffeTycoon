package com.anxdre.coffetycoon.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anxdre.coffetycoon.R
import com.anxdre.coffetycoon.data.User
import com.anxdre.coffetycoon.ui.mainmenu.MainMenuActivity
import com.anxdre.coffetycoon.util.SharedPrefHelper
import com.anxdre.coffetycoon.util.showSortToast
import com.anxdre.coffetycoon.util.viewAnimate
import com.anxdre.coffetycoon.util.viewVisible
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private val sharedPrefHelper by lazy { SharedPrefHelper(applicationContext) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        animateOnCreated()
        btn_login.setOnClickListener {
            if (validateField()) getSignIn() else showSortToast(this,"Data tidak lengkap")
        }
    }

    private fun getSignIn() {
        val user = User(
            ti_email.editText!!.text.toString(),
            ti_password.editText!!.text.toString(),
            ti_email.editText!!.text.toString(),
            350000
        )

        val task = sharedPrefHelper.saveUser(user)
        if (!task) {
            Toast.makeText(this@LoginActivity, "Buka Toko Gagal", Toast.LENGTH_SHORT).show()
        } else {
            startActivity(Intent(this@LoginActivity, MainMenuActivity::class.java))
        }
    }

    private fun checkUser() {
        val user = sharedPrefHelper.getUser()
        if (user.username.isNotBlank()) {
            startActivity(Intent(this@LoginActivity, MainMenuActivity::class.java))
            finish()
        }
    }

    private fun validateField(): Boolean {
        if (ti_email.editText!!.text.isNullOrBlank()) {
            return false
        }
        if (ti_password.editText!!.text.isNullOrBlank()) {
            return false
        }
        return true
    }

    private fun animateOnCreated() {
        iv_background.apply {
            viewAnimate(this@LoginActivity, this, R.anim.top_to_bottom)

            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {

                }

                override fun onAnimationEnd(p0: Animation?) {
                    viewVisible(tv_title)
                    viewAnimate(this@LoginActivity, tv_title, R.anim.fade)
                }

                override fun onAnimationRepeat(p0: Animation?) {

                }

            })
        }
    }

    override fun onStart() {
        super.onStart()
        checkUser()
    }
}