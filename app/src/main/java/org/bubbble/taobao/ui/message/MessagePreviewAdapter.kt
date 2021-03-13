package org.bubbble.taobao.ui.message

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.bubbble.taobao.databinding.ItemMessagePreviewBinding

/**
 * @author Andrew
 * @date 2020/11/18 11:39
 */
class MessagePreviewAdapter : ListAdapter<Int, MessagePreviewAdapter.ViewHolder>(MessagePreviewDiffCallback()) {

    class ViewHolder(private val binding: ItemMessagePreviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: Int) {

        }
    }

    class MessagePreviewDiffCallback : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMessagePreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}