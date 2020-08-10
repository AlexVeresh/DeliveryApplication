package com.app.deliveryapplication.ui.homescreen.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.deliveryapplication.R
import com.app.deliveryapplication.ui.AppActivity
import com.app.deliveryapplication.ui.homescreen.DishItemInformationFragment
import com.app.deliveryapplication.ui.homescreen.viewmodels.HomeScreenViewModel
import com.app.deliveryapplication.ui.search.SearchItemInformationFragment
import com.app.deliveryapplication.vo.Dish
import com.app.deliveryapplication.vo.Restaurant
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dish_item.view.*
import kotlinx.android.synthetic.main.restaurant_information_item.view.*

const val RESTAURANT_INFO = R.layout.restaurant_information_item
const val DISH_ITEM = R.layout.dish_item

class RestaurantInfoAdapter(
    private val viewModel: HomeScreenViewModel,
    private val context: Context,
    private val restaurant: Restaurant
): ListAdapter<Dish, RecyclerView.ViewHolder>(DISHES_COMPARATOR){

    override fun getItemViewType(position: Int): Int {
        return when(position){
            0 -> RESTAURANT_INFO
            else -> DISH_ITEM
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            RESTAURANT_INFO -> RestaurantInfoViewHolder.create(parent, restaurant)
            else -> DishesViewHolder.create(parent, viewModel, context)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return when(getItemViewType(position)){
            RESTAURANT_INFO -> (holder as RestaurantInfoViewHolder).bind()
            else -> (holder as DishesViewHolder).bind(getItem(position))
        }
    }
    companion object{
        val DISHES_COMPARATOR = object: DiffUtil.ItemCallback<Dish>(){

            override fun areItemsTheSame(oldItem: Dish, newItem: Dish): Boolean {
                return oldItem.dish_id == newItem.dish_id
            }

            override fun areContentsTheSame(oldItem: Dish, newItem: Dish): Boolean {
                return oldItem == newItem

            }
        }
    }


}

class RestaurantInfoViewHolder(
    view: View,
    private val restaurant: Restaurant
): RecyclerView.ViewHolder(view){

    private val restaurantTitle = view.restaurant_title ?: null
    private val restaurantDescription = view.restaurant_description ?: null
    private val restaurantRating = view.restaurant_item_rating ?: null
    private val restaurantDeliveryTime = view.restaurant_delivery_time ?: null
    private val restaurantPrice = view.restaurant_price ?: null

    init {

    }

    fun bind(){
        restaurantTitle?.text = restaurant.restaurant_title
        restaurantDescription?.text = restaurant.restaurant_description
        restaurantRating?.text = restaurant.restaurant_rating
        restaurantDeliveryTime?.text = itemView.resources.getString(R.string.delivery_time_placeholder, restaurant.restaurant_delivery_time)
        restaurantPrice?.text = itemView.resources.getString(R.string.restaurant_price_holder, restaurant.restaurant_price.toString())
    }

    companion object{
        fun create(parent: ViewGroup, restaurant: Restaurant):RestaurantInfoViewHolder{
            val view = LayoutInflater.from(parent.context).inflate(RESTAURANT_INFO, parent, false)
            return RestaurantInfoViewHolder(view, restaurant)
        }
    }

}
class DishesViewHolder(
    view: View,
    private val viewModel: HomeScreenViewModel,
    private val context: Context
): RecyclerView.ViewHolder(view){

    private val dishImg = view.dish_img ?: null
    private val dishTitle = view.dish_title ?: null
    private val dishAdd = view.dish_add ?: null
    private val dishPrice = view.dish_price ?: null
    private val dishWeight = view.dish_weight ?: null
    private var dish: Dish? = null

    init {
        view.setOnClickListener {
            viewModel.chosenDishId.postValue(dish?.dish_id)
            DishItemInformationFragment().show((context as AppActivity).supportFragmentManager, "ItemFromHomeScreen")

        }
        dishAdd?.setOnClickListener {
            viewModel.addDishToOrderList(dish!!)
        }
    }

    fun bind(dish: Dish?){
        this.dish = dish
        if(dish == null){
            dishTitle?.text = "1111"
        }
        else {
            dishImg?.let {
                Picasso.get()
                    .load("${dish?.dish_img_url}.jpg")
                    .fit()
                    .centerCrop()
                    // .placeholder(R.drawable.ic_about)
                    .into(it)
            }
            dishTitle?.text = dish?.dish_title
            dishPrice?.text = itemView.resources.getString(
                R.string.price_placeholder,
                dish?.dish_price?.toString()
            )
            dishWeight?.text = itemView.resources.getString(
                R.string.weight_placeholder,
                dish?.dish_weight?.toString()
            )
        }
    }


    companion object{
        fun create(parent: ViewGroup, viewModel: HomeScreenViewModel, context: Context):DishesViewHolder{
            val view = LayoutInflater.from(parent.context).inflate(DISH_ITEM, parent, false)
            return DishesViewHolder(view, viewModel, context)
        }
    }


}