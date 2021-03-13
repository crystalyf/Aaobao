package org.bubbble.taobao.ui.tao.flow

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.bubbble.taobao.databinding.ItemTaoShopMessageBinding
import org.bubbble.taobao.util.load

/**
 * @author Andrew
 * @date 2020/11/17 20:22
 */
class TaoShopAdapter : ListAdapter<Int, TaoShopAdapter.ViewHolder>(ShopDiffCallback()) {

    class ViewHolder(private val binding: ItemTaoShopMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: Int) {
            binding.shopPhoto.load(data)
        }
    }

    class ShopDiffCallback : DiffUtil.ItemCallback<Int>() {

        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemTaoShopMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}