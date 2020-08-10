package com.app.deliveryapplication.ui.homescreen.viewmodels

import android.os.Parcelable
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.app.deliveryapplication.repository.orders.OrdersRepository
import com.app.deliveryapplication.repository.products.ProductsRepository
import com.app.deliveryapplication.repository.restaurants.DishesRepository
import com.app.deliveryapplication.repository.restaurants.RestaurantsRepository
import com.app.deliveryapplication.ui.homescreen.adapters.*
import com.app.deliveryapplication.vo.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

const val DEFAULT_SUBCATEGORY = "Все"

class HomeScreenViewModel @ViewModelInject constructor(
    private val productsRepository: ProductsRepository,
    private val ordersRepository: OrdersRepository,
    private val restaurantsRepository: RestaurantsRepository,
    private val dishesRepository: DishesRepository,
    @Assisted private val safeStateHandle: SavedStateHandle
): ViewModel(){
    companion object{
        const val KEY_SUBCATEGORY = "subcategory"
        const val KEY_CATEGORY = "category"
        const val KEY_PRODUCT = "product"
        const val KEY_RESTAURANT = "restaurant"
        const val KEY_DISH = "dish"
        const val PAGE_SIZE = 29
    }

    init {
        if(!safeStateHandle.contains(KEY_SUBCATEGORY)){
            safeStateHandle.set(
                KEY_SUBCATEGORY,
                DEFAULT_SUBCATEGORY
            )
        }

    }


    var productSubcategoryClicked = false
    var adapterState: Parcelable? = null

    val chosenRestaurantId = safeStateHandle.getLiveData<String?>(KEY_RESTAURANT)
    val chosenProductId  = safeStateHandle.getLiveData<String?>(KEY_PRODUCT)
    val chosenDishId = safeStateHandle.getLiveData<String?>(KEY_DISH)

    val chosenCategory = safeStateHandle.getLiveData<String?>(KEY_CATEGORY)
    val chosenSubcategory = safeStateHandle.getLiveData<String>(KEY_SUBCATEGORY)

    var productsAdapter: ProductsAdapter? = null
    var categoriesAdapter: CategoriesAdapter? = null
    var restaurantsAdapter: RestaurantsAdapter? = null
    var restaurantInfoAdapter: RestaurantInfoAdapter? = null





    @OptIn(ExperimentalCoroutinesApi::class)
    val restaurants = restaurantsRepository.getRestaurants(PAGE_SIZE)

    fun productById(productId: String) = productsRepository.getProductById(productId)

    fun restaurantById(restaurantId: String) = restaurantsRepository.getRestaurantById(restaurantId)

    fun dishById(dishId: String) = dishesRepository.getDishById(dishId)

    suspend fun dishes(restaurantId: String) = dishesRepository.dishesById(restaurantId).transform {
        val list = listOf(Dish()) + it
        emit(list)
    }

    fun retryDishesCall(restaurantId: String) = viewModelScope.launch{ dishesRepository.loadData(restaurantId)}

    val promoProducts = productsRepository.getPromoProducts(PAGE_SIZE)

    val promoProductsForPreview = liveData{
        val items = productsRepository.promoProductsForPreview()
        emit(items)
    }

    fun products(category: String, subcategory: String?) =
        productsRepository.getProducts(
            category = category,
            pageSize = PAGE_SIZE,
            subcategory = when(subcategory){
                DEFAULT_SUBCATEGORY -> null
                else -> subcategory
            }
        )


    fun addProductToOrderList(product: Product){
        viewModelScope.launch {
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
    }

    fun addDishToOrderList(dish: Dish){
        viewModelScope.launch {
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
                    product = basketItem.product,
                    amount = basketItem.amount?.plus(1),
                    basketItemId = basketItem.basketItemId,
                    dish = null
                )
            )

        }
    }

    fun getBasketItem(id: String?)= flow<BasketItem?>{
        val task = viewModelScope.async {
            ordersRepository.getBasketItemOrNull(id!!)
        }
        emit(task.await())
    }

    fun deleteBasketItem(basketItemId: String) = viewModelScope.launch {
        val existedBasketItem = ordersRepository.getBasketItemOrNull(basketItemId)
        if (existedBasketItem?.amount == 1) ordersRepository.deleteBasketItem(basketItemId)
        else ordersRepository.updateBasketItem(
            BasketItem(
                product = existedBasketItem!!.product,
                amount = existedBasketItem.amount?.minus(1),
                basketItemId = existedBasketItem.basketItemId,
                dish = null
            )
        )

    }
}
