package com.app.deliveryapplication.ui.search

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.map
import androidx.recyclerview.widget.RecyclerView
import com.app.deliveryapplication.R
import com.app.deliveryapplication.setUpAppBar
import com.app.deliveryapplication.setUpConnectionError
import com.app.deliveryapplication.setUpLoading
import com.app.deliveryapplication.ui.AppLoadStateAdapter
import com.app.deliveryapplication.ui.CustomFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_pages_navigator.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.app_bar.view.*
import kotlinx.android.synthetic.main.promo_products_fragment.*
import kotlinx.android.synthetic.main.search_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment: Fragment(){

    private lateinit var searchingAdapter: SearchAdapter

    private val searchViewModel: SearchViewModel by activityViewModels()

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
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.setUpAppBar(resources.getString(R.string.search), null)
        if(searchViewModel.searchAdapter == null){
            searchViewModel.searchAdapter = initAdapter()
        }
        searchingAdapter = searchViewModel.searchAdapter!!
        searched_items_pane.adapter = searchingAdapter.withLoadStateFooter(
            AppLoadStateAdapter(searchingAdapter)
        )
        initSearch()
    }



    private fun initAdapter() : SearchAdapter{
        val searchAdapter = SearchAdapter(searchViewModel, requireActivity())
        lifecycleScope.launchWhenCreated {
            searchAdapter.loadStateFlow.collectLatest {
                requireActivity().setUpLoading(it.refresh is LoadState.Loading)
                if(it.refresh is LoadState.Error) {
                    requireActivity().setUpConnectionError(
                        searchAdapter.itemCount == 0
                    ) { searchAdapter.retry() }.also {
                        if(searchAdapter.itemCount!=0)
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
        searchViewModel.searchItem.observe(lifecycleOwner, Observer {searchingItem ->

            if(job!=null){
                job?.cancel()
            }
            job = lifecycleScope.launch{
                searchViewModel.searchedProducts(searchingItem!!)
                    .collectLatest{
                            searchAdapter.submitData(it)
                    }


            }
        })
        return searchAdapter
    }

    private fun initSearch(){
        val view = this.activity?.findViewById<View>(R.id.app_bar)
        view?.searching_btn?.setOnClickListener {
            searchViewModel.showSearchedResult(view.searching_input_field?.text?.trim().toString())
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().setUpLoading(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        searchViewModel.searchAdapter = null
    }
}