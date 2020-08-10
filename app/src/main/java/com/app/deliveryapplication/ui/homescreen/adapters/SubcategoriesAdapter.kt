package com.app.deliveryapplication.ui.homescreen.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.app.deliveryapplication.R
import com.app.deliveryapplication.ui.homescreen.viewmodels.HomeScreenViewModel
import kotlinx.android.synthetic.main.products_fragment.*
import kotlinx.android.synthetic.main.subcategory_item.view.*


class SubcategoriesAdapter(
    private val subcategoryList: List<String>,
    private val viewModel: HomeScreenViewModel,
    private val rootPane: RecyclerView
): RecyclerView.Adapter<SubcategoriesAdapter.SubcategoryViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubcategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.subcategory_item, parent, false)
        return SubcategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubcategoryViewHolder, position: Int) {
        holder.bind(subcategoryList[position])
    }

    override fun getItemCount(): Int {

        return subcategoryList.size
    }

    inner class SubcategoryViewHolder(val view: View): RecyclerView.ViewHolder(view){

        private val subcategoryText: TextView = view.subcategory_text
        private val subcategoryItemPane: CardView = view.subcategory_item_pane
        private var subcategory:String? = null
        init {
            view.setOnClickListener {
                val currentItem = viewModel.chosenSubcategory.value
                notifyItemChanged(subcategoryList.indexOf(currentItem))
                notifyItemChanged(subcategoryList.indexOf(subcategory!!))
                viewModel.adapterState = rootPane.layoutManager?.onSaveInstanceState()
                viewModel.chosenSubcategory.postValue(subcategory)
                viewModel.productSubcategoryClicked = true
            }
        }
        fun bind(subcategory: String){
            this.subcategory = subcategory
            subcategoryText.text = subcategory
            if(viewModel.chosenSubcategory.value == subcategory){
                subcategoryText.setTextColor(Color.WHITE)
                subcategoryItemPane.setCardBackgroundColor(view.resources.getColor(R.color.colorPrimary))
            }
            else{
                subcategoryText.setTextColor(Color.RED)
                subcategoryItemPane.setCardBackgroundColor(view.resources.getColor(R.color.colorWhite))
            }
        }

    }
}

