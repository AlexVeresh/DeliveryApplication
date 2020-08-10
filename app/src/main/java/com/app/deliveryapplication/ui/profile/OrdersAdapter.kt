package com.app.deliveryapplication.ui.profile

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.deliveryapplication.R
import com.app.deliveryapplication.ui.basket.BasketViewModel
import com.app.deliveryapplication.vo.Order
import kotlinx.android.synthetic.main.order_item.view.*


class OrdersAdapter(
    private val viewModel: BasketViewModel,
    private val context: Context
): ListAdapter<Order, OrderViewHolder>(ORDERS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        return OrderViewHolder.create(parent, viewModel, context)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object{
        val ORDERS_COMPARATOR = object : DiffUtil.ItemCallback<Order>(){
            override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
                return oldItem.orderId == newItem.orderId
            }

            override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class OrderViewHolder(
    view: View,
    viewModel: BasketViewModel,
    context: Context
): RecyclerView.ViewHolder(view){

    private val orderItemDate = view.order_item_date
    private val orderItemNumber = view.order_item_number
    private val orderItemPrice = view.order_item_price
    private var order: Order? = null

    init {
        view.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle(itemView.resources.getString(R.string.alert_orders_dialog_title))
                .setPositiveButton(
                    "Подтвердить",
                    DialogInterface.OnClickListener { dialogInterface, _ ->
                        viewModel.insertBasketItems(order?.basketItemList!!)
                        viewModel.totalPrice.value = order?.totalPrice
                        viewModel.currentAddress = order?.address!!
                    })
                .setNegativeButton(
                    "Отмена",
                    DialogInterface.OnClickListener { dialogInterface, _ -> dialogInterface.dismiss() })
                .create()
                .show()

        }
    }

    fun bind(order: Order?){
        this.order = order
        orderItemDate.text = order?.orderTime
        orderItemNumber.text = itemView.resources.getString(R.string.order_number_title, order?.orderId.toString())
        orderItemPrice.text = itemView.resources.getString(R.string.price_placeholder, order?.totalPrice.toString())
    }


    companion object{
        fun create(parent: ViewGroup, viewModel: BasketViewModel, context: Context):OrderViewHolder{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
            return OrderViewHolder(view, viewModel, context)
        }
    }
}