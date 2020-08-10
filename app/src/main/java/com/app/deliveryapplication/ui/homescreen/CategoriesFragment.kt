package com.app.deliveryapplication.ui.homescreen

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.deliveryapplication.PRODUCTS_CATEGORIES
import com.app.deliveryapplication.R
import com.app.deliveryapplication.setUpAppBar
import com.app.deliveryapplication.ui.homescreen.adapters.CategoriesAdapter
import com.app.deliveryapplication.ui.homescreen.viewmodels.HomeScreenViewModel
import kotlinx.android.synthetic.main.categories_fragment.*

class CategoriesFragment: Fragment(){

    private lateinit var categoriesAdapter: CategoriesAdapter

    private val categoriesViewModel: HomeScreenViewModel by activityViewModels()

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
        return inflater.inflate(R.layout.categories_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.setUpAppBar(resources.getString(R.string.products), R.drawable.ic_shop)
        if(categoriesViewModel.categoriesAdapter == null){
            categoriesViewModel.categoriesAdapter = CategoriesAdapter(
                PRODUCTS_CATEGORIES,
                categoriesViewModel,
                lifecycleOwner
            )
        }
        categoriesAdapter = categoriesViewModel.categoriesAdapter!!
        val glm = GridLayoutManager(activity, 3)
            .apply {
                this.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (position) {
                            0,1 -> 3
                            else -> 1
                        }
                    }
                }
            }
        categories_pane.layoutManager = glm
        categories_pane.addItemDecoration(itemDecoration)
        categories_pane.adapter = categoriesAdapter

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
            if (position > 1) {
                when {
                    position % 3 == 0 -> {
                        outRect.left =  view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                        outRect.right =  view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                        when {
                            position == 3 -> {
                                outRect.top = view.resources.getDimension(R.dimen.dimen_12dp).toInt()
                                outRect.bottom = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                            }
                            position + 2 >= parent.adapter!!.itemCount -> {
                                outRect.top = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                                outRect.bottom = view.resources.getDimension(R.dimen.dimen_16dp).toInt()
                            }
                            else -> {
                                outRect.top = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                                outRect.bottom = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                            }
                        }
                    }
                    position % 3 == 2 -> {
                        outRect.left = view.resources.getDimension(R.dimen.dimen_16dp).toInt()
                        outRect.right = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                        when {
                            position == 2 -> {
                                outRect.top = view.resources.getDimension(R.dimen.dimen_12dp).toInt()
                                outRect.bottom = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                            }
                            position + 3 >= parent.adapter!!.itemCount -> {
                                outRect.top = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                                outRect.bottom = view.resources.getDimension(R.dimen.dimen_16dp).toInt()
                            }
                            else -> {
                                outRect.top = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                                outRect.bottom = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                            }
                        }
                    }
                    else -> {
                        outRect.right = view.resources.getDimension(R.dimen.dimen_16dp).toInt()
                        outRect.left = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                        when {
                            position == 4 -> {
                                outRect.top = view.resources.getDimension(R.dimen.dimen_12dp).toInt()
                                outRect.bottom = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                            }
                            position + 1 >= parent.adapter!!.itemCount -> {
                                outRect.top = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                                outRect.bottom = view.resources.getDimension(R.dimen.dimen_16dp).toInt()
                            }
                            else -> {
                                outRect.top = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                                outRect.bottom = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        categoriesViewModel.categoriesAdapter = null
    }


}