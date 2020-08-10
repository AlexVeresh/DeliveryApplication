package com.app.deliveryapplication.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.deliveryapplication.R
import com.app.deliveryapplication.setUpAppBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.profile_fragment.*

@AndroidEntryPoint
class ProfileFragment: Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.setUpAppBar(resources.getString(R.string.profile), null)
        orders_history_pane.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_orderHistoryFragment)
        }
        my_address_pane.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_userAddressFragment)
        }
        payment_methods_pane.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_paymentWaysFragment)
        }
        special_pane.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_specialsFragment)
        }
        my_promo_pane.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_userPromoFragment)
        }

    }
}