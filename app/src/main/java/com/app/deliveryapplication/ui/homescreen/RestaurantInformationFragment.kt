package com.app.deliveryapplication.ui.homescreen

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.app.deliveryapplication.R
import com.app.deliveryapplication.setUpAppBar
import com.app.deliveryapplication.setUpConnectionError
import com.app.deliveryapplication.setUpLoading
import com.app.deliveryapplication.ui.CustomFragment
import com.app.deliveryapplication.ui.homescreen.adapters.RestaurantInfoAdapter
import com.app.deliveryapplication.ui.homescreen.viewmodels.HomeScreenViewModel
import com.app.deliveryapplication.vo.Restaurant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_pages_navigator.*
import kotlinx.android.synthetic.main.restaurant_information_fragment.*
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class RestaurantInformationFragment : Fragment(){


    private val restaurantInfoViewModel: HomeScreenViewModel by activityViewModels()

    private lateinit var restaurantInfoAdapter: RestaurantInfoAdapter

    private lateinit var chosenRestaurant: Restaurant

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.restaurant_information_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.setUpAppBar(resources.getString(R.string.restaurants), R.drawable.ic_restaurant)
        restaurant_info_pane.addItemDecoration(itemDecoration)
        lifecycleScope.launchWhenCreated {
            restaurantInfoViewModel.restaurantById(
                restaurantInfoViewModel.chosenRestaurantId.value!!
            ).collectLatest {
                chosenRestaurant = it
                setRestaurantInfo()
            }
        }

    }

    private fun setRestaurantInfo(){
        if(restaurantInfoViewModel.restaurantInfoAdapter == null){
            loading_dish_bar.isVisible = true
            restaurantInfoViewModel.restaurantInfoAdapter = initAdapter()
        }
        restaurantInfoAdapter = restaurantInfoViewModel.restaurantInfoAdapter!!
        restaurant_info_pane.adapter = restaurantInfoAdapter
        if(restaurantInfoAdapter.itemCount == 0 && !loading_dish_bar.isVisible){
            requireActivity().setUpConnectionError(true){
                loading_dish_bar.isVisible = true
                requireActivity().setUpLoading(false)
                restaurantInfoViewModel.retryDishesCall(chosenRestaurant.restaurant_id)
            }
        }
    }

    private fun initAdapter(): RestaurantInfoAdapter{
        val restaurantInfoAdapter = RestaurantInfoAdapter(restaurantInfoViewModel, requireActivity(), chosenRestaurant)

        lifecycleScope.launchWhenCreated {
            restaurantInfoViewModel.dishes(chosenRestaurant.restaurant_id)
                .collectLatest {
                    loading_dish_bar.isVisible = false
                    if(it.size == 1){
                        requireActivity().setUpConnectionError(true){
                            loading_dish_bar.isVisible = true
                            restaurantInfoViewModel.retryDishesCall(chosenRestaurant.restaurant_id)
                        }
                    }
                    else {
                        requireActivity().setUpLoading(false)
                        restaurantInfoAdapter.submitList(it)

                    }
                }
        }
        return restaurantInfoAdapter
    }

    private val itemDecoration = object : RecyclerView.ItemDecoration(){
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            val position = parent.getChildAdapterPosition(view)
            if (position!=0) {
                outRect.left = view.resources.getDimension(R.dimen.dimen_16dp).toInt()
                outRect.right = view.resources.getDimension(R.dimen.dimen_16dp).toInt()
                when(position){
                    1 -> {
                        outRect.top =  view.resources.getDimension(R.dimen.dimen_16dp).toInt()
                        outRect.bottom =  view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                    }
                    restaurantInfoAdapter.itemCount-1 -> {
                        outRect.top =  view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                        outRect.bottom =  view.resources.getDimension(R.dimen.dimen_16dp).toInt()
                    }
                    else -> {
                        outRect.top =  view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                        outRect.bottom =  view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                    }

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loading_dish_bar.isVisible = false
        requireActivity().setUpLoading(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        restaurantInfoViewModel.restaurantInfoAdapter = null
        restaurantInfoViewModel.chosenRestaurantId.postValue(null)
    }
}