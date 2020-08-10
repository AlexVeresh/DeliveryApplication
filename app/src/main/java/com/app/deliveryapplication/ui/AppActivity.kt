package com.app.deliveryapplication.ui

import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.app.deliveryapplication.R
import com.app.deliveryapplication.listOfIds
import com.app.deliveryapplication.setupWithNavController
import com.app.deliveryapplication.ui.search.SearchItemInformationFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mongodb.stitch.android.core.StitchAppClient
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity: AppCompatActivity(){


    private var currentNavController:LiveData<NavController>? = null


    @Inject
    lateinit var appClient: StitchAppClient





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pages_navigator)
        if(savedInstanceState == null){
            setupBottomNavigationBar()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putIntegerArrayList("listOfIds", listOfIds)
        super.onSaveInstanceState(outState)

    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        listOfIds = savedInstanceState.getIntegerArrayList("listOfIds") ?: ArrayList()
        setupBottomNavigationBar()


    }

    private fun setupBottomNavigationBar(){

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_bar)

        val navGraphIds = listOf(R.navigation.homescreen, R.navigation.promotions, R.navigation.basket, R.navigation.search, R.navigation.profile)

        val navController = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_fragment_container
        )

        currentNavController = navController

    }

    override fun onNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }
}