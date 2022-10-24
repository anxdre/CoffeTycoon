package com.anxdre.coffetycoon.util

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.anxdre.coffetycoon.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.layout_confirmation_price.view.*


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

fun showAlertConfirmation(
    context: Context,
    title: String? = null,
    message: String? = null,
    inputMode: Boolean = false,
    btnTrueText: String = "Ya",
    btnFalseText: String = "Tidak",
    trueButtonEvent: (dialog: MaterialDialog) -> Unit?,
    falseButtonEvent: (dialog: MaterialDialog) -> Unit?
) {
    val dialog = MaterialDialog(context).customView(R.layout.layout_confirmation_price)
    dialog.view.setBackgroundColor(Color.TRANSPARENT)
    dialog.show()


    //define layout
    val customView = dialog.getCustomView()
    val tvTitle = customView.tv_title
    val tvMessage = customView.tv_message_confirmation
    val etPrice = customView.et_price_confirmation
    val btnTrue = customView.btn_continue_confirmation
    val btnFalse = customView.btn_cancel_confirmation

    if (!inputMode) {
        viewGone(etPrice)
    }

    tvTitle.text = title
    tvMessage.text = message
    btnTrue.text = btnTrueText
    btnFalse.text = btnFalseText

    btnTrue.setOnClickListener {
        trueButtonEvent(dialog)
    }

    btnFalse.setOnClickListener {
        falseButtonEvent(dialog)
    }
}

fun showAlert(
    context: Context,
    title: String? = null,
    message: String? = null,
    btnTrueEvent: (dialog: MaterialDialog) -> Unit

) {
    val dialog = MaterialDialog(context).customView(R.layout.layout_confirmation_price)
    dialog.view.setBackgroundColor(Color.TRANSPARENT)
    dialog.show()


    //define layout
    val customView = dialog.getCustomView()
    val tvTitle = customView.tv_title
    val tvMessage = customView.tv_message_confirmation
    val etPrice = customView.et_price_confirmation
    val btnTrue = customView.btn_continue_confirmation
    val btnFalse = customView.btn_cancel_confirmation

    viewGone(etPrice)
    viewGone(btnFalse)

    tvTitle.text = title
    tvMessage.text = message
    btnTrue.text = "Oke"

    btnTrue.setOnClickListener {
        btnTrueEvent(dialog)
    }
}



