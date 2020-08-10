package com.app.deliveryapplication.ui.profile

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.app.deliveryapplication.repository.orders.OrdersRepository

class ProfileViewModel @ViewModelInject constructor(
    private val ordersRepository: OrdersRepository
):ViewModel(){

    val orders = ordersRepository.getAllOrders()


}