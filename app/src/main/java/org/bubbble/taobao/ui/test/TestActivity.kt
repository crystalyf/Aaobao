package org.bubbble.taobao.ui.test

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import org.bubbble.taobao.R
import org.bubbble.taobao.databinding.ActivityTestBinding
import org.bubbble.taobao.ui.tao.flow.TaoFlowFragment

class TestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.run {
            viewPager.adapter = InfoAdapter(supportFragmentManager)
            tabLayout.setupWithViewPager(viewPager)
        }
    }

    companion object {

        private val INFO_TITLES = arrayOf(
                R.string.my_test,
                R.string.system_test,
        )
        private val INFO_PAGES = arrayOf(
                { MyTestFragment() },
                { SystemTestFragment() }
        )
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
            return resources.getString(INFO_TITLES[position]) ?: "null"
        }
    }
}