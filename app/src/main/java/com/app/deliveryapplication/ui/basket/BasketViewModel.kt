package com.app.deliveryapplication.ui.basket

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.app.deliveryapplication.repository.orders.OrdersRepository
import com.app.deliveryapplication.vo.BasketItem
import com.app.deliveryapplication.vo.Order
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BasketViewModel @ViewModelInject constructor(
    private val ordersRepository: OrdersRepository,
    @Assisted private val safeStateHandle: SavedStateHandle
): ViewModel() {

    val totalPrice = MutableLiveData<Int>()

    val currentListCount = MutableLiveData<Int>()

    var currentAddress: String = "Адрес доставки"

    var basketItemAdapter:BasketItemsAdapter? = null

    val basketItems = ordersRepository.getAllBasketItems().map{
        totalPrice.value = 0
        it.forEach {basketItem ->
            val item = if (basketItem.dish == null) basketItem.product?.current_price?.toInt()!! else basketItem.dish.dish_price
            totalPrice.value = totalPrice.value?.plus(basketItem.amount?.times(item)!!)

        }
        currentListCount.value = it.size
        if(it.isNotEmpty()) it + listOf(BasketItem()) else {totalPrice.value = 0; it}

    }

    fun insertBasketItem(basketItem: BasketItem){
        viewModelScope.launch {
            val existedBasketItem = ordersRepository.getBasketItemOrNull(basketItem.basketItemId)!!
            ordersRepository.updateBasketItem(
                BasketItem(
                    product = existedBasketItem.product,
                    amount = existedBasketItem.amount?.plus(1),
                    basketItemId = existedBasketItem.basketItemId,
                    dish = existedBasketItem.dish
                )
            )

        }
    }

    fun deleteBasketItem(basketItem: BasketItem) = viewModelScope.launch {
        val existedBasketItem = ordersRepository.getBasketItemOrNull(basketItem.basketItemId)!!
        if (existedBasketItem.amount == 1) ordersRepository.deleteBasketItem(basketItem.basketItemId)
        ordersRepository.updateBasketItem(
            BasketItem(
                product = existedBasketItem.product,
                amount = existedBasketItem.amount?.minus(1),
                basketItemId = existedBasketItem.basketItemId,
                dish = existedBasketItem.dish
            )
        )

    }

    fun removeBasketItemFromTheOrder(basketItem: BasketItem) = viewModelScope.launch {
        ordersRepository.deleteBasketItem(basketItem.basketItemId)
    }

    fun deleteAllBasketItems() = viewModelScope.launch { ordersRepository.deleteAllBasketItems() }

    fun insertBasketItems(basketItems: List<BasketItem>) = viewModelScope.launch { ordersRepository.insertBasketItems(basketItems) }

    fun createOrder(basketList: List<BasketItem>){
        viewModelScope.launch {
                val data = Calendar.getInstance().time
                val month = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())!!
                val sdf = SimpleDateFormat(" hh:mm dd/yyyy", Locale.ROOT)
                val order = Order(
                    address = currentAddress,
                    promoCode = null,
                    basketItemList = basketList.filter { it.basketItemId != "" },
                    orderTime = sdf.format(data).replace("/", " $month "),
                    totalPrice = totalPrice.value
                )
                ordersRepository.makeOrder(order)
                ordersRepository.deleteAllBasketItems()
            }
    }

    val orders = ordersRepository.getAllOrders()


}