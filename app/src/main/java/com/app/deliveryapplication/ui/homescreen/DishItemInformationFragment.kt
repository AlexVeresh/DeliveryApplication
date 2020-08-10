package com.app.deliveryapplication.ui.homescreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.app.deliveryapplication.R
import com.app.deliveryapplication.ui.homescreen.viewmodels.HomeScreenViewModel
import com.app.deliveryapplication.vo.Dish
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.picasso.transformations.MaskTransformation
import kotlinx.android.synthetic.main.adding_to_basket_item.view.*
import kotlinx.android.synthetic.main.item_info_fragment.*
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class DishItemInformationFragment : BottomSheetDialogFragment(){


    private val dishViewModel: HomeScreenViewModel by activityViewModels()

    private lateinit var dish: Dish

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
        lifecycleScope.launchWhenCreated {
            dishViewModel.dishById(
                dishViewModel.chosenDishId.value!!
            ).collectLatest {
                dish = it
                setUpDishInfo()
            }
        }
    }


    private fun setUpDishInfo(){
        Picasso.get()
            .load("${dish.dish_img_url}.jpg")
            .fit()
            .centerCrop()
            .transform(MaskTransformation(context, R.drawable.bottom_sheet_rounded_shape))
            .into(view?.findViewById<ImageView>(R.id.item_info_img))
        item_info_title.text = dish.dish_title
        item_info_description.text = dish.dish_description
        item_info_weight.text = view?.resources?.getString(R.string.weight_placeholder, dish.dish_weight.toString())
        item_info_price.text = view?.resources?.getString(R.string.price_placeholder, dish.dish_price.toString())

        add_to_basket_search_btn.setOnClickListener {
            dishViewModel.addDishToOrderList(dish)
            setUpPane(1)
        }

        add_item_search_pane.add_basket_element.setOnClickListener {
            dishViewModel.addDishToOrderList(dish)
            val amount = add_item_search_pane.products_counter.text.dropLast(2).toString().toInt()
            add_item_search_pane.products_counter.text = "${amount+1}шт"

        }
        add_item_search_pane.remove_basket_element.setOnClickListener {
            dishViewModel.deleteBasketItem(dish.dish_id)
            val amount = add_item_search_pane.products_counter.text.dropLast(2).toString().toInt()
            if(amount == 1){
                setUpPane(null)
            }
            else {
                add_item_search_pane.products_counter.text = "${amount - 1}шт"
            }

        }
        lifecycleScope.launchWhenCreated {
            dishViewModel.getBasketItem(dish.dish_id).collectLatest { basketItem ->
                setUpPane(basketItem?.amount)
            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        dishViewModel.chosenDishId.postValue(null)

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