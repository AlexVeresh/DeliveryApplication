package com.app.deliveryapplication.vo

sealed class SearchItem {
    data class ProductItem(val product: Product) : SearchItem()
    data class RestaurantItem(val restaurant: Restaurant, val dishList: List<Dish>?) : SearchItem()
    data class DishItem(val dish: Dish): SearchItem()
}

