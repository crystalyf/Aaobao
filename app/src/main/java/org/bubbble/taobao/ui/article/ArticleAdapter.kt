package org.bubbble.taobao.ui.article

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.bubbble.taobao.R
import org.bubbble.taobao.databinding.ItemArticleCommodityBinding
import org.bubbble.taobao.databinding.ItemArticleMakeupBinding
import org.bubbble.taobao.databinding.ItemArticleShopBinding
import org.bubbble.taobao.util.load
import org.bubbble.taobao.util.logger

/**
 * @author Andrew
 * @date 2020/11/19 16:02
 */
internal class ArticleAdapter(private var onCallback: (Float, Boolean?) -> Unit): ListAdapter<ArticleItem, ViewHolder>(ArticleDiffCallback) {

    private val selectMap = mutableMapOf<Int, ArticleItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.item_article_shop -> ViewHolder.ShopViewHolder(ItemArticleShopBinding.inflate(layoutInflater, parent, false))
            R.layout.item_article_commodity -> ViewHolder.CommodityViewHolder(ItemArticleCommodityBinding.inflate(layoutInflater, parent, false))
            R.layout.item_article_makeup -> ViewHolder.MakeupViewHolder(ItemArticleMakeupBinding.inflate(layoutInflater, parent, false))
            else -> throw IllegalArgumentException("Invalid viewType")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is ViewHolder.ShopViewHolder) {
            holder.onBind(getItem(position), ::checkedShopAll, ::isShopAllChecked)
        }

        if (holder is ViewHolder.CommodityViewHolder) {
            holder.onBind(getItem(position), ::changePrice, ::onCheckClick, ::isChecked)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item.type) {
            0 -> R.layout.item_article_shop
            1 -> R.layout.item_article_commodity
            2 -> R.layout.item_article_makeup
            else -> -1
        }
    }

    override fun submitList(list: List<ArticleItem>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    private fun onCheckClick(isChecked: Boolean, position: Int) {
        if (isChecked) {
            selectMap[getItem(position).id] = getItem(position)
        } else {
            selectMap.remove(getItem(position).id)
        }
        notifyDataSetChanged()
    }

    private fun checkedShopAll(isChecked: Boolean, position: Int) {
        if (isChecked) {
            for (value in position + 1 until currentList.size) {
                if (currentList[value].type == 1) {
                    selectMap[currentList[value].id] = currentList[value]
                } else if (currentList[value].type != 1) {
                    break
                }
            }
        } else {
            for (value in position + 1 until currentList.size) {
                if (currentList[value].type == 1) {
                    selectMap.remove(currentList[value].id)
                } else if (currentList[value].type != 1) {
                    break
                }
            }
        }
        notifyDataSetChanged()
    }

    fun checkedAll(isChecked: Boolean) {
        if (isChecked) {
            for (value in currentList) {
                if (value.type == 1 || value.type == 0) {
                    selectMap[value.id] = value
                }
            }
        } else {
            for (value in currentList) {
                if (value.type == 1 || value.type == 0) {
                    selectMap.remove(value.id)
                }
            }
        }
        notifyDataSetChanged()
    }

    /**
     * 检索店铺内商品是否全选
     */
    private fun isShopAllChecked(position: Int): Boolean {
        var isAll = true
        for (value in position + 1 until currentList.size) {
            // 如果是1，但不包含立刻终止，如果不是1直接终止，那就isAll没有改变那就代表是全选状态
            if (currentList[value].type == 1 && !selectMap.containsKey(currentList[value].id)) {
                isAll = false
                break
            } else if (currentList[value].type != 1) {
                break
            }
        }
        return isAll
    }

    private fun isChecked(position: Int): Boolean {
        var isCheckAll = true
        var allPrice = 0F
        for (value in currentList) {
            if (value.type == 1) {
                if (selectMap.containsKey(value.id)) {
                    allPrice += value.quantity * value.singlePrice
                } else {
                    isCheckAll = false
                }
            }
        }
        onCallback(allPrice, isCheckAll)
        return selectMap.containsKey(currentList[position].id)
    }

    private fun changePrice() {
        var allPrice = 0F
        for (value in currentList) {
            if (value.type == 1) {
                if (selectMap.containsKey(value.id)) {
                    allPrice += value.quantity * value.singlePrice
                }
            }
        }
        onCallback(allPrice, null)
    }
}

internal sealed class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    class ShopViewHolder(private val binding: ItemArticleShopBinding) : ViewHolder(binding.root) {
        fun onBind(data: ArticleItem,
                   onClick: (Boolean, Int) -> Unit,
                   isChecked: (Int) -> Boolean) {
            binding.shopName.text = data.shopName
            binding.checkBox.isChecked = isChecked(adapterPosition)

            binding.checkBox.setOnClickListener {
                onClick(binding.checkBox.isChecked, adapterPosition)
            }
        }
    }

    class CommodityViewHolder(private val binding: ItemArticleCommodityBinding) : ViewHolder(binding.root) {

        init {
            binding.quantityText.setOnClickListener {
                if (binding.quantityChange.visibility != View.VISIBLE) {
                    binding.quantityChange.visibility = View.VISIBLE
                    binding.quantityText.visibility = View.INVISIBLE
                }
            }
        }
        fun onBind(data: ArticleItem,
                   changePrice: () -> Unit,
                   onClick: (Boolean, Int) -> Unit,
                   isChecked: (Int) -> Boolean) {
            if (binding.quantityChange.visibility == View.VISIBLE) {
                binding.quantityChange.visibility = View.INVISIBLE
                binding.quantityText.visibility = View.VISIBLE
            }


            if (data.quantity == 1) {
                binding.addOne.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.addOne.context, R.color.text_color))
                binding.removeOne.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.removeOne.context, R.color.gray))
            }

            if (data.quantity == data.maxQuantity ){
                binding.addOne.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.addOne.context, R.color.gray))
                binding.removeOne.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.removeOne.context, R.color.text_color))
            }

            binding.checkBox.isChecked = isChecked(adapterPosition)
            binding.pictures.load(data.pictures)
            binding.articleName.text = data.articleName
            binding.subName.text = data.subName
            binding.price.text = "${data.singlePrice}"
            binding.tag.text = data.tag
            binding.quantityText.text = data.quantity.toString()
            binding.quantity.text = data.quantity.toString()

            binding.checkBox.setOnClickListener {
                onClick(binding.checkBox.isChecked, adapterPosition)
            }

            binding.removeOne.setOnClickListener {
                if (data.quantity - 1 > 0) {
                    data.quantity -= 1

                    if (data.quantity == 1) {
                        binding.addOne.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.addOne.context, R.color.text_color))
                        binding.removeOne.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.removeOne.context, R.color.gray))
                    } else {
                        binding.addOne.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.addOne.context, R.color.gray))
                        binding.removeOne.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.removeOne.context, R.color.text_color))
                    }
                    binding.quantityText.text = data.quantity.toString()
                    binding.quantity.text = data.quantity.toString()
                    changePrice()
                } else {
                    Toast.makeText(binding.root.context, "已经不能再少了", Toast.LENGTH_SHORT).show()
                }
            }

            binding.addOne.setOnClickListener {
                if (data.quantity + 1 <= data.maxQuantity) {
                    data.quantity += 1

                    if (data.quantity == data.maxQuantity) {
                        binding.addOne.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.addOne.context, R.color.gray))
                        binding.removeOne.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.removeOne.context, R.color.text_color))
                    } else {
                        binding.addOne.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.addOne.context, R.color.text_color))
                        binding.removeOne.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.removeOne.context, R.color.gray))
                    }

                    binding.quantityText.text = data.quantity.toString()
                    binding.quantity.text = data.quantity.toString()
                    changePrice()
                } else {
                    Toast.makeText(binding.root.context, "已经不能再多了", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    class MakeupViewHolder(private val binding: ItemArticleMakeupBinding) : ViewHolder(binding.root) {
        fun onBind(data: ArticleItem) {
        }
    }
}

object ArticleDiffCallback : DiffUtil.ItemCallback<ArticleItem>() {
    override fun areItemsTheSame(oldItem: ArticleItem, newItem: ArticleItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ArticleItem, newItem: ArticleItem): Boolean {
        return oldItem.id  == newItem.id
    }
}