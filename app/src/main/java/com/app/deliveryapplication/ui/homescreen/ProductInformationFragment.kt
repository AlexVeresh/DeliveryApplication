package com.app.deliveryapplication.ui.homescreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.app.deliveryapplication.R
import com.app.deliveryapplication.setUpAppBar
import com.app.deliveryapplication.ui.homescreen.viewmodels.HomeScreenViewModel
import com.app.deliveryapplication.vo.BasketItem
import com.app.deliveryapplication.vo.Product
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.adding_to_basket_item.view.*
import kotlinx.android.synthetic.main.product_info_fragment.*
import kotlinx.coroutines.flow.collectLatest

class ProductInformationFragment : Fragment(){

    private val productInfoViewModel: HomeScreenViewModel by activityViewModels()

    private lateinit var product: Product

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.product_info_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenCreated {
            productInfoViewModel.productById(productInfoViewModel.chosenProductId.value!!).collectLatest {
                product = it
                setUpProductInfo()
            }
        }

    }

    private fun setUpProductInfo(){
        this.setUpAppBar(product.product_category!!, R.drawable.ic_shop)
        Picasso.get()
            .load("${product.img_url}.jpg")
            .fit()
            .centerCrop()
            .into(product_img)
        product_title.text = product.product_title
        product_price.text = this.resources.getString (R.string.currency, product.default_price)
        add_to_basket_btn.setOnClickListener {
            productInfoViewModel.addProductToOrderList(product)
            setUpPane(BasketItem(product.id, product, null,1))
        }
        add_item_pane.add_basket_element.setOnClickListener {
            productInfoViewModel.addProductToOrderList(product)
            val amount = add_item_pane.products_counter.text.dropLast(2).toString().toInt()
            add_item_pane.products_counter.text = "${amount+1}шт"
        }
        add_item_pane.remove_basket_element.setOnClickListener {
            productInfoViewModel.deleteBasketItem(product.id)
            val amount = add_item_pane.products_counter.text.dropLast(2).toString().toInt()
            if(amount == 1){
                setUpPane(null)
            }
            else {
                add_item_pane.products_counter.text = "${amount - 1}шт"
            }
        }
        lifecycleScope.launchWhenCreated {
            productInfoViewModel.getBasketItem(product.id).collectLatest { basketItem ->
                setUpPane(basketItem)
            }
        }
    }


    private fun setUpPane(basketItem: BasketItem?){
        if(basketItem != null){
            add_to_basket_btn.visibility = View.INVISIBLE
            add_item_pane.visibility = View.VISIBLE
            add_item_pane.products_counter.text = "${basketItem.amount.toString()}шт"
        }
        else{
            add_to_basket_btn.visibility = View.VISIBLE
            add_item_pane.visibility = View.INVISIBLE
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        productInfoViewModel.chosenProductId.postValue(null)
    }

}

