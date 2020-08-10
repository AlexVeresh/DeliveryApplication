package com.app.deliveryapplication.api

import android.util.Log
import androidx.lifecycle.Transformations.map
import com.app.deliveryapplication.vo.Dish
import com.app.deliveryapplication.vo.Product
import com.app.deliveryapplication.vo.Restaurant
import com.app.deliveryapplication.vo.SearchItem
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.mongodb.client.model.Filters
import com.mongodb.stitch.android.core.StitchAppClient
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential
import kotlinx.coroutines.*
import org.bson.Document
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import java.io.IOException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.system.measureTimeMillis

@Singleton
class RemoteStorageApi @Inject constructor(
    private val appClient: StitchAppClient
) {


    suspend fun loadProducts(
        collection: String,
        limitSize: Int,
        after: String?,
        category: String?,
        subcategory: String?

    ):List<Product> {
        val remoteClient: RemoteMongoClient =
            appClient.getServiceClient(
                RemoteMongoClient.factory,
                "mongodb-atlas"
            )
        val remoteCollection: RemoteMongoCollection<*> =
        remoteClient.getDatabase("FoodManDB").getCollection(collection)


        val filters = ArrayList<Document>()

        if(category != null){
            filters.add(Document().append("product_category", Document().append("\$eq", category)))
        }
        if(after!=null){
            filters.add(Document().append("_id", Document().append("\$gt", ObjectId(after))))
        }
        if(subcategory!=null){
            filters.add(Document().append("product_subcategory", Document().append("\$eq", subcategory)))
        }

        val filterDocument = Document()
        if(filters.isNotEmpty()) filterDocument.append("\$and", filters)
        val remoteFindIterable: RemoteFindIterable<*> =
        remoteCollection
            .find(filterDocument)
            .limit(limitSize)

        val foundItems = remoteFindIterable.into(ArrayList<Any>())

        return foundItems.await().result.map { convertDocumentToProduct(it as Document) }

    }

    suspend fun loadPromoProducts(
        limitSize: Int,
        after: String?
    ): List<Product>{
        val remoteClient: RemoteMongoClient =
            appClient.getServiceClient(
                RemoteMongoClient.factory,
                "mongodb-atlas"
            )
        val remoteCollection: RemoteMongoCollection<*> =
            remoteClient.getDatabase("FoodManDB").getCollection("Products")

        val filters = ArrayList<Bson>()

        filters.add(Document().append("isPromoted", Document().append("\$eq", true)))

        if(after!=null){
            filters.add(Document().append("_id", Document().append("\$gt", ObjectId(after))))
        }

        val remoteFindIterable: RemoteFindIterable<*> =
            remoteCollection
                .find(Document().append("\$and", filters))
                .limit(limitSize)

        val foundPromoItems = remoteFindIterable.into(ArrayList<Any>())

        return foundPromoItems.await().result.map { convertDocumentToProduct(it as Document) }

    }


    suspend fun loadRestaurants(
        limitSize: Int,
        after: String?,
        collection: String
    ):List<Restaurant>{
        val remoteClient: RemoteMongoClient =
            appClient.getServiceClient(
                RemoteMongoClient.factory,
                "mongodb-atlas"
            )
        val remoteCollection: RemoteMongoCollection<*> =
            remoteClient.getDatabase("FoodManDB").getCollection(collection)


        val filterDocument = Document()
        if(after!=null){
            filterDocument.append("_id", Document().append("\$gt", ObjectId(after)))
        }

        val remoteFindIterable: RemoteFindIterable<*> =
        remoteCollection
            .find(filterDocument)
            .limit(limitSize)

        val foundItems = remoteFindIterable.into(ArrayList<Any>())

        return foundItems.await().result.map { it -> convertDocumentToRestaurant(it as Document) }
    }

    suspend fun loadDishesById(
        restaurant_id: String,
        collection: String
    ):List<Dish>{
        val remoteClient: RemoteMongoClient =
            appClient.getServiceClient(
                RemoteMongoClient.factory,
                "mongodb-atlas"
            )
        val remoteCollection: RemoteMongoCollection<*> =
            remoteClient.getDatabase("FoodManDB").getCollection(collection)


        val filterDocument = Document().append("restaurant_owner_id", Document().append("\$eq", restaurant_id))
        val remoteFindIterable: RemoteFindIterable<*> =
            remoteCollection
                .find(filterDocument)


        val foundItems = remoteFindIterable.into(ArrayList<Any>())

        return foundItems.await().result.map { it -> convertDocumentToDish(it as Document) }

    }


    suspend fun loadSearchedData(
        query: String,
        limitSize: Int,
        afterForRestaurants:String?,
        afterForProducts:String?
    ): Pair<List<SearchItem.RestaurantItem>, List<SearchItem.ProductItem>> {

        val searchQuery = query.replace("(", "\\(")
            .replace(")", "\\)")

       
        val remoteClient: RemoteMongoClient =
            appClient.getServiceClient(
                RemoteMongoClient.factory,
                "mongodb-atlas"
            )
        val restaurantCollection: RemoteMongoCollection<*> =
            remoteClient.getDatabase("FoodManDB").getCollection("Restaurants")

        val productsCollection: RemoteMongoCollection<*> =
            remoteClient.getDatabase("FoodManDB").getCollection("Products")

        val restaurantsFilters = ArrayList<Document>()
        val productsFilters = ArrayList<Document>()


        if(afterForRestaurants != null)
            restaurantsFilters.add(Document().append("_id", Document().append("\$gt", ObjectId(afterForRestaurants))))

        if(afterForProducts!=null)
            productsFilters.add(Document().append("_id", Document().append("\$gt", ObjectId(afterForProducts))))

        val productSearchList = listOf<Document>(
            Document().append("product_title", Document().append("\$regex", searchQuery)),
            Document().append("product_category", Document().append("\$regex", searchQuery)),
            Document().append("product_subcategory", Document().append("\$regex", searchQuery)),
            Document().append("current_price", Document().append("\$regex", searchQuery)),
            Document().append("default_price", Document().append("\$regex", searchQuery)))

        val restaurantSearchList = listOf<Document>(
            Document().append("restaurant_title", Document().append("\$regex", searchQuery)),
            Document().append("restaurant_description", Document().append("\$regex", searchQuery)),
            Document().append("restaurant_price", Document().append("\$regex", searchQuery)),
            Document().append("restaurant_rating", Document().append("\$regex", searchQuery)),
            Document().append("restaurant_delivery_time", Document().append("\$regex", searchQuery)),
            Document().append("restaurant_menu_list", Document().append("\$elemMatch", Document().append("dish_title",
            Document().append("\$regex", searchQuery))))

        )



        restaurantsFilters.add(Document().append("\$or", restaurantSearchList))
        productsFilters.add(Document().append("\$or", productSearchList))


        val remoteRestaurantFindIterable =
            restaurantCollection
                .find(Document().append("\$and", restaurantsFilters))
                .limit(limitSize)


        val remoteProductFindIterable: RemoteFindIterable<*> =
            productsCollection
                .find(Document().append("\$and", productsFilters))
                .limit(limitSize)


        val foundRestaurantItems = remoteRestaurantFindIterable.into(ArrayList<Any>())
        val foundProductItems = remoteProductFindIterable.into(ArrayList<Any>())

        return Pair(
            foundRestaurantItems.await().result.map{
            val document = it as Document
            val dishList = (document["restaurant_menu_list"] as ArrayList<Document>)
                .filter { doc -> doc.getString("dish_title").contains(searchQuery)}
                .map {doc -> convertDocumentToDish(doc) }
            SearchItem.RestaurantItem(convertDocumentToRestaurant(document),
                dishList)
            },
            foundProductItems.await().result.map{SearchItem.ProductItem(convertDocumentToProduct(it as Document))}
        )

    }


    private fun convertDocumentToProduct(document: Document): Product {
        return Product(
            id = document.getObjectId("_id").toString(),
            product_title = document.getString("product_title"),
            product_category = document.getString("product_category"),
            product_subcategory = document.getString("product_subcategory"),
            current_price = document.getString("current_price"),
            default_price = document.getString("default_price"),
            img_url = document.getString("img_url")
        )
    }

    private fun convertDocumentToRestaurant(document: Document): Restaurant{
        return Restaurant(
            restaurant_id = document.getObjectId("_id").toString(),
            restaurant_title = document.getString("restaurant_title"),
            restaurant_description = document.getString("restaurant_description"),
            restaurant_price = document.getInteger("restaurant_price"),
            restaurant_rating = document.getString("restaurant_rating"),
            restaurant_delivery_time = document.getString("restaurant_delivery_time"),
            restaurant_img_url = document.getString("restaurant_img_url")
        )
    }

    private fun convertDocumentToDish(document: Document):Dish{
        return Dish(
            dish_id = document.getObjectId("_id").toString(),
            dish_title = document.getString("dish_title"),
            dish_description = document.getString("dish_description"),
            dish_price = document.getInteger("dish_price"),
            dish_weight = document.getInteger("dish_weight"),
            dish_img_url = document.getString("dish_img_url"),
            restaurant_owner_id = document.getString("restaurant_owner_id")
        )
    }


    private suspend fun <T> Task<T>.await(): Task<T> = suspendCoroutine { continuation ->
        addOnCompleteListener { task ->
            continuation.resume(task)

//            if (task.isSuccessful) {
//                continuation.resume(task)
//            } else {
//                continuation.resumeWithException(task.exception ?: IOException("No connection"))
//            }
        }
    }

}