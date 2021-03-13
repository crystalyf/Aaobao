package org.bubbble.taobao.ui.search.keyword

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import org.bubbble.taobao.R
import org.bubbble.taobao.data.SearchHotKeyword
import org.bubbble.taobao.data.SearchHotKeywordData
import org.bubbble.taobao.databinding.FragmentKeywordBinding
import org.bubbble.taobao.ui.search.SearchHotKeywordAdapter
import org.bubbble.taobao.util.doAsync
import org.bubbble.taobao.util.dp
import org.bubbble.taobao.util.onUI

/**
 * A simple [Fragment] subclass.
 * Use the [KeywordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class KeywordFragment : Fragment() {

    private lateinit var binding: FragmentKeywordBinding

    private val adapter = SearchHotKeywordAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentKeywordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {

            val tagsHistory = arrayOf("SurfaceBook", "Google Pixel 5", "佳能 M6", "Macbook pro", "8848 钛金手机", "蚊子尸体", "新鲜空气", "拓展坞")
            for (value in tagsHistory) {
                searchHistoryGroup.addView(Chip(searchHistoryGroup.context).apply {
                    text = value
                    isClickable = true
                    isFocusable = true
                    chipMinHeight = 36F.dp
                    chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.background_white_secondary)
                })
            }


            val tagsMore = arrayOf("Surface Pro", "Pixel 3XL", "Essential Phone PH-1", "iPad Pro", "天文望远镜", "空气加湿器", "佳能套机镜头")
            for (value in tagsMore) {
                searchMoreGroup.addView(Chip(searchMoreGroup.context).apply {
                    text = value
                    isClickable = true
                    isFocusable = true
                    chipMinHeight = 36F.dp
                    chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.background_white_secondary)
                })
            }

            hotList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            hotList.adapter = adapter
        }

        setupHotKeyword()
    }

    private fun setupHotKeyword() {
        doAsync {
            val data = mutableListOf<SearchHotKeyword>().apply {
                add(SearchHotKeyword(mutableListOf<SearchHotKeywordData>().apply {
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                }))
                add(SearchHotKeyword(mutableListOf<SearchHotKeywordData>().apply {
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                }))
                add(SearchHotKeyword(mutableListOf<SearchHotKeywordData>().apply {
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                }))
                add(SearchHotKeyword(mutableListOf<SearchHotKeywordData>().apply {
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                }))
                add(SearchHotKeyword(mutableListOf<SearchHotKeywordData>().apply {
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                }))
                add(SearchHotKeyword(mutableListOf<SearchHotKeywordData>().apply {
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                    add(SearchHotKeywordData("关键字", "0"))
                }))
            }

            onUI {
                adapter.submitList(data)
            }
        }
    }
}