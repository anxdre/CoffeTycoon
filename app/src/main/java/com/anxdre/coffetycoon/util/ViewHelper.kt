package com.anxdre.coffetycoon.util

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar


fun viewVisible(view: View) {
    view.visibility = View.VISIBLE
}

fun viewInvisible(view: View) {
    view.visibility = View.INVISIBLE
}

fun viewGone(view: View) {
    view.visibility = View.GONE
}

fun viewAnimate(context: Context, view: View, animXml: Int) {
    view.startAnimation(
        AnimationUtils.loadAnimation(
            context, animXml
        )
    )
}

fun showSortToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun showLongToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

fun showSortSnackBar(parentView: View, message: String) {
    Snackbar.make(parentView, message, Snackbar.LENGTH_SHORT).show()
}

fun showLongSnackBar(parentView: View, message: String) {
    Snackbar.make(parentView, message, Snackbar.LENGTH_LONG).show()
}
