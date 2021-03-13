package org.bubbble.taobao.ui.tao.flow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import org.bubbble.taobao.R
import org.bubbble.taobao.databinding.FragmentTaoFlowBinding

/**
 * A simple [Fragment] subclass.
 * Use the [TaoFlowFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TaoFlowFragment : Fragment() {

    private lateinit var binding: FragmentTaoFlowBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentTaoFlowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TaoFlowAdapter()
        binding.taoList.layoutManager = LinearLayoutManager(context)
        binding.taoList.adapter = adapter

        val shopList = mutableListOf(R.drawable.shop_1, R.drawable.shop_2, R.drawable.shop_3, R.drawable.shop_4, R.drawable.shop_5, R.drawable.shop_6, R.drawable.shop_7, R.drawable.shop_8)
        val list = mutableListOf(
                TaoFlowItem(0, 0, shopList),
                TaoFlowItem(1, 0, null),
                TaoFlowItem(1, 1, null),
                TaoFlowItem(1, 2, null),
                TaoFlowItem(1, 3, null),
                TaoFlowItem(1, 4, null),
                TaoFlowItem(1, 5, null),
                TaoFlowItem(1, 6, null),
                TaoFlowItem(1, 7, null),
                TaoFlowItem(1, 8, null))

        adapter.submitList(list)
    }
}