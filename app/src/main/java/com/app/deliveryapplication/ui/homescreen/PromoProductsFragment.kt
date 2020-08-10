package com.app.deliveryapplication.ui.homescreen

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.deliveryapplication.R
import com.app.deliveryapplication.setUpAppBar
import com.app.deliveryapplication.setUpConnectionError
import com.app.deliveryapplication.setUpLoading
import com.app.deliveryapplication.ui.AppLoadStateAdapter
import com.app.deliveryapplication.ui.CustomFragment
import com.app.deliveryapplication.ui.homescreen.adapters.ProductsAdapter
import com.app.deliveryapplication.ui.homescreen.viewmodels.HomeScreenViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_page_fragment.*
import kotlinx.android.synthetic.main.products_fragment.*
import kotlinx.android.synthetic.main.promo_products_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PromoProductsFragment: CustomFragment(){

    private val promoProductsViewModel: HomeScreenViewModel by activityViewModels()

    private lateinit var promoProductsAdapter: ProductsAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.promo_products_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.setUpAppBar(resources.getString(R.string.products), R.drawable.ic_shop)
        if(promoProductsViewModel.productsAdapter==null) {
            promoProductsViewModel.productsAdapter = initAdapter()

        }
        promoProductsAdapter =  promoProductsViewModel.productsAdapter!!
        val glm = GridLayoutManager(requireActivity(), 2)
        glm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                return when(position){
                    promoProductsAdapter.itemCount -> 2
                    else -> 1
                }
            }
        }
        promo_products_pane.layoutManager = glm
        promo_products_pane.addItemDecoration(itemDecoration)
        promo_products_pane.adapter = promoProductsAdapter.withLoadStateFooter(
             AppLoadStateAdapter(promoProductsAdapter)
        )
        initSwipeToRefresh()

    }

    private fun initAdapter():ProductsAdapter{

        val promoProductsAdapter = ProductsAdapter(promoProductsViewModel)

        @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
        lifecycleScope.launchWhenCreated {
            promoProductsAdapter.loadStateFlow.collectLatest {
                if(promo_products_refresh != null &&promo_products_refresh.isRefreshing) {
                    promo_products_refresh.isRefreshing = it.refresh is LoadState.Loading
                }
                requireActivity().setUpLoading(it.refresh is LoadState.Loading)
                if(it.refresh is LoadState.Error) {
                    requireActivity().setUpConnectionError(
                        promoProductsAdapter.itemCount == 0
                    ) { promoProductsAdapter.retry() }.also {
                        if(promoProductsAdapter.itemCount!=0)
                            Toast.makeText(
                                requireContext(),
                                "Ошибка подключения",
                                Toast.LENGTH_SHORT
                            ).show()
                    }
                }

            }
        }

        @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
        lifecycleScope.launch{
                promoProductsViewModel.promoProducts
                    .collectLatest{
                        promoProductsAdapter.submitData(it)
                    }
        }

        return promoProductsAdapter
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
            if(position != promoProductsAdapter.itemCount){
                if (position % 2 == 0) {
                    if (position == 0) {
                        outRect.top = view.resources.getDimension(R.dimen.dimen_12dp).toInt()
                    } else {
                        outRect.top = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                    }
                    outRect.left = view.resources.getDimension(R.dimen.dimen_16dp).toInt()
                    outRect.right = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                    if (position == parent.adapter!!.itemCount - 1||position == parent.adapter!!.itemCount - 2) {
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

    private fun initSwipeToRefresh(){
        promo_products_refresh?.setOnRefreshListener {
            promoProductsAdapter.refresh()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        promoProductsViewModel.productsAdapter = null
    }
}