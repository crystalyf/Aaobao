package org.bubbble.taobao.ui.article

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import org.bubbble.taobao.R
import org.bubbble.taobao.databinding.FragmentArticleBinding

/**
 * A simple [Fragment] subclass.
 * Use the [ArticleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ArticleFragment : Fragment() {

    private lateinit var binding: FragmentArticleBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ArticleAdapter { allPrice, isChecked ->
            isChecked?.let {
                binding.checkBox.isChecked = it
            }
            binding.allPrice.text = "$allPrice"
        }
        binding.articleList.layoutManager = LinearLayoutManager(context)
        binding.articleList.adapter = adapter

        val data = mutableListOf<ArticleItem>().apply {
            add(ArticleItem(0, 0,"上海surface精品店", R.drawable.tao_1,
                    "SurfaceBook 2 13寸 GTX1050显卡 8G内存",
                    "i7 13寸版本", "包邮", 5F, 1, 8, false))
            add(ArticleItem(1, 1,"上海surface精品店", R.drawable.tao_2,
                    "SurfaceBook 2 13寸 GTX1050显卡 8G内存",
                    "i7 13寸版本", "包邮", 7F, 2, 8, false))
            add(ArticleItem(1, 2,"上海surface精品店", R.drawable.tao_3,
                    "SurfaceBook 2 13寸 GTX1050显卡 8G内存",
                    "i7 13寸版本", "包邮", 7F, 2, 8, false))
            add(ArticleItem(2, 3,"上海surface精品店", R.drawable.tao_4,
                    "SurfaceBook 2 13寸 GTX1050显卡 8G内存",
                    "i7 13寸版本", "包邮", 3F, 1, 8, false))
            add(ArticleItem(0, 4,"上海surface精品店", R.drawable.tao_5,
                    "SurfaceBook 2 13寸 GTX1050显卡 8G内存",
                    "i7 13寸版本", "包邮", 4F, 2, 8, false))
            add(ArticleItem(1, 5,"上海surface精品店", R.drawable.tao_6,
                    "SurfaceBook 2 13寸 GTX1050显卡 8G内存",
                    "i7 13寸版本", "包邮", 1F, 3, 8, false))
            add(ArticleItem(1, 6,"上海surface精品店", R.drawable.tao_7,
                    "SurfaceBook 2 13寸 GTX1050显卡 8G内存",
                    "i7 13寸版本", "包邮", 9.9F, 1, 8, false))
            add(ArticleItem(1, 7,"上海surface精品店", R.drawable.tao_8,
                    "SurfaceBook 2 13寸 GTX1050显卡 8G内存",
                    "i7 13寸版本", "包邮", 13530F, 1, 8, false))
            add(ArticleItem(2, 8,"上海surface精品店", R.drawable.tao_9,
                    "SurfaceBook 2 13寸 GTX1050显卡 8G内存",
                    "i7 13寸版本", "包邮", 13530F, 1, 8, false))
            add(ArticleItem(0, 9,"上海surface精品店", R.drawable.tao_8,
                    "SurfaceBook 2 13寸 GTX1050显卡 8G内存",
                    "i7 13寸版本", "包邮", 13530F, 1, 8, false))
            add(ArticleItem(1, 10,"上海surface精品店", R.drawable.tao_7,
                    "SurfaceBook 2 13寸 GTX1050显卡 8G内存",
                    "i7 13寸版本", "包邮", 13530F, 2, 8, false))
            add(ArticleItem(1, 11,"上海surface精品店", R.drawable.tao_6,
                    "SurfaceBook 2 13寸 GTX1050显卡 8G内存",
                    "i7 13寸版本", "包邮", 13530F, 2, 8, false))
            add(ArticleItem(1, 12,"上海surface精品店", R.drawable.tao_5,
                    "SurfaceBook 2 13寸 GTX1050显卡 8G内存",
                    "i7 13寸版本", "包邮", 13530F, 3, 8, false))
            add(ArticleItem(1, 13,"上海surface精品店", R.drawable.tao_4,
                    "SurfaceBook 2 13寸 GTX1050显卡 8G内存",
                    "i7 13寸版本", "包邮", 13530F, 1, 8, false))
            add(ArticleItem(1, 14,"上海surface精品店", R.drawable.tao_3,
                    "SurfaceBook 2 13寸 GTX1050显卡 8G内存",
                    "i7 13寸版本", "包邮", 13530F, 1, 8, false))
            add(ArticleItem(1, 15,"上海surface精品店", R.drawable.tao_2,
                    "SurfaceBook 2 13寸 GTX1050显卡 8G内存",
                    "i7 13寸版本", "包邮", 13530F, 1, 8, false))
            add(ArticleItem(1, 16,"上海surface精品店", R.drawable.tao_1,
                    "SurfaceBook 2 13寸 GTX1050显卡 8G内存",
                    "i7 13寸版本", "包邮", 13530F, 1, 8, false))
            add(ArticleItem(2, 17,"上海surface精品店", R.drawable.tao_2,
                    "SurfaceBook 2 13寸 GTX1050显卡 8G内存",
                    "i7 13寸版本", "包邮", 13530F, 1, 8, false))
            add(ArticleItem(0, 18,"上海surface精品店", R.drawable.tao_3,
                    "SurfaceBook 2 13寸 GTX1050显卡 8G内存",
                    "i7 13寸版本", "包邮", 13530F, 5, 8, false))
            add(ArticleItem(1, 19,"上海surface精品店", R.drawable.tao_4,
                    "SurfaceBook 2 13寸 GTX1050显卡 8G内存",
                    "i7 13寸版本", "包邮", 13530F, 3, 8, false))
            add(ArticleItem(1, 20,"上海surface精品店", R.drawable.tao_5,
                    "SurfaceBook 2 13寸 GTX1050显卡 8G内存",
                    "i7 13寸版本", "包邮", 13530F, 2, 8, false))
            add(ArticleItem(1, 21,"上海surface精品店", R.drawable.tao_6,
                    "SurfaceBook 2 13寸 GTX1050显卡 8G内存",
                    "i7 13寸版本", "包邮", 13530F, 1, 8, false))
            add(ArticleItem(1, 22,"上海surface精品店", R.drawable.tao_7,
                    "SurfaceBook 2 13寸 GTX1050显卡 8G内存",
                    "i7 13寸版本", "包邮", 13530F, 10, 8, false))
        }

        binding.checkBox.setOnClickListener {
            adapter.checkedAll(binding.checkBox.isChecked)
        }

        adapter.submitList(data)
    }
}