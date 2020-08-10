package com.app.deliveryapplication

import android.app.Application
import androidx.lifecycle.Transformations.map
import com.app.deliveryapplication.api.RemoteStorageApi
import com.mongodb.stitch.android.core.StitchAppClient
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.bson.Document
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.system.measureTimeMillis

@HiltAndroidApp
class DeliveryApp: Application() {

    override fun onCreate() {
        super.onCreate()
    }

}