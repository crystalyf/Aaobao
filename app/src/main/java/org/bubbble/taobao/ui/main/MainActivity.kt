package org.bubbble.taobao.ui.main


import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.iterator
import androidx.core.view.setPadding
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import org.bubbble.taobao.R
import org.bubbble.taobao.base.BaseThemeActivity
import org.bubbble.taobao.util.dp
import org.bubbble.taobao.util.firstFragmentGraphId
import org.bubbble.taobao.util.logger
import org.bubbble.taobao.util.setupWithNavController


@AndroidEntryPoint
class MainActivity : BaseThemeActivity() {

    private var currentNavController: LiveData<NavController>? = null

    override var isLightStatus = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)

        val navGraphIds = listOf(
            R.navigation.popular,
            R.navigation.tao,
            R.navigation.message,
            R.navigation.article,
            R.navigation.person
        )

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )

        var menuView: BottomNavigationMenuView? = null
        for (i in 0 until bottomNavigationView.childCount) {
            val child: View = bottomNavigationView.getChildAt(i)
            if (child is BottomNavigationMenuView) {
                menuView = child
                break
            }
        }

        var imageView: ImageView? = null
        val otherItemView = mutableListOf<View>()
        menuView?.let { item ->
            val itemView = item.getChildAt(0) as BottomNavigationItemView
            itemView.iterator().forEach {
                it.visibility = View.GONE
                otherItemView.add(it)
            }
            imageView = ImageView(itemView.context).apply {
                val layoutParam =  ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                setImageResource(R.drawable.ic_taobao)
                setPadding(8.dp)
                layoutParams = layoutParam
            }
            itemView.addView(imageView)
        }

        // Whenever the selected controller changes, setup the action bar.
        controller.observe(this, { navController ->
            logger("${navController.graph.id} | ${R.id.popular}")
            if (navController.graph.id == firstFragmentGraphId) {
                imageView?.visibility = View.VISIBLE
                for (view in otherItemView) {
                    view.visibility = View.GONE
                }
            } else {
                imageView?.visibility = View.GONE
                for (view in otherItemView) {
                    view.visibility = View.VISIBLE
                }
            }
        })

        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }
}