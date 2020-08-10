package com.app.deliveryapplication.ui.search


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.app.deliveryapplication.R
import com.app.deliveryapplication.vo.SearchItem
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.picasso.transformations.MaskTransformation
import kotlinx.android.synthetic.main.adding_to_basket_item.view.*
import kotlinx.android.synthetic.main.item_info_fragment.*
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class SearchItemInformationFragment : BottomSheetDialogFragment(){


    private val searchViewModel: SearchViewModel by activityViewModels()

    private lateinit var searchItem: SearchItem

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.item_info_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        styleBottomSheet()
        if(searchViewModel.chosenProduct!=null){
            val product = searchViewModel.chosenProduct!!
            Picasso.get()
                .load("${product.img_url}.jpg")
                .fit()
                .centerCrop()
                .transform(MaskTransformation(context, R.drawable.bottom_sheet_rounded_shape))
                .into(view.findViewById<ImageView>(R.id.item_info_img))
            item_info_description.text = product.product_title
            item_info_price.text = view.resources.getString(R.string.price_placeholder, product.default_price)
            item_info_weight.visibility = View.GONE
            item_info_title.visibility = View.GONE
            searchItem = SearchItem.ProductItem(product)
            setUpListeners()

        }
        else if(searchViewModel.chosenDish!=null){
            val dish = searchViewModel.chosenDish!!
            Picasso.get()
                .load("${dish.dish_img_url}.jpg")
                .fit()
                .centerCrop()
                .transform(MaskTransformation(context, R.drawable.bottom_sheet_rounded_shape))
                .into(view.findViewById<ImageView>(R.id.item_info_img))
            item_info_title.text = dish.dish_title
            item_info_description.text = dish.dish_description
            item_info_weight.text = view.resources.getString(R.string.weight_placeholder, dish.dish_weight.toString())
            item_info_price.text = view.resources.getString(R.string.price_placeholder, dish.dish_price.toString())
            searchItem = SearchItem.DishItem(searchViewModel.chosenDish!!)
            setUpListeners()
        }
        else{
            this.dismiss()
        }


    }

    private fun setUpListeners(){
        add_to_basket_search_btn.setOnClickListener {
            searchViewModel.addToOrderList(searchItem)
            setUpPane(1)


        }
        add_item_search_pane.add_basket_element.setOnClickListener {
            searchViewModel.addToOrderList(searchItem)
            val amount = add_item_search_pane.products_counter.text.dropLast(2).toString().toInt()
            add_item_search_pane.products_counter.text = "${amount+1}шт"

        }
        add_item_search_pane.remove_basket_element.setOnClickListener {
            searchViewModel.deleteBasketItem(searchItem)
            val amount = add_item_search_pane.products_counter.text.dropLast(2).toString().toInt()
            if(amount == 1){
                setUpPane(null)
            }
            else {
                add_item_search_pane.products_counter.text = "${amount - 1}шт"
            }

        }
        lifecycleScope.launchWhenCreated {
            searchViewModel.getBasketItem(searchItem).collectLatest { basketItem ->
                setUpPane(basketItem?.amount)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        searchViewModel.chosenDish = null
        searchViewModel.chosenProduct = null
    }

    private fun setUpPane(basketAmount: Int?){
        if(basketAmount != null){
            add_to_basket_search_btn.visibility = View.INVISIBLE
            add_item_search_pane.visibility = View.VISIBLE
            add_item_search_pane.products_counter.text = "${basketAmount}шт"
        }
        else{
            add_to_basket_search_btn.visibility = View.VISIBLE
            add_item_search_pane.visibility = View.INVISIBLE
        }

    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    private fun styleBottomSheet(){
        dialog?.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(R.id.bottom_sheet)
            bottomSheet?.let { sheet ->
                dialog.behavior.peekHeight = sheet.height
                sheet.parent.parent.requestLayout()
            }
        }
    }
}