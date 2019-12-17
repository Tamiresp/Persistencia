package com.example.weaterapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weaterapp.R

class ListAdapter(private val items: MutableList<Item>): RecyclerView.Adapter<MyViewHolder>() {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item: Item = items[position]
    }

    private fun deleteItem(holder: MyViewHolder, position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    fun addItem(item: Item) {
        items.add(item)
        notifyItemInserted(itemCount)
    }

    private fun updateItem(holder: MyViewHolder, position: Int) {
        items[position] = Item()
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val viewHolder = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return MyViewHolder(viewHolder)
    }

}
class MyViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){


}