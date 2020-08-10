package com.app.deliveryapplication.di

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.app.deliveryapplication.api.RemoteStorageApi
import com.app.deliveryapplication.db.DeliveryDb
import com.app.deliveryapplication.db.ProductsDao
import com.mongodb.stitch.android.core.Stitch
import com.mongodb.stitch.android.core.StitchAppClient
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideStitchAppClient(): StitchAppClient {
        if(!Stitch.hasAppClient("foodman-sslon")) {
            Stitch.initializeDefaultAppClient("foodman-sslon");
        }
        return Stitch.getDefaultAppClient().also {
            it.auth.loginWithCredential(AnonymousCredential())
        }

    }

    @Singleton
    @Provides
    fun provideDb(app: Application): DeliveryDb{
        return Room
            .databaseBuilder(app, DeliveryDb::class.java, "DeliveryDb")
            .fallbackToDestructiveMigration()
            .build()

    }


}