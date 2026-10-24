package com.example.myapplication.utilities

import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding


class UniversalAdapter<T, VB: ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup, Boolean) -> VB,
    private val bind: (binding: VB, item: T) -> Unit,
    private val onItemClick: (T) -> Unit = {}
) : RecyclerView.Adapter<UniversalAdapter.VH<VB>>() {
    private var list: List<T> = emptyList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VH<VB> {
        val binding = inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(
        holder: VH<VB>,
        position: Int
    ) {
        val item = list[position]
        bind(holder.binding, item)
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = list.size

    class VH<VB: ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)

    fun submitList(newList: List<T>){
        list = newList
        notifyDataSetChanged()
    }

}