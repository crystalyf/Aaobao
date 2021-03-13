package org.bubbble.taobao.ui.tao

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.viewModels
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import org.bubbble.taobao.R
import org.bubbble.taobao.databinding.FragmentTaoBinding
import org.bubbble.taobao.ui.main.MainViewModel
import org.bubbble.taobao.ui.tao.flow.TaoFlowFragment

/**
 * A simple [Fragment] subclass.
 * Use the [TaoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TaoFragment : Fragment() {

    private lateinit var binding: FragmentTaoBinding

    private val activityViewModel: MainViewModel by viewModels()

    private var tabItemList = ArrayList<View>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTabLayout()
        binding.run {
            viewPager.adapter = InfoAdapter(childFragmentManager)
        }
    }

    private fun setupTabLayout() {

        binding.tabLayout.apply {
            addTab(newTab().setCustomView(addTabView(resources.getString(INFO_TITLES[0]), "最新动态")))
            addTab(newTab().setCustomView(addTabView(resources.getString(INFO_TITLES[1]), "新品首发")))
            addTab(newTab().setCustomView(addTabView(resources.getString(INFO_TITLES[2]), "主播在线")))
            addTab(newTab().setCustomView(addTabView(resources.getString(INFO_TITLES[3]), "粉丝活动")))
        }

        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                binding.tabLayout.getTabAt(position)?.select()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.customView?.findViewById<TextView>(R.id.sub_title)?.let {
                    it.setBackgroundResource(R.drawable.background_radius_corners_primary)
                    it.setTextColor(Color.WHITE)
                }
                binding.viewPager.currentItem = tab?.position ?: 0
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.customView?.findViewById<TextView>(R.id.sub_title)?.let {
                    it.setBackgroundResource(R.drawable.background_radius_corners)
                    it.setTextColor(ContextCompat.getColor(it.context, R.color.text_color))
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                tab?.customView?.findViewById<TextView>(R.id.sub_title)?.let {
                    it.setBackgroundResource(R.drawable.background_radius_corners_primary)
                    it.setTextColor(Color.WHITE)
                }
            }
        })

        binding.tabLayout.getTabAt(0)?.select()
    }

    private fun addTabView(title: String, subTitle: String): View {
        val tabItem = LayoutInflater.from(binding.tabLayout.context).inflate(R.layout.item_tao_tabview, binding.tabLayout, false)
        tabItem.findViewById<TextView>(R.id.title).text = title
        tabItem.findViewById<TextView>(R.id.sub_title).text = subTitle
        tabItemList.add(tabItem)
        return tabItem
    }

    companion object {

        private val INFO_TITLES = arrayOf(
                R.string.follow,
                R.string.news,
                R.string.live,
                R.string.welfare,
        )
        private val INFO_PAGES = arrayOf(
                { TaoFlowFragment() },
                { TaoFlowFragment() },
                { TaoFlowFragment() },
                { TaoFlowFragment() }
        )

        const val FOLLOW = 0
        const val NEWS = 1
        const val LIVE = 2
        const val WELFARE = 2
    }

    /**
     * Adapter that builds a page for each info screen.
     */
    inner class InfoAdapter(
            fm: FragmentManager
    ) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getCount() = INFO_PAGES.size

        override fun getItem(position: Int) = INFO_PAGES[position]()

        override fun getPageTitle(position: Int): CharSequence {
            return context?.resources?.getString(INFO_TITLES[position]) ?: "null"
        }
    }
}