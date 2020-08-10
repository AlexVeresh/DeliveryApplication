package com.app.deliveryapplication.ui.homescreen.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.deliveryapplication.R
import com.app.deliveryapplication.ui.homescreen.viewmodels.HomeScreenViewModel
import com.app.deliveryapplication.vo.Product
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.product_item.view.*

class PromoProductsAdapter(
    private val viewModel: HomeScreenViewModel
) :ListAdapter<Product, PromoProductViewHolder>(PRODUCTS_COMPARATOR){

    override fun onBindViewHolder(holder: PromoProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoProductViewHolder {
        return  PromoProductViewHolder.create(parent,viewModel)

    }

    companion object{
        val PRODUCTS_COMPARATOR = object : DiffUtil.ItemCallback<Product>(){

            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem

            }

        }
    }
}
class PromoProductViewHolder(
    view: View,
    private val viewModel: HomeScreenViewModel
): RecyclerView.ViewHolder(view){
    private val productItemPane: CardView? = view.product_item_pane ?: null
    private val productItemImg: ImageView? = view.product_item_img ?: null
    private val productItemDescription: TextView? = view.product_item_description ?: null
    private val productItemAdd: View? = view.product_item_add ?: null
    private val productItemDefaultPrice: TextView? = view.product_item_default_price ?: null
    private val productItemCurrentPrice: TextView? = view.product_item_current_price ?: null
    private var product: Product? = null
    init {

        view.setOnClickListener {
            viewModel.chosenProductId.value = product?.id
            view.findNavController()
                .navigate(R.id.action_categoriesFragment_to_productInformationFragment)
        }
        productItemAdd?.setOnClickListener {
            product?.let { item -> viewModel.addProductToOrderList(item) }
        }

    }
    fun bind(product: Product?){
        this.product = product
        productItemPane?.layoutParams = ViewGroup.LayoutParams(itemView.resources.getDimension(R.dimen.item_width).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        productItemImg?.let {
            Picasso.get()
                .load("${product?.img_url}.jpg")
                .fit()
                .centerCrop()
                .placeholder(R.drawable.img_placeholder)
                .into(it)
        }
        productItemDescription?.text = product?.product_title
        productItemCurrentPrice?.text = product?.current_price
        productItemDefaultPrice?.text =
            itemView.resources.getString(R.string.price_placeholder, product?.default_price)
        productItemCurrentPrice?.apply { paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG }
        productItemCurrentPrice?.text = product?.current_price

    }

    companion object{
        fun create(parent: ViewGroup, viewModel: HomeScreenViewModel): PromoProductViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.product_item, parent, false)
            return PromoProductViewHolder(view, viewModel)
        }
    }

}