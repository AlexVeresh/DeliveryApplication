package com.app.deliveryapplication.ui.homescreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.app.deliveryapplication.R
import com.app.deliveryapplication.setUpAppBar
import com.app.deliveryapplication.setUpConnectionError
import com.app.deliveryapplication.setUpLoading
import com.app.deliveryapplication.ui.AppLoadStateAdapter
import com.app.deliveryapplication.ui.CustomFragment
import com.app.deliveryapplication.ui.homescreen.adapters.RestaurantsAdapter
import com.app.deliveryapplication.ui.homescreen.viewmodels.HomeScreenViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_pages_navigator.*
import kotlinx.android.synthetic.main.home_page_fragment.*
import kotlinx.android.synthetic.main.products_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest


class RestaurantsListFragment : CustomFragment(){

    private lateinit var restaurantsAdapter: RestaurantsAdapter

    private val restaurantsViewModel: HomeScreenViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_page_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.setUpAppBar(resources.getString(R.string.restaurants), R.drawable.ic_restaurant)
        if(restaurantsViewModel.restaurantsAdapter==null) {
           restaurantsViewModel.restaurantsAdapter =  initAdapter()
        }
        restaurantsAdapter = restaurantsViewModel.restaurantsAdapter!!
        home_items_pane.adapter = restaurantsAdapter.withLoadStateFooter(
            AppLoadStateAdapter(restaurantsAdapter)
        )
        initSwipeToRefresh()


    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun initAdapter():RestaurantsAdapter{
        val restaurantsAdapter = RestaurantsAdapter(restaurantsViewModel)

        lifecycleScope.launchWhenCreated {
            restaurantsAdapter.loadStateFlow.collectLatest {
                if(home_swipe_refresh != null && home_swipe_refresh.isRefreshing) {
                    home_swipe_refresh.isRefreshing = it.refresh is LoadState.Loading
                }
                requireActivity().setUpLoading(it.refresh is LoadState.Loading)
                if(it.refresh is LoadState.Error) {
                    requireActivity().setUpConnectionError(
                        restaurantsAdapter.itemCount == 0
                    ) { restaurantsAdapter.retry() }
                        if(restaurantsAdapter.itemCount!=0) {
                            Toast.makeText(
                                requireContext(),
                                "Ошибка подключения",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
            }
        }

        lifecycleScope.launchWhenCreated {
            restaurantsViewModel.restaurants.collectLatest {
                requireActivity().setUpLoading(false)
                restaurantsAdapter.submitData(it)

            }
        }
        return restaurantsAdapter

    }

    private fun initSwipeToRefresh(){
        home_swipe_refresh?.setOnRefreshListener {
            restaurantsAdapter.refresh()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        restaurantsViewModel.restaurantsAdapter = null
    }
}