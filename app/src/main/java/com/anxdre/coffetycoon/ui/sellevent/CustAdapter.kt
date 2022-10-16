package com.anxdre.coffetycoon.ui.sellevent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.anxdre.coffetycoon.R
import com.anxdre.coffetycoon.data.Customer
import kotlinx.android.synthetic.main.layout_item_customer.view.*

class CustAdapter() : RecyclerView.Adapter<CustAdapter.CustViewHolder>() {
    private val listOfCustomer = mutableListOf<Customer>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustViewHolder =
        CustViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_customer, parent, false)
        )

    override fun onBindViewHolder(holder: CustViewHolder, position: Int) =
        holder.bindView(listOfCustomer[position])

    override fun getItemCount(): Int = listOfCustomer.size

    fun addCustomer(customer: Customer) {
        listOfCustomer.add(customer)
        notifyDataSetChanged()
    }

    fun getCustomer() = listOfCustomer

    inner class CustViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(customer: Customer) {
            itemView.tv_buy_name.text = customer.name
            itemView.tv_buy_quantity.text = "${customer.itemQuantity} Pcs"
            itemView.tv_buy_status.apply {
                if (customer.served) {
                    text = "Served"
                    setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.green))
                    itemView.iv_customer.setImageDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.ic_emoji_happy
                        )
                    )
                } else {
                    text = "Not served"
                    setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.danger))
                    itemView.iv_customer.setImageDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.ic_emoji_sad
                        )
                    )
                }
            }
        }
    }
}

