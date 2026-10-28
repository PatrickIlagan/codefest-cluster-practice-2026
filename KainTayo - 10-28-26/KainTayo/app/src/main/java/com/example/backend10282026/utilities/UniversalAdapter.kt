package com.example.backend10282026.utilities

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class UniversalAdapter<T, VB : ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup, Boolean) -> VB,
    private val bind: (binding: VB, item: T) -> Unit,
    private val onItemClick: (T) -> Unit = {}
): RecyclerView.Adapter<UniversalAdapter.VH<VB>>() {
    private var list: List<T> = emptyList()
    override fun onCreateViewHolder(
        p0: ViewGroup,
        p1: Int
    ): VH<VB> {
        val binding = inflate(LayoutInflater.from(p0.context), p0,false)
        return VH(binding)
    }

    override fun onBindViewHolder(
        p0: VH<VB>,
        p1: Int
    ) {
        val item = list[p1]
        bind(p0.binding, item)
        p0.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = list.size

    fun submitList(newList: List<T>){
        list = newList
        notifyDataSetChanged()
    }

    class VH<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)
}