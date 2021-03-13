package org.bubbble.taobao.ui.search.relate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import org.bubbble.taobao.R
import org.bubbble.taobao.databinding.FragmentRelateBinding


/**
 * A simple [Fragment] subclass.
 * Use the [RelateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RelateFragment : Fragment() {

    private lateinit var binding: FragmentRelateBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentRelateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RelateAdapter()
        binding.relateList.layoutManager = LinearLayoutManager(context)
        binding.relateList.adapter = adapter
        binding.relateList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
            ContextCompat.getDrawable(binding.relateList.context, R.drawable.divider_2px)?.let {
                setDrawable(it)
            }
        })

        adapter.submitList(mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11))
    }
}