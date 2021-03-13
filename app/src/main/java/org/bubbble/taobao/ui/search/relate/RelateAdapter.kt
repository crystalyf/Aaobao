package org.bubbble.taobao.ui.search.relate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.bubbble.taobao.databinding.ItemRelateKeywordBinding

/**
 * @author Andrew
 * @date 2020/11/20 11:22
 */
class RelateAdapter : ListAdapter<Int, RelateAdapter.ViewHolder>(RelateDiffCallback()) {

    class ViewHolder(binding: ItemRelateKeywordBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(data : Int) {

        }
    }

    class RelateDiffCallback : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRelateKeywordBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}