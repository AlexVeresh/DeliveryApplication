package com.app.deliveryapplication.ui.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.deliveryapplication.R
import com.app.deliveryapplication.ui.AppActivity
import com.app.deliveryapplication.vo.Product
import com.app.deliveryapplication.vo.SearchItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.product_item.view.*
import kotlinx.android.synthetic.main.search_item.view.*

const val RESTAURANT_FOUND = 0
const val PRODUCT_FOUND = 1
class SearchAdapter(
    private val viewModel: SearchViewModel,
    private val context: Context
): PagingDataAdapter<SearchItem, RecyclerView.ViewHolder>(SEARCH_ITEMS_COMPARATOR){



    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is SearchItem.RestaurantItem -> RESTAURANT_FOUND
            else -> PRODUCT_FOUND
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = getItem(position)!!){
            is SearchItem.RestaurantItem -> (holder as RestaurantViewHolder).bind(item)
            else -> (holder as ProductViewHolder).bind(item as SearchItem.ProductItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            RESTAURANT_FOUND -> RestaurantViewHolder.create(parent, viewModel, context)
            else -> ProductViewHolder.create(parent, viewModel, context)
        }
    }

    companion object {
        val SEARCH_ITEMS_COMPARATOR = object : DiffUtil.ItemCallback<SearchItem>(){

            override fun areItemsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
                return (oldItem is SearchItem.RestaurantItem && newItem is SearchItem.RestaurantItem &&
                        oldItem.restaurant.restaurant_id == newItem.restaurant.restaurant_id)
                        || (oldItem is SearchItem.ProductItem && newItem is SearchItem.ProductItem &&
                        oldItem.product.id == newItem.product.id)
            }

            override fun areContentsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class RestaurantViewHolder(
    view: View,
    private val viewModel: SearchViewModel,
    private val context: Context
) : RecyclerView.ViewHolder(view){

    private val restaurantItemImg = view.search_item_img ?: null
    private val restaurantItemTitle = view.search_item_title ?: null
    private val restaurantItemDescription = view.search_item_description ?: null
    private val restaurantDishes: RecyclerView? = view.search_item_dishes ?: null
    private var foundRestaurantItem: SearchItem.RestaurantItem? = null

    init {
        view.setOnClickListener {

        }
    }

    fun bind(foundRestaurantItem: SearchItem.RestaurantItem?){
        this.foundRestaurantItem = foundRestaurantItem
        restaurantItemImg?.let {
            Picasso.get()
                .load("${foundRestaurantItem?.restaurant?.restaurant_img_url}.jpg")
                .fit()
                .centerCrop()
                // .placeholder(R.drawable.ic_about)
                .into(it)
        }
        restaurantItemTitle?.text = foundRestaurantItem?.restaurant?.restaurant_title
        restaurantItemDescription?.text = foundRestaurantItem?.restaurant?.restaurant_description
        restaurantDishes?.adapter = FoundDishesAdapter(foundRestaurantItem?.dishList!!, viewModel, context)
    }

    companion object{
        fun create(parent: ViewGroup, viewModel: SearchViewModel, context: Context): RestaurantViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.search_item, parent, false)
            return RestaurantViewHolder(
                view,
                viewModel,
                context
            )
        }
    }

}
class ProductViewHolder(
    view: View,
    private val viewModel: SearchViewModel,
    private val context: Context

):RecyclerView.ViewHolder(view){

    private val productItemImg = view.search_item_img ?: null
    private val productItemTitle = view.search_item_title ?: null
    private val productItemDescription = view.search_item_description ?: null
    private var foundProductItem: SearchItem.ProductItem? = null

    init {
        view.setOnClickListener {
            viewModel.chosenProduct = foundProductItem?.product
            SearchItemInformationFragment().show((context as AppActivity).supportFragmentManager, "ItemFromSearchScreen")
        }
    }

    fun bind(foundProductItem: SearchItem.ProductItem?){
        this.foundProductItem = foundProductItem
        productItemImg?.let {
            Picasso.get()
                .load("${foundProductItem?.product?.img_url}.jpg")
                .fit()
                .centerCrop()
                // .placeholder(R.drawable.ic_about)
                .into(it)
        }
        productItemTitle?.text = foundProductItem?.product?.product_title
        productItemDescription?.text = foundProductItem?.product?.product_category

    }

    companion object{
        fun create(parent: ViewGroup, viewModel: SearchViewModel, context: Context): ProductViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.search_item, parent, false)
            return ProductViewHolder(
                view,
                viewModel,
                context
            )
        }
    }
}