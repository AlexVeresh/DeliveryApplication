package com.app.deliveryapplication.ui.homescreen.adapters

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.app.deliveryapplication.R
import com.app.deliveryapplication.ui.homescreen.viewmodels.HomeScreenViewModel
import kotlinx.android.synthetic.main.category_item.view.*
import kotlinx.android.synthetic.main.promo_products_list.view.*
import kotlinx.android.synthetic.main.promotions_list.view.*
import kotlinx.coroutines.flow.collectLatest

private const val PROMOTIONS_LINE = R.layout.promotions_list
private const val PROMO_PRODUCTS = R.layout.promo_products_list
private const val CATEGORIES = R.layout.category_item

class CategoriesAdapter(
    private val categoriesList: List<Pair<Int, String>>,
    private val viewModel: HomeScreenViewModel,
    private val lifecycleOwner: LifecycleOwner

): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun getItemViewType(position: Int): Int {
        return when(position){
            0 -> PROMOTIONS_LINE
            1 -> PROMO_PRODUCTS
            else -> CATEGORIES
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            PROMOTIONS_LINE -> PromotionLineViewHolder.create(parent, viewType, viewModel)
            PROMO_PRODUCTS -> PromoProductsListViewHolder.create(parent, viewType, viewModel, lifecycleOwner)
            else -> CategoryViewHolder.create(parent, viewType, viewModel)
        }
    }

    override fun getItemCount(): Int {
        return categoriesList.size+2
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == CATEGORIES)
            (holder as CategoryViewHolder).bind(categoriesList[position-2])
    }

}

class CategoryViewHolder(
    val view: View,
    val viewModel: HomeScreenViewModel
): RecyclerView.ViewHolder(view){
    private val categoryImg = view.category_item_img
    private val categoryTitle = view.category_item_title
    private var category: Pair<Int, String>? = null

    init{

        view.setOnClickListener {
            viewModel.chosenCategory.postValue(category?.second)
            view.findNavController().navigate(R.id.action_categoriesFragment_to_productsFragment)
        }

    }



    fun bind(category: Pair<Int, String>){

        this.category = category
        categoryImg.background = view.resources.getDrawable(category.first)
        categoryTitle.text = category.second

    }

    companion object{
        fun create(parent: ViewGroup, viewType: Int, viewModel: HomeScreenViewModel): CategoryViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
            return CategoryViewHolder(
                view,
                viewModel
            )
        }
    }
}

class PromoProductsListViewHolder(
    val view: View,
    val viewModel: HomeScreenViewModel,
    lifecycleOwner: LifecycleOwner
): RecyclerView.ViewHolder(view){

    private val promoProducts = view.promo_products_list
    private val showPromoProducts = view.show_all_promo_products

    init{
        showPromoProducts.setOnClickListener {
            view.findNavController().navigate(R.id.action_categoriesFragment_to_promoProductsFragment)
        }

        val itemDecoration = object: RecyclerView.ItemDecoration(){
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                val position = parent.getChildAdapterPosition(view)
                outRect.top = view.resources.getDimension(R.dimen.dimen_8dp).toInt()
                outRect.bottom = view.resources.getDimension(R.dimen.dimen_12dp).toInt()
                when(position){
                    0 -> {
                        outRect.right = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                        outRect.left = view.resources.getDimension(R.dimen.dimen_16dp).toInt()
                    }
                    parent.adapter?.itemCount?.minus(1) -> {
                        outRect.right = view.resources.getDimension(R.dimen.dimen_16dp).toInt()
                        outRect.left = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                    }
                    else -> {
                        outRect.right = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                        outRect.left = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                    }
                }
            }
        }
        promoProducts.addItemDecoration(itemDecoration)
        val adapter = PromoProductsAdapter(viewModel)
        promoProducts.adapter = adapter
        viewModel.promoProductsForPreview.observe(lifecycleOwner, Observer {
            adapter.submitList(it)
        })

    }

    companion object{
        fun create(parent: ViewGroup, viewType: Int, viewModel: HomeScreenViewModel, lifecycleOwner: LifecycleOwner): PromoProductsListViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
            return PromoProductsListViewHolder(
                view,
                viewModel,
                lifecycleOwner
            )
        }
    }
}

class PromotionLineViewHolder(
    val view: View,
    val viewModel: HomeScreenViewModel
): RecyclerView.ViewHolder(view){

    private val promotionsList: RecyclerView = view.promotions_list

    init{
        val itemDecoration = object: RecyclerView.ItemDecoration(){
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                val position = parent.getChildAdapterPosition(view)
                outRect.top = view.resources.getDimension(R.dimen.dimen_16dp).toInt()
                outRect.bottom = view.resources.getDimension(R.dimen.dimen_8dp).toInt()
                when(position){
                    0 -> {
                        outRect.right = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                        outRect.left = view.resources.getDimension(R.dimen.dimen_16dp).toInt()
                    }
                    parent.adapter?.itemCount?.minus(1) -> {
                        outRect.right = view.resources.getDimension(R.dimen.dimen_16dp).toInt()
                        outRect.left = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                    }
                    else -> {
                        outRect.right = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                        outRect.left = view.resources.getDimension(R.dimen.dimen_4dp).toInt()
                    }
                }
            }
        }
        promotionsList.addItemDecoration(itemDecoration)
        promotionsList.adapter = PromotionsAdapter()
    }

    companion object{
        fun create(parent: ViewGroup, viewType: Int, viewModel: HomeScreenViewModel): PromotionLineViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
            return PromotionLineViewHolder(
                view,
                viewModel
            )
        }
    }
}