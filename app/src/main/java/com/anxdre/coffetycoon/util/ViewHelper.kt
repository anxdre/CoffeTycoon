package com.anxdre.coffetycoon.util

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import com.anxdre.coffetycoon.R
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

fun <T> baseSpinnerAdapter(context: Context, listOfData: Array<T>): ArrayAdapter<T> =
    ArrayAdapter(context, R.layout.layout_simple_spinner_listitem, listOfData)