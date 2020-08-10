package com.app.deliveryapplication.ui.homescreen.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.deliveryapplication.R


const val FIRST_OFFER_TYPE = R.layout.first_offer_type
const val SECOND_OFFER_TYPE = R.layout.second_offer_type

class PromotionsAdapter(): RecyclerView.Adapter<PromotionsViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when(position%2){
            0 -> FIRST_OFFER_TYPE
            else -> SECOND_OFFER_TYPE
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromotionsViewHolder {
        return PromotionsViewHolder.create(parent, viewType)
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: PromotionsViewHolder, position: Int) {
        holder.bind()
    }

}

class PromotionsViewHolder(view: View): RecyclerView.ViewHolder(view){

    fun bind(){

    }

    companion object{
        fun create(parent: ViewGroup, viewType: Int): PromotionsViewHolder{
            val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
            return PromotionsViewHolder(view)
        }
    }


}