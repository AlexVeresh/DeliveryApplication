package com.app.deliveryapplication.ui.homescreen

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.app.deliveryapplication.*
import com.app.deliveryapplication.ui.AppLoadStateAdapter

import com.app.deliveryapplication.ui.CustomFragment
import com.app.deliveryapplication.ui.homescreen.adapters.ProductsAdapter
import com.app.deliveryapplication.ui.homescreen.adapters.SubcategoriesAdapter
import com.app.deliveryapplication.ui.homescreen.viewmodels.DEFAULT_SUBCATEGORY
import com.app.deliveryapplication.ui.homescreen.viewmodels.HomeScreenViewModel
import com.app.deliveryapplication.vo.Product
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_pages_navigator.*
import kotlinx.android.synthetic.main.activity_pages_navigator.view.*
import kotlinx.android.synthetic.main.app_bar.view.*
import kotlinx.android.synthetic.main.home_page_fragment.*
import kotlinx.android.synthetic.main.products_fragment.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import java.lang.Error

@AndroidEntryPoint
class ProductsFragment: Fragment(){


    private lateinit var productsAdapter: ProductsAdapter

    private val homeScreenViewModel: HomeScreenViewModel by activityViewModels()
    private var job: Job? = null
    private lateinit var lifecycleOwner: LifecycleOwner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleOwner = this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.products_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.setUpAppBar(homeScreenViewModel.chosenCategory.value!!, R.drawable.ic_shop)
        if(homeScreenViewModel.productsAdapter==null) {
           homeScreenViewModel.productsAdapter = initAdapter()

        }
        productsAdapter =  homeScreenViewModel.productsAdapter!!
        initSwipeToRefresh()

        val glm = GridLayoutManager(requireActivity(), 2)
        glm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                return when(position){
                    productsAdapter.itemCount -> 2
                    else -> 1
                }
            }
        }
        products_pane.layoutManager = glm
        products_pane.addItemDecoration(itemDecoration)
        products_pane.adapter = productsAdapter.withLoadStateFooter(
            AppLoadStateAdapter(productsAdapter)
        )
        initAppBarPane()


    }



    private fun initAdapter():ProductsAdapter{

        val productsAdapter = ProductsAdapter(homeScreenViewModel)


        @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
        lifecycleScope.launchWhenCreated {
            productsAdapter.loadStateFlow.collectLatest {
                if(products_swipe_refresh != null &&products_swipe_refresh.isRefreshing) {
                    products_swipe_refresh.isRefreshing = it.refresh is LoadState.Loading
                }
                setUpLoading(it.refresh is LoadState.Loading)
                if(homeScreenViewModel.productSubcategoryClicked&&
                    it.refresh is LoadState.NotLoading) {
                    products_pane.scrollToPosition(0)
                    homeScreenViewModel.productSubcategoryClicked = false
                }
                if(it.refresh is LoadState.Error) {
                    setUpConnectionError(
                      error = productsAdapter.itemCount == 0
                    ) { productsAdapter.retry() }
                    if(productsAdapter.itemCount!=0)
                        Toast.makeText(
                            requireContext(),
                            "Ошибка подключения",
                            Toast.LENGTH_SHORT
                        ).show()

                }


            }
        }


        @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
        homeScreenViewModel.chosenSubcategory.observe(lifecycleOwner, Observer {newSubcategory ->
            if(job!=null){
                job?.cancel()
            }
            job = lifecycleScope.launch{
            homeScreenViewModel.products(homeScreenViewModel.chosenCategory.value!!, newSubcategory)
                    .collectLatest{
                        productsAdapter.submitData(it)
                    }
            }
        })

        return productsAdapter
    }

    private fun initAppBarPane(){
        val view = this.activity?.findViewById<View>(R.id.app_bar)
        view?.app_bar_pane?.elevation = 0F
        val itemDecoration = object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                outRect.top = view.resources.getDimension(R.dimen.dimen_12dp).toInt()
                outRect.bottom = view.resources.getDimension(R.dimen.dimen_12dp).toInt()
                when (position) {
                    0 -> {
                        outRect.left = view.resources.getDimension(R.dimen.dimen_16dp).toInt()
                        outRect.right = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                    }
                    parent.adapter!!.itemCount - 1 -> {
                        outRect.left = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                        outRect.right = view.resources.getDimension(R.dimen.dimen_16dp).toInt()
                     }
                    else -> {
                        outRect.left = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                        outRect.right = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                    }
                }
            }
        }
        subcategory_list?.addItemDecoration(itemDecoration)

        subcategory_list?.adapter  =
        SubcategoriesAdapter(
            listOf("Все") + PRODUCTS_SUBCATEGORIES[homeScreenViewModel.chosenCategory.value]!!,
            homeScreenViewModel,
            subcategory_list!!
        )

        if(homeScreenViewModel.adapterState!=null) {
            subcategory_list.layoutManager?.onRestoreInstanceState(homeScreenViewModel.adapterState)
            homeScreenViewModel.adapterState = null
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        homeScreenViewModel.productsAdapter = null
        homeScreenViewModel.adapterState = null
        homeScreenViewModel.chosenSubcategory.postValue(DEFAULT_SUBCATEGORY)
        homeScreenViewModel.chosenCategory.postValue(null)
    }
    private fun initSwipeToRefresh(){
        products_swipe_refresh.setOnRefreshListener {
            productsAdapter.refresh()

        }
    }


    private val itemDecoration = object : ItemDecoration(){
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            val position = parent.getChildAdapterPosition(view)
            if (position!=productsAdapter.itemCount) {
                if (position % 2 == 0) {
                    if (position == 0) {
                        outRect.top = view.resources.getDimension(R.dimen.dimen_12dp).toInt()
                    } else {
                        outRect.top = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                    }
                    outRect.left = view.resources.getDimension(R.dimen.dimen_16dp).toInt()
                    outRect.right = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                    if (position == parent.adapter!!.itemCount - 1 || position == parent.adapter!!.itemCount - 2) {
                        outRect.bottom = view.resources.getDimension(R.dimen.dimen_12dp).toInt()
                    } else {
                        outRect.bottom = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                    }
                } else if (position % 2 != 0) {
                    if (position == 1) {
                        outRect.top = view.resources.getDimension(R.dimen.dimen_12dp).toInt()
                    } else {
                        outRect.top = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                    }
                    outRect.left = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                    outRect.right = view.resources.getDimension(R.dimen.dimen_16dp).toInt()
                    if (position == parent.adapter!!.itemCount - 1) {
                        outRect.bottom = view.resources.getDimension(R.dimen.dimen_12dp).toInt()
                    } else {
                        outRect.bottom = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                    }
                }
            }
        }
    }

    private fun setUpLoading(isLoading: Boolean){
        val view = this.view?.findViewById<View>(R.id.loading_pane)
        view?.isVisible = isLoading
        view?.progress_bar?.isVisible = true
        view?.loading_error_msg?.isVisible = false
        view?.loading_retry?.isVisible = false
    }

    private fun setUpConnectionError(error: Boolean, retryCallBack: () -> Unit){
        val view = this.view?.findViewById<View>(R.id.loading_pane)
        view?.isVisible = error
        view?.progress_bar?.isVisible = false
        view?.loading_error_msg?.isVisible = true
        view?.loading_retry?.setOnClickListener { retryCallBack.invoke() }
        view?.loading_retry?.isVisible = true

    }

    override fun onDestroyView() {
        super.onDestroyView()
        setUpLoading(false)
    }

}