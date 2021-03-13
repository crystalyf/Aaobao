package org.bubbble.taobao.ui.search.result

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.bubbble.taobao.R
import org.bubbble.taobao.databinding.ItemResultSingleBinding
import org.bubbble.taobao.databinding.ItemResultTwinBinding

/**
 * @author Andrew
 * @date 2020/11/20 14:50
 */
internal class ResultAdapter : ListAdapter<ResultItem, ViewHolder>(ResultDiffCallback) {

    var itemType = Result.SINGLE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.item_result_single -> ViewHolder.SingleViewHolder(ItemResultSingleBinding.inflate(layoutInflater, parent, false))
            R.layout.item_result_twin -> ViewHolder.TwinViewHolder(ItemResultTwinBinding.inflate(layoutInflater, parent, false))
            else -> throw IllegalArgumentException("Invalid viewType")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemViewType(position: Int): Int {
        return when (itemType) {
            Result.SINGLE -> R.layout.item_result_single
            Result.TWIN -> R.layout.item_result_twin
        }
    }

    override fun submitList(list: List<ResultItem>?) {
        super.submitList(list?.let { ArrayList(it) })
    }
}

internal sealed class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class SingleViewHolder(private val binding: ItemResultSingleBinding) : ViewHolder(binding.root) {
        fun onBind(data: ResultItem) {

        }
    }
    class TwinViewHolder(private val binding: ItemResultTwinBinding) : ViewHolder(binding.root) {
        fun onBind(data: ResultItem) {

        }
    }
}

object ResultDiffCallback : DiffUtil.ItemCallback<ResultItem>() {
    override fun areItemsTheSame(oldItem: ResultItem, newItem: ResultItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ResultItem, newItem: ResultItem): Boolean {
        return oldItem == newItem
    }

}

enum class Result{
    SINGLE,
    TWIN
}