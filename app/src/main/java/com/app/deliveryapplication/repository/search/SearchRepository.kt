package com.app.deliveryapplication.repository.search

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.app.deliveryapplication.api.RemoteStorageApi
import com.app.deliveryapplication.repository.search.SearchDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val remoteStorage: RemoteStorageApi
) {


    fun searchProductsBySubstring(searchQuery: String, pageSize: Int) = Pager(
        config = PagingConfig(pageSize)
    ) {

        SearchDataSource(
            remoteApi = remoteStorage,
            searchQuery = searchQuery
        )

    }.flow

}