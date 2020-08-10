package com.app.deliveryapplication.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.app.deliveryapplication.R
import com.app.deliveryapplication.ui.basket.BasketViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.order_history_fragment.*
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class OrderHistoryFragment :Fragment(){


    private val basketViewModel: BasketViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.order_history_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        order_items_pane.adapter = initAdapter()
    }

    private fun initAdapter():OrdersAdapter{
        val ordersAdapter = OrdersAdapter(basketViewModel, requireActivity())
        lifecycleScope.launchWhenCreated {
            basketViewModel.orders.collectLatest {
                ordersAdapter.submitList(it)
            }
        }
        return ordersAdapter
    }

}