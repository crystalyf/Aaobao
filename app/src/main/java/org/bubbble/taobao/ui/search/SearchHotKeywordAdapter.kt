package org.bubbble.taobao.ui.search

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.bubbble.taobao.R
import org.bubbble.taobao.data.SearchHotKeyword
import org.bubbble.taobao.data.SearchHotKeywordData
import org.bubbble.taobao.databinding.ItemSearchHotBinding

/**
 * @author Andrew
 * @date 2020/11/09 15:32
 */
class SearchHotKeywordAdapter : ListAdapter<SearchHotKeyword, SearchHotKeywordAdapter.ViewHolder>(SearchHotKeywordDiffCallback()) {

    class ViewHolder(private val context: Context, private val binding: ItemSearchHotBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: SearchHotKeyword) {
            val adapter = KeywordAdapter(context, data = data.keyword)
            for (position in 0 until adapter.count) {
                binding.keywordList.addView(adapter.getView(position, null, binding.keywordList))
            }
        }

        class KeywordAdapter(context: Context, private val resLayout: Int = R.layout.item_search_hot_keyword,
                             private val data: List<SearchHotKeywordData>) : ArrayAdapter<SearchHotKeywordData>(context, resLayout, data) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view: View = convertView ?: LayoutInflater.from(context).inflate(resLayout, parent, false)
                val rank: TextView = view.findViewById(R.id.rank)
                rank.text = "${position + 1}"
                when (position) {
                    0 -> {
                        rank.setBackgroundResource(R.drawable.background_top_1)
                        rank.setTextColor(Color.WHITE)
                    }
                    1 -> {
                        rank.setBackgroundResource(R.drawable.background_top_2)
                        rank.setTextColor(Color.WHITE)
                    }
                    2 -> {
                        rank.setBackgroundResource(R.drawable.background_top_3)
                        rank.setTextColor(Color.WHITE)
                    }
                }
                view.findViewById<TextView>(R.id.keyword).text = data[position].keyword
                return view
            }
        }
    }

    class SearchHotKeywordDiffCallback : DiffUtil.ItemCallback<SearchHotKeyword>() {
        override fun areItemsTheSame(oldItem: SearchHotKeyword, newItem: SearchHotKeyword): Boolean
                = oldItem.keyword == newItem.keyword

        override fun areContentsTheSame(oldItem: SearchHotKeyword, newItem: SearchHotKeyword): Boolean
                = oldItem.keyword == newItem.keyword
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.context, ItemSearchHotBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}