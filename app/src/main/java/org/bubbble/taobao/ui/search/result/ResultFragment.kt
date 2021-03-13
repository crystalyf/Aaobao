package org.bubbble.taobao.ui.search.result

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import org.bubbble.taobao.R
import org.bubbble.taobao.databinding.FragmentResultBinding
import org.bubbble.taobao.ui.search.SearchViewModel
import org.bubbble.taobao.ui.tao.TaoFragment
import org.bubbble.taobao.util.load

/**
 * A simple [Fragment] subclass.
 * Use the [ResultFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ResultFragment : Fragment() {

    private lateinit var binding: FragmentResultBinding

    private val activityViewModel: SearchViewModel by activityViewModels()

    private var tabItemList = ArrayList<View>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ResultAdapter()
        binding.resultList.layoutManager = LinearLayoutManager(context)
        binding.resultList.adapter = adapter

        adapter.submitList(mutableListOf(
            ResultItem(1),
            ResultItem(2),
            ResultItem(3),
            ResultItem(4),
            ResultItem(5),
            ResultItem(6),
            ResultItem(7),
            ResultItem(8),
            ResultItem(9),
            ResultItem(10),
            ResultItem(11),
            ResultItem(12),
            ResultItem(13),
            ResultItem(14),
            ResultItem(15),
            ResultItem(16),
            ResultItem(17),
            ResultItem(18),
            ResultItem(19),
            ResultItem(20),
            ResultItem(21),
            ResultItem(22)))

        binding.switchLayout.setOnClickListener {
            if (binding.resultList.layoutManager is GridLayoutManager) {
                binding.switchLayout.load(R.drawable.ic_cascades)
                adapter.itemType = Result.SINGLE
                binding.resultList.layoutManager = LinearLayoutManager(context)
            } else {
                binding.switchLayout.load(R.drawable.ic_list)
                adapter.itemType = Result.TWIN
                binding.resultList.layoutManager = GridLayoutManager(context, 2)
            }
        }

        binding.resultTab.apply {
            addTab(newTab().setCustomView(addTabView( "全部")))
            addTab(newTab().setCustomView(addTabView( "天猫")))
            addTab(newTab().setCustomView(addTabView( "店铺")))
            addTab(newTab().setCustomView(addTabView( "淘宝经验")))
        }
    }


    private fun addTabView(title: String): View {
        val tabItem = LayoutInflater.from(binding.resultTab.context).inflate(R.layout.item_text_tabview, binding.resultTab, false)
        tabItem.findViewById<TextView>(R.id.title).text = title
        tabItemList.add(tabItem)
        return tabItem
    }
}