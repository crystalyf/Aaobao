package org.bubbble.taobao.ui.test

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.bubbble.taobao.R

/**
 * A simple [Fragment] subclass.
 * Use the [MyTestFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyTestFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_test, container, false)
    }
}