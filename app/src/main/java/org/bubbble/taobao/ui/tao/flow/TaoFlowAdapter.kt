package org.bubbble.taobao.ui.tao.flow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.bubbble.taobao.R
import org.bubbble.taobao.databinding.ItemTaoFlowBinding
import org.bubbble.taobao.databinding.ItemTaoShopBinding
import org.bubbble.taobao.util.load


/**
 * @author Andrew
 * @date 2020/11/16 14:41
 */
internal class TaoFlowAdapter : ListAdapter<TaoFlowItem, ViewHolder>(TaoFlowDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.item_tao_flow -> {
                ViewHolder.FlowViewHolder(
                        ItemTaoFlowBinding.inflate(inflater, parent, false)
                )
            }

            R.layout.item_tao_shop -> {
                ViewHolder.ShopViewHolder(
                        ItemTaoShopBinding.inflate(inflater, parent, false)
                )
            }
            else -> throw IllegalArgumentException("Invalid viewType")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position) as TaoFlowItem
        if (holder is ViewHolder.FlowViewHolder) {
            holder.onBind(data)
        }

        if (holder is ViewHolder.ShopViewHolder) {
            holder.onBind(data)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item.type) {
            0 -> R.layout.item_tao_shop
            1 -> R.layout.item_tao_flow
            else -> -1
        }
    }

    override fun submitList(list: List<TaoFlowItem>?) {
        super.submitList(list?.let { ArrayList(it) })
    }
}

internal sealed class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    class FlowViewHolder(private val binding: ItemTaoFlowBinding) : ViewHolder(binding.root) {
        private val images = intArrayOf(R.drawable.tao_1, R.drawable.tao_2, R.drawable.tao_3, R.drawable.tao_4, R.drawable.tao_5, R.drawable.tao_6, R.drawable.tao_7, R.drawable.tao_8, R.drawable.tao_9)
        fun onBind(data: TaoFlowItem) {
            binding.photoLayout.removeAllViews()
            if (data.data == 0) {
                val photo = ImageView(binding.photoLayout.context)
                val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                photo.layoutParams = lp
                photo.scaleType = ImageView.ScaleType.FIT_CENTER
                photo.load(R.drawable.tao_rectangle)
                binding.photoLayout.addView(photo)
                return
            }
            for (index in 0..data.data) {
                val photo = ImageView(binding.photoLayout.context)
                val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                photo.layoutParams = lp
                photo.scaleType = ImageView.ScaleType.FIT_CENTER
                photo.load(images[index])
                binding.photoLayout.addView(photo)
            }
        }
    }
    val taoShopAdapter = TaoShopAdapter()

    class  ShopViewHolder(private val binding: ItemTaoShopBinding): ViewHolder(binding.root) {
        fun onBind(data: TaoFlowItem) {
            binding.shopList.layoutManager = LinearLayoutManager(binding.shopList.context, LinearLayoutManager.HORIZONTAL, false)
            binding.shopList.adapter = taoShopAdapter
            taoShopAdapter.submitList(data.shopList)
        }
    }
}

object TaoFlowDiffCallback : DiffUtil.ItemCallback<TaoFlowItem>() {
    override fun areItemsTheSame(oldItem: TaoFlowItem, newItem: TaoFlowItem): Boolean {
        return oldItem.data == newItem.data
    }

    override fun areContentsTheSame(oldItem: TaoFlowItem, newItem: TaoFlowItem): Boolean {
        return oldItem.data == newItem.data
    }
}
