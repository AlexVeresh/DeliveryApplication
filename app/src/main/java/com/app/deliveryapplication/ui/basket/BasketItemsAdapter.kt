package com.app.deliveryapplication.ui.basket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.deliveryapplication.R
import com.app.deliveryapplication.vo.BasketItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.basket_item.view.*
import kotlinx.android.synthetic.main.order_info_item.view.*


const val BASKET_ITEMS = R.layout.basket_item
const val ORDER_INFO = R.layout.order_info_item
class BasketItemsAdapter(
    private val viewModel: BasketViewModel,
    private val lifecycleOwner: LifecycleOwner
): ListAdapter<BasketItem, BasketItemViewHolder>(BASKET_ITEMS_COMPARATOR) {

    override fun getItemViewType(position: Int): Int {
        return when(position){
            itemCount-1 -> ORDER_INFO
            else -> BASKET_ITEMS
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketItemViewHolder {
        return BasketItemViewHolder.create(parent, viewType, viewModel,lifecycleOwner)
    }

    override fun onBindViewHolder(holder: BasketItemViewHolder, position: Int) {
        holder.bind(getItem(position),getItemViewType(position))
    }

    fun getItemAt(position: Int): BasketItem{
        return getItem(position)
    }
    companion object{
        val BASKET_ITEMS_COMPARATOR = object : DiffUtil.ItemCallback<BasketItem>(){

            override fun areItemsTheSame(oldItem: BasketItem, newItem: BasketItem): Boolean {
                return oldItem.basketItemId == newItem.basketItemId
            }

            override fun areContentsTheSame(oldItem: BasketItem, newItem: BasketItem): Boolean {
                return oldItem == newItem

            }
        }
    }

}

class BasketItemViewHolder(
    view: View,
    viewType: Int,
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: BasketViewModel
): RecyclerView.ViewHolder(view){

    private val basketItemImg = view.basket_item_img ?: null
    private val basketItemDescription = view.basket_item_description ?: null
    private val basketItemAdd = view.basket_item_add ?: null
    private val basketItemMinus = view.basket_item_minus ?: null
    private val basketItemAmount = view.basket_item_counter ?: null
    private val basketItemRemove = view.basket_item_remove_btn ?: null
    private val basketItemPrice = view.basket_item_price ?: null
    private val orderItemAddress = view.order_item_address ?: null
    private val orderItemPromo = view.order_item_promo ?: null
    private val allItemsPrice = view.all_items_price ?: null
    private val totalPrice = view.total_price ?: null
    private val orderItemAddressPane = view.order_item_address_pane ?: null
    private var basketItem: BasketItem? = null

    init {

        if(viewType == ORDER_INFO){
            orderItemAddressPane?.setOnClickListener {
                view.findNavController().navigate(R.id.action_basketFragment_to_addressFragment)
            }

            viewModel.totalPrice.observe(lifecycleOwner, Observer {

                val price = it ?: 0
                allItemsPrice?.text = itemView.resources.getString(R.string.price_placeholder, price.toString())
                totalPrice?.text = itemView.resources.getString(R.string.price_placeholder, price.toString())

            })

            orderItemAddress?.text = if(viewModel.currentAddress!=itemView.resources.getString(R.string.delivery_address))
                "${viewModel.currentAddress.split("//")[0]}, ${viewModel.currentAddress.split("//")[1]}"
                else itemView.resources.getString(R.string.delivery_address)
        }

    }

    fun bind(basketItem: BasketItem?, viewType: Int){
        if(viewType == BASKET_ITEMS) {
            this.basketItem = basketItem
            if (basketItem?.dish == null) {
                basketItemImg?.let {
                    Picasso.get()
                        .load("${basketItem?.product?.img_url}.jpg")
                        .fit()
                        .centerCrop()
                        // .placeholder(R.drawable.ic_about)
                        .into(it)
                }
                basketItemDescription?.text = basketItem?.product?.product_title
                basketItemAmount?.text = basketItem?.amount.toString()
                val price = basketItem?.product?.default_price?.toInt()?.times(basketItem.amount!!)
                basketItemPrice?.text =
                    itemView.resources.getString(R.string.price_placeholder, price.toString())
            } else {
                basketItemImg?.let {
                    Picasso.get()
                        .load("${basketItem.dish.dish_img_url}.jpg")
                        .fit()
                        .centerCrop()
                        // .placeholder(R.drawable.ic_about)
                        .into(it)
                }
                basketItemDescription?.text = basketItem.dish.dish_title
                basketItemAmount?.text = basketItem.amount.toString()
                val price = basketItem.dish.dish_price.times(basketItem.amount!!)
                basketItemPrice?.text =
                    itemView.resources.getString(R.string.price_placeholder, price.toString())
            }
            basketItemAdd?.setOnClickListener {
                viewModel.insertBasketItem(basketItem!!)
            }
            basketItemMinus?.setOnClickListener {
                viewModel.deleteBasketItem(basketItem!!)
            }
            basketItemRemove?.setOnClickListener {
                viewModel.removeBasketItemFromTheOrder(basketItem!!)
            }
        }

    }

    companion object{
        fun create(parent: ViewGroup, viewType: Int, viewModel: BasketViewModel, lifecycleOwner: LifecycleOwner): BasketItemViewHolder{
            val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
            return BasketItemViewHolder(view, viewType, lifecycleOwner, viewModel)
        }
    }


}