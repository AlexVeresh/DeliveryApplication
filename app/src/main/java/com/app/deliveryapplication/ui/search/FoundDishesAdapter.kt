package com.app.deliveryapplication.ui.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.deliveryapplication.R
import com.app.deliveryapplication.ui.AppActivity
import com.app.deliveryapplication.vo.Dish
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.android.synthetic.main.found_dish_item.view.*

class FoundDishesAdapter(
    private val foundDishes: List<Dish>,
    private val viewModel: SearchViewModel,
    private val context: Context
): RecyclerView.Adapter<FoundDishViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoundDishViewHolder {
        return FoundDishViewHolder.create(parent,viewModel, context)
    }

    override fun getItemCount(): Int {
        return foundDishes.size
    }

    override fun onBindViewHolder(holder: FoundDishViewHolder, position: Int) {
        holder.bind(foundDishes[position])
    }

}
class FoundDishViewHolder(
    view: View,
    viewModel: SearchViewModel,
    context: Context
): RecyclerView.ViewHolder(view){

    private val foundDishTitle = view.found_dish_title
    private val foundDishPrice = view.found_dish_price
    private var dish: Dish? = null

    init {
        view.setOnClickListener {
            viewModel.chosenDish = dish
            SearchItemInformationFragment().show((context as AppActivity).supportFragmentManager, "ItemFromSearchScreen")
        }
    }

    fun bind(dish: Dish?){
        this.dish = dish
        foundDishTitle.text = dish?.dish_title
        foundDishPrice.text = itemView.resources.getString(R.string.currency, dish?.dish_price.toString())
    }

    companion object{
        fun create(parent: ViewGroup, viewModel: SearchViewModel, context: Context):FoundDishViewHolder{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.found_dish_item, parent, false)
            return FoundDishViewHolder(view, viewModel, context)
        }
    }
}