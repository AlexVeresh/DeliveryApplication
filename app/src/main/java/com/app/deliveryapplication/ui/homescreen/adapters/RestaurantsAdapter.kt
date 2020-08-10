package com.app.deliveryapplication.ui.homescreen.adapters

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.deliveryapplication.R
import com.app.deliveryapplication.ui.homescreen.viewmodels.HomeScreenViewModel
import com.app.deliveryapplication.vo.Product
import com.app.deliveryapplication.vo.Restaurant
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.product_item.view.*
import kotlinx.android.synthetic.main.promotions_list.view.*
import kotlinx.android.synthetic.main.rating_line.view.*
import kotlinx.android.synthetic.main.restaurant_item.view.*


private const val PROMOTIONS_LINE = R.layout.promotions_list
private const val PRODUCTS_BTN = R.layout.show_products_pane
private const val RESTAURANTS_LIST = R.layout.restaurant_item

class RestaurantsAdapter(
    private val viewModel: HomeScreenViewModel
): PagingDataAdapter<Restaurant, RecyclerView.ViewHolder>(RESTAURANTS_COMPARATOR){


    override fun getItemViewType(position: Int): Int {
        return when(position){
            0 -> PROMOTIONS_LINE
            1 -> PRODUCTS_BTN
            else -> RESTAURANTS_LIST
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == PRODUCTS_BTN) (holder as RestaurantViewHolder).bind(null, getItemViewType(position))
        if(getItemViewType(position) == RESTAURANTS_LIST) (holder as RestaurantViewHolder).bind(getItem(position-2), getItemViewType(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == PROMOTIONS_LINE) PromotionsLineViewHolder.create(parent, viewType)
        else RestaurantViewHolder.create(parent, viewType, viewModel)
    }


    companion object{
        val RESTAURANTS_COMPARATOR = object : DiffUtil.ItemCallback<Restaurant>(){
            override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
                return oldItem.restaurant_id == newItem.restaurant_id
            }

            override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
                return oldItem == newItem
            }
        }
    }


}

class RestaurantViewHolder(
    view: View,
    viewType: Int,
    private val viewModel: HomeScreenViewModel
): RecyclerView.ViewHolder(view){

    private val restaurantImg: ImageView? = view.restaurant_item_bg ?: null
    private val restaurantTitle: TextView? = view.restaurant_item_title ?: null
    private val restaurantRating: View? = view.restaurant_item_rating_bar ?: null
    private val restaurantPrice: TextView? = view.restaurant_item_price ?: null
    private var restaurant: Restaurant? = null

    init {
        when(viewType){
            PRODUCTS_BTN ->{
                view.setOnClickListener {
                    view.findNavController().navigate(R.id.action_restaurantsListFragment_to_categoriesFragment)
                }
            }
            RESTAURANTS_LIST-> {
                view.setOnClickListener {
                    viewModel.chosenRestaurantId.value = restaurant?.restaurant_id
                    view.findNavController().navigate(R.id.action_restaurantsListFragment_to_restaurantInformationFragment)
                }
            }
        }

    }

    fun bind(restaurant: Restaurant?, viewType: Int){
        if(viewType == RESTAURANTS_LIST){

            this.restaurant = restaurant
            restaurantImg?.let {
                Picasso.get()
                    .load("${restaurant?.restaurant_img_url}.jpg")
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.img_placeholder)
                    .into(it)
            }
            restaurantTitle?.text = restaurant?.restaurant_title
            restaurantPrice?.text = itemView.resources.getString(
                R.string.restaurant_price_holder,
                restaurant?.restaurant_price.toString()
            )
            restaurantRating?.findViewById<TextView>(R.id.restaurant_item_rating)?.text =
                restaurant?.restaurant_rating

        }
    }
    companion object{
        fun create(parent: ViewGroup, viewType: Int, viewModel: HomeScreenViewModel): RestaurantViewHolder{
                val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
            return RestaurantViewHolder(view, viewType, viewModel)
        }
    }

}

class PromotionsLineViewHolder(view: View): RecyclerView.ViewHolder(view){

    private val promotionsList: RecyclerView = view.promotions_list

    init {
        val itemDecoration = object: RecyclerView.ItemDecoration(){
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                val position = parent.getChildAdapterPosition(view)
                outRect.top = view.resources.getDimension(R.dimen.dimen_16dp).toInt()
                outRect.bottom = view.resources.getDimension(R.dimen.dimen_8dp).toInt()
                when(position){
                    0 -> {
                        outRect.right = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                        outRect.left = view.resources.getDimension(R.dimen.dimen_16dp).toInt()
                    }
                    parent.adapter?.itemCount?.minus(1) -> {
                        outRect.right = view.resources.getDimension(R.dimen.dimen_16dp).toInt()
                        outRect.left = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                    }
                    else -> {
                        outRect.right = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                        outRect.left = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                    }
                }
            }
        }
        promotionsList.addItemDecoration(itemDecoration)
        promotionsList.adapter = PromotionsAdapter()
    }

    companion object{
        fun create(parent: ViewGroup, viewType: Int): PromotionsLineViewHolder{
            val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
            return PromotionsLineViewHolder(view)
        }
    }

}