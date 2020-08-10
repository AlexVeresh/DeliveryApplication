package com.app.deliveryapplication.ui.basket

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.media.MediaRouter
import android.media.browse.MediaBrowser
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.app.deliveryapplication.R
import com.app.deliveryapplication.setUpAppBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.app_bar.view.*
import kotlinx.android.synthetic.main.basket_fragment.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BasketFragment: Fragment(){

    private lateinit var basketItemsAdapter: BasketItemsAdapter
    private val basketViewModel: BasketViewModel by activityViewModels()

    private lateinit var lifecycleOwner: LifecycleOwner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleOwner = this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.basket_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.setUpAppBar(resources.getString(R.string.basket), null)
        if(basketViewModel.basketItemAdapter == null){
            basketViewModel.basketItemAdapter = initAdapter()
        }
        basketItemsAdapter = basketViewModel.basketItemAdapter!!
        basket_items_pane.adapter = basketItemsAdapter
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT)
        {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }


            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if(viewHolder.absoluteAdapterPosition!=basketItemsAdapter.itemCount-1)
                basketViewModel.removeBasketItemFromTheOrder(basketItemsAdapter.getItemAt(viewHolder.absoluteAdapterPosition))
            }

        }).attachToRecyclerView(basket_items_pane)
        initMakeOrderBtn()
        initDeleteBtn()

    }

    private fun initAdapter():BasketItemsAdapter{
        val basketItemsAdapter = BasketItemsAdapter(basketViewModel, lifecycleOwner)

        lifecycleScope.launchWhenCreated {
            basketViewModel.basketItems.collectLatest {
                basketItemsAdapter.submitList(it)
            }
        }

        return basketItemsAdapter
    }
    private fun initMakeOrderBtn(){
        basketViewModel.totalPrice.observe(viewLifecycleOwner, Observer {
            val price = it ?: 0
            confirm_btn_title.text = view?.resources?.getString(R.string.make_order_btn_title, price.toString())
        })
        make_order_pane.setOnClickListener {
            basketViewModel.createOrder(basketItemsAdapter.currentList)
        }
        basketViewModel.currentListCount.observe(viewLifecycleOwner, Observer {
            if(it == 0) {
                make_order_pane.visibility = View.GONE
                this.setUpAppBar(resources.getString(R.string.basket), null)
                empty_pane_title.visibility = View.VISIBLE
            }
            else{
                this.setUpAppBar(resources.getString(R.string.basket), R.drawable.ic_bucket)
                empty_pane_title.visibility = View.GONE
            }
        })
    }

    private fun initDeleteBtn(){
        val view = this.activity?.findViewById<View>(R.id.app_bar)
        view?.fragment_icon?.setOnClickListener {
            if(basketItemsAdapter.currentList.isNotEmpty()) {
                AlertDialog.Builder(requireActivity())
                    .setTitle(resources.getString(R.string.alert_dialog_title))
                    .setPositiveButton(
                        "Подтвердить",
                        DialogInterface.OnClickListener { dialogInterface, _ ->
                            basketViewModel.deleteAllBasketItems()
                        })
                    .setNegativeButton(
                        "Отмена",
                        DialogInterface.OnClickListener { dialogInterface, _ -> dialogInterface.dismiss() })
                    .create()
                    .show()
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val view = this.activity?.findViewById<View>(R.id.app_bar)
        view?.fragment_icon?.setOnClickListener(null)
    }
}