package org.bubbble.taobao.ui.popular

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE
import org.bubbble.taobao.R
import org.bubbble.taobao.databinding.FragmentPopularBinding
import org.bubbble.taobao.ui.search.SearchActivity
import org.bubbble.taobao.util.doAsync
import org.bubbble.taobao.util.onUI

/**
 * A simple [Fragment] subclass.
 * Use the [PopularFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PopularFragment : Fragment() {

    private lateinit var binding: FragmentPopularBinding

    private var tabItemList = ArrayList<View>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPopularBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            popularHead.searchTerms.run {
                addButton(requireContext(), "显示器4k")
                addButton(requireContext(), "显示器4k 144hz")
                addButton(requireContext(), "显示器4k 曲面")
                addButton(requireContext(), "电脑显示器4k")
                addButton(requireContext(), "显示器4k二手")
                addButton(requireContext(), "显示器27英寸4k")
                addButton(requireContext(), "aoc4k显示器")
                addButton(requireContext(), "显示器4k24寸")
                addButton(requireContext(), "43寸4k显示器")
                addButton(requireContext(), "4k显示器 曲面屏")
                addButton(requireContext(), "lg4k显示器")
            }

            popularHead.searchBox.setOnClickListener {
                startActivity(Intent(requireContext(), SearchActivity::class.java))
            }
        }
        setupBanner()
        setupTabLayout()
    }

    private fun setupBanner() {

        val bannerData = ArrayList<Int>().apply {
            add(R.drawable.ic_taobao)

            add(R.drawable.ic_taobao)
            add(R.drawable.ic_taobao)
            add(R.drawable.ic_taobao)

            add(R.drawable.ic_taobao)
        }

        binding.popularBanner.bannerPager.adapter = BannerViewAdapter(bannerData)
        binding.popularBanner.bannerPager.setCurrentItem(1, false)

        // 主要在这里实现“无限循环的效果”
        binding.popularBanner.bannerPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

                if (position < 1) {

                    doAsync {
                        Thread.sleep(200)
                        onUI {
                            binding.popularBanner.bannerPager.setCurrentItem(bannerData.size - 2, false)
                        }
                    }

                } else if (position > bannerData.size - 2) {

                    doAsync {
                        Thread.sleep(200)
                        onUI {
                            binding.popularBanner.bannerPager.setCurrentItem(1, false)
                        }
                    }

                }
                super.onPageSelected(position)
            }
        })

        val handler = Handler(Looper.getMainLooper())
        // 定时切换页面
        val runnable = Runnable { // 0 1 2 3 4 0 1 2 3 4 ...
            // 4的时候会自动切换到1，0的时候会自动切换到3，所以正确的切换顺序为：1(默认选中) -> 2 3 4 2 3 4 2 3
            val selected = binding.popularBanner.bannerPager.currentItem

            if (selected < bannerData.size - 1) {
                binding.popularBanner.bannerPager.currentItem = selected + 1
            } else {
                binding.popularBanner.bannerPager.currentItem = 2
            }
        }
        handler.postDelayed(runnable, 3000)

        binding.popularBanner.bannerPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("SwitchIntDef")
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                when (state) {
                    SCROLL_STATE_IDLE -> {
                        handler.hasCallbacks(runnable)
                        handler.postDelayed(runnable, 3000)
                    }

                    SCROLL_STATE_DRAGGING -> {
                        handler.removeCallbacks(runnable)
                    }
                }
            }
        })

        binding.popularBanner.bannerIndicator.setViewPager2(binding.popularBanner.bannerPager)
    }

    private fun setupTabLayout() {
        binding.innerHeader.tab.apply {
            addTab(newTab().setCustomView(addTabView("全部", "猜你喜欢")))
            addTab(newTab().setCustomView(addTabView("直播", "网红推荐")))
            addTab(newTab().setCustomView(addTabView("便宜好货", "低价抢购")))
            addTab(newTab().setCustomView(addTabView("买家秀", "购后分享")))
            addTab(newTab().setCustomView(addTabView("全球", "进口好货")))
            addTab(newTab().setCustomView(addTabView("生活", "享受生活")))
            addTab(newTab().setCustomView(addTabView("母婴", "母婴大赏")))
            addTab(newTab().setCustomView(addTabView("时尚", "时尚好货")))
        }
    }

    private fun addTabView(title: String, subTitle: String): View {
        val tabItem = LayoutInflater.from(binding.innerHeader.tab.context).inflate(R.layout.item_popular_tabview, binding.innerHeader.tab, false)
        tabItem.findViewById<TextView>(R.id.title).text = title
        tabItem.findViewById<TextView>(R.id.sub_title).text = subTitle
        tabItemList.add(tabItem)
        return tabItem
    }
}