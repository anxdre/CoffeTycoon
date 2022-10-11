package com.anxdre.coffetycoon.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import com.anxdre.coffetycoon.R
import com.anxdre.coffetycoon.ui.auth.LoginActivity
import com.anxdre.coffetycoon.util.viewAnimate
import com.anxdre.coffetycoon.util.viewVisible
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SplashActivity : AppCompatActivity(), CoroutineScope {
    private val backJob = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        launch {
            viewAnimate(this@SplashActivity, iv_logo, R.anim.left_to_right)
            iv_logo.animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {

                }

                override fun onAnimationEnd(p0: Animation?) {
                    viewVisible(tv_logo)
                    viewAnimate(this@SplashActivity, tv_logo, R.anim.fade)
                }

                override fun onAnimationRepeat(p0: Animation?) {

                }
            })
            delay(4000)
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        }
    }

    override val coroutineContext: CoroutineContext
        get() = backJob + Dispatchers.Main

    override fun onPause() {
        super.onPause()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancelChildren()
    }
}