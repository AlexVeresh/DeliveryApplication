package com.app.deliveryapplication

import android.app.Activity
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.util.set
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_pages_navigator.view.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.app_bar.view.*
import kotlinx.android.synthetic.main.network_state_item.view.*


var listOfIds = ArrayList<Int>()

fun BottomNavigationView.setupWithNavController(
    navGraphIds: List<Int>,
    fragmentManager: FragmentManager,
    containerId: Int

): LiveData<NavController> {
    val idToTagMap = SparseArray<String>()
    val selectedNavController = MutableLiveData<NavController>()
    var firstFragmentGraphId = 0
    var selectedNavHostFragment: NavHostFragment? = null

    navGraphIds.forEachIndexed { index, navGraphId ->
        val fragmentTag = "fragment_$index"

        val navHostFragment = getNavHostFragment(
            fragmentManager = fragmentManager,
            fragmentTag = fragmentTag,
            containerId = containerId,
            navGraphId = navGraphId
        )

        val graphId = navHostFragment.navController.graph.id
        if (index == 0) {
            firstFragmentGraphId = graphId;
        }

        idToTagMap[graphId] = fragmentTag

        if (this.selectedItemId == graphId) {

            selectedNavHostFragment = navHostFragment
        }
        detachNavHostFragment(
            fragmentManager,
            navHostFragment
        )

    }
    selectedNavController.value = selectedNavHostFragment?.navController
    attachNavHostFragment(
        fragmentManager,
        selectedNavHostFragment!!,
        selectedNavHostFragment?.navController?.graph?.id == firstFragmentGraphId
    )

    var selectedItemTag = idToTagMap[this.selectedItemId]

    setOnNavigationItemSelectedListener {item ->
        if(fragmentManager.isStateSaved){
            false
        }
        else{

            val newSelectedFragmentTag = idToTagMap[item.itemId]

            if(newSelectedFragmentTag != selectedItemTag){

                val selectedFragment = fragmentManager.findFragmentByTag(newSelectedFragmentTag) as NavHostFragment

                if(fragmentManager.backStackEntryCount == listOfIds.size){

                    listOfIds.add(item.itemId)
                    fragmentManager
                        .beginTransaction()
                        .attach(selectedFragment)
                        .setPrimaryNavigationFragment(selectedFragment)
                        .detach(fragmentManager.findFragmentByTag(selectedItemTag)!!)
                        .addToBackStack(newSelectedFragmentTag)
                        .setReorderingAllowed(true)
                        .commit()

                }
                selectedItemTag = newSelectedFragmentTag
                selectedNavController.value = selectedFragment.navController
                true
            }
            else{
                false
            }

        }

    }

    setOnItemReselected(fragmentManager, idToTagMap)

    fragmentManager.addOnBackStackChangedListener {
        if(fragmentManager.backStackEntryCount < listOfIds.size){
            if(listOfIds.size>1){
                this.selectedItemId = listOfIds[listOfIds.size - 2]
            }
            else if(listOfIds.size == 1){
                this.selectedItemId = firstFragmentGraphId
            }
            listOfIds.removeAt(listOfIds.size - 1)

        }

        selectedNavController.value?.let { controller ->
            if(controller.currentDestination == null){
                controller.navigate(controller.graph.id)
            }
        }
    }


    return selectedNavController

}

private fun BottomNavigationView.setOnItemReselected(
    fragmentManager: FragmentManager,
    idToTagMap: SparseArray<String>
){
    setOnNavigationItemReselectedListener {
        val newSelectedFragmentTag = idToTagMap[it.itemId]
        val selectedNavController = (fragmentManager.findFragmentByTag(newSelectedFragmentTag) as NavHostFragment).navController
        selectedNavController.popBackStack(
            selectedNavController.graph.startDestination, false
        )
    }
}



private fun getNavHostFragment(
    fragmentManager: FragmentManager,
    fragmentTag: String,
    containerId: Int,
    navGraphId: Int
): NavHostFragment{

    val existingNavHostFragment = fragmentManager.findFragmentByTag(fragmentTag) as NavHostFragment?
    existingNavHostFragment?.let { return it }

    val newNavHostFragment = NavHostFragment.create(navGraphId)
    fragmentManager
        .beginTransaction()
        .add(containerId, newNavHostFragment, fragmentTag)
        .commitNow()
    return newNavHostFragment
}

private fun attachNavHostFragment(
    fragmentManager: FragmentManager,
    navHostFragment: NavHostFragment,
    isPrimaryFragment: Boolean
){
    fragmentManager
        .beginTransaction()
        .attach(navHostFragment)
        .apply {
            if(isPrimaryFragment){
                setPrimaryNavigationFragment(navHostFragment)
            }

        }
        .commitNow()
}

private fun detachNavHostFragment(
    fragmentManager: FragmentManager,
    navHostFragment: NavHostFragment
){
    fragmentManager
        .beginTransaction()
        .detach(navHostFragment)
        .commitNow()
}

fun Fragment.setUpAppBar(fragmentTitle: String, fragmentIcon: Int?){
    val view = this.activity?.findViewById<View>(R.id.app_bar)
    view?.app_bar_pane?.elevation = resources.getDimension(R.dimen.dimen_6dp)
    if(fragmentTitle == resources.getString(R.string.search)){
        view?.fragment_icon?.visibility = View.INVISIBLE
        view?.fragment_title?.visibility = View.INVISIBLE
        view?.searching_btn?.visibility = View.VISIBLE
        view?.searching_input_field?.visibility = View.VISIBLE
    }
    else{
        view?.searching_btn?.visibility = View.INVISIBLE
        view?.searching_input_field?.visibility = View.INVISIBLE
        view?.fragment_icon?.visibility = View.VISIBLE
        view?.fragment_title?.visibility = View.VISIBLE
        if(fragmentIcon == null){
            view?.fragment_icon?.visibility = View.INVISIBLE
        }
        else {
            view?.fragment_icon?.background = resources.getDrawable(fragmentIcon)
        }

        view?.fragment_title?.text = fragmentTitle

    }
}

fun Activity.setUpLoading(isLoading: Boolean){
    val view = this.findViewById<View>(R.id.loading_pane)
    view.isVisible = isLoading
    view.progress_bar.isVisible = true
    view.loading_error_msg.isVisible = false
    view.loading_retry.isVisible = false
}

fun Activity.setUpConnectionError(error: Boolean, retryCallBack: () -> Unit){
    val view = this.findViewById<View>(R.id.loading_pane)
    view.isVisible = error
    view.progress_bar.isVisible = false
    view.loading_error_msg.isVisible = true
    view.loading_retry.setOnClickListener { retryCallBack.invoke() }
    view.loading_retry.isVisible = true

}


