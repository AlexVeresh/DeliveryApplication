package com.app.deliveryapplication.ui.search

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.PagingData
import com.app.deliveryapplication.repository.orders.OrdersRepository
import com.app.deliveryapplication.repository.restaurants.DishesRepository
import com.app.deliveryapplication.repository.search.SearchRepository
import com.app.deliveryapplication.vo.BasketItem
import com.app.deliveryapplication.vo.Dish
import com.app.deliveryapplication.vo.Product
import com.app.deliveryapplication.vo.SearchItem
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class SearchViewModel @ViewModelInject constructor(
    private val searchRepository: SearchRepository,
    private val ordersRepository: OrdersRepository,
    @Assisted val safeStateHandle: SavedStateHandle
):ViewModel(){
    companion object{
        const val KEY_SEARCHED = "searched"
        const val KEY_DISH = "dish_item"
        const val KEY_PRODUCT = "product_item"
        const val DEFAULT_SEARCH_REQUEST = ""
        const val PAGE_SIZE = 25
    }

    init {
        if(!safeStateHandle.contains(KEY_SEARCHED)){
            safeStateHandle.set(KEY_SEARCHED, DEFAULT_SEARCH_REQUEST)
        }

    }

    var searchAdapter: SearchAdapter? = null

    var chosenProduct: Product? = null
    var chosenDish: Dish? = null

    private var currentSearchResult: Flow<PagingData<SearchItem>>? = null



    fun searchedProducts(searchQuery: String): Flow<PagingData<SearchItem>>{
            val lastResult = currentSearchResult
            if (lastResult != null &&
                searchQuery == safeStateHandle.get(KEY_SEARCHED)) {
                return lastResult

            }
            return if (searchQuery.isNotEmpty()) {
                val result = searchRepository.searchProductsBySubstring(searchQuery, PAGE_SIZE)
                currentSearchResult = result
                result
            } else flowOf(PagingData.empty())
        }

    fun addToOrderList(searchItem: SearchItem){
        viewModelScope.launch {
            if (searchItem is SearchItem.ProductItem) {
                val product = searchItem.product
                val basketItem = ordersRepository.getBasketItemOrNull(product.id)
                if (basketItem == null) ordersRepository.insertBasketItem(
                    BasketItem(
                        product = product,
                        amount = 1,
                        basketItemId = product.id,
                        dish = null
                    )
                )
                else ordersRepository.updateBasketItem(
                    BasketItem(
                        product = basketItem.product,
                        amount = basketItem.amount?.plus(1),
                        basketItemId = basketItem.basketItemId,
                        dish = null
                    )
                )

            }
            else if(searchItem is SearchItem.DishItem){
                val dish = searchItem.dish
                val basketItem = ordersRepository.getBasketItemOrNull(dish.dish_id)
                if (basketItem == null) ordersRepository.insertBasketItem(
                    BasketItem(
                        product = null,
                        amount = 1,
                        basketItemId = dish.dish_id,
                        dish = dish
                    )
                )
                else ordersRepository.updateBasketItem(
                    BasketItem(
                        product = null,
                        amount = basketItem.amount?.plus(1),
                        basketItemId = basketItem.basketItemId,
                        dish = basketItem.dish
                    )
                )

            }
            }
        }


    fun getBasketItem(searchItem: SearchItem?)= flow<BasketItem?>{
        val task = viewModelScope.async {
            if(searchItem is SearchItem.ProductItem) ordersRepository.getBasketItemOrNull(searchItem.product.id)
            else ordersRepository.getBasketItemOrNull((searchItem as SearchItem.DishItem).dish.dish_id)
        }
        emit(task.await())
    }

    fun deleteBasketItem(searchItem: SearchItem?) = viewModelScope.launch {
        val basketItemId = if(searchItem is SearchItem.ProductItem) searchItem.product.id
        else (searchItem as SearchItem.DishItem).dish.dish_id
        val existedBasketItem = ordersRepository.getBasketItemOrNull(basketItemId)!!
        if (existedBasketItem.amount == 1) ordersRepository.deleteBasketItem(basketItemId)
        else ordersRepository.updateBasketItem(
            BasketItem(
                product = existedBasketItem.product,
                amount = existedBasketItem.amount?.minus(1),
                basketItemId = existedBasketItem.basketItemId,
                dish = existedBasketItem.dish
            )
        )

    }

    fun showSearchedResult(searchRequest: String){

        safeStateHandle.getLiveData<String>(KEY_SEARCHED).postValue(searchRequest)
        currentSearchResult = null

    }

    val searchItem = safeStateHandle.getLiveData<String>(KEY_SEARCHED)

}