package com.app.deliveryapplication.repository.search

import androidx.paging.*
import com.app.deliveryapplication.api.RemoteStorageApi
import com.app.deliveryapplication.vo.SearchItem
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class SearchDataSource (
    private val remoteApi: RemoteStorageApi,
    private val searchQuery: String
) : PagingSource<Pair<String?, String?>, SearchItem>(){


    override suspend fun load(
        params: LoadParams<Pair<String?, String?>>
    ): LoadResult<Pair<String?, String?>, SearchItem> {

        return try {
            val searchData = remoteApi.loadSearchedData(
                    query = searchQuery,
                    limitSize = params.loadSize,
                    afterForRestaurants = params.key?.first,
                    afterForProducts = params.key?.second
                )
            val restaurantList = if (params.key!=null && params.key!!.first == null) emptyList() else searchData.first
            val productList = if (params.key!=null && params.key!!.second == null) emptyList() else searchData.second

            LoadResult.Page(
                data = restaurantList + productList,
                prevKey = null,
                nextKey = if(restaurantList.isEmpty() && productList.isEmpty()) null
                else Pair(
                    restaurantList.lastOrNull()?.restaurant?.restaurant_id,
                    productList.lastOrNull()?.product?.id
                )

            )


            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }

    override val keyReuseSupported: Boolean
        get() = true

//    override fun getRefreshKey(state: PagingState<Pair<String?, String?>, SearchItem>): Pair<String?, String?>? {
//
//        return state.anchorPosition?.let {
//            Pair((state.closestItemToPosition(it) as SearchItem.RestaurantItem).restaurant.restaurant_id,
//            (state.closestItemToPosition(it) as SearchItem.ProductItem).product.id)
//        }
//    }

}


