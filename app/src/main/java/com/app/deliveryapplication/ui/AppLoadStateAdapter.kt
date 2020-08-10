package com.app.deliveryapplication.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.deliveryapplication.R
import kotlinx.android.synthetic.main.network_state_item.view.*

class AppLoadStateAdapter(
    private val adapter: PagingDataAdapter<*, *>
) : LoadStateAdapter<ItemStateViewHolder>() {
    override fun onBindViewHolder(holder: ItemStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): ItemStateViewHolder {
        return ItemStateViewHolder(parent) { adapter.retry() }
    }
}

class ItemStateViewHolder(
    parent: ViewGroup,
    private val retry: () -> Unit
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.network_state_item, parent, false)
){
    private val progressBar = itemView.loading_bar
    private val retryBtn = itemView.retry_btn
        .also { it.setOnClickListener { retry.invoke() } }
    private val errorMsg = itemView.error_title

    fun bind(loadState: LoadState) {

        progressBar.isVisible = loadState is LoadState.Loading
        retryBtn.isVisible = loadState is LoadState.Error
        errorMsg.isVisible = loadState is LoadState.Error

    }

}