package org.bubbble.taobao.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import org.bubbble.taobao.R
import org.bubbble.taobao.base.BaseThemeActivity
import org.bubbble.taobao.databinding.ActivitySearchBinding
import org.bubbble.taobao.ui.search.relate.RelateFragment
import org.bubbble.taobao.util.getFragment


class SearchActivity : BaseThemeActivity() {

    private lateinit var binding: ActivitySearchBinding

    private val viewModel: SearchViewModel by viewModels()
    override var isLightStatus = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_search_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.navigation_relate) {
                if (binding.editableBar.visibility != View.VISIBLE) {
                    binding.notEditableBar.visibility = View.INVISIBLE
                    binding.editableBar.visibility = View.VISIBLE
                    showSoftInputFromWindow()
                }
            }
        }

        binding.searchButton.setOnClickListener {
            navController.navigate(R.id.navigation_result)
        }

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                p0?.let {
                    if (it.toString().isNotEmpty() && this@SearchActivity.getFragment(RelateFragment::class.java) != null) {
                        // 更新关键字
                    } else if (it.toString().isNotEmpty() && this@SearchActivity.getFragment(RelateFragment::class.java) == null) {
                        // 导航到RelateFragment并更新关键字
                        navController.navigate(R.id.action_navigation_keyword_to_navigation_relate)
                    } else if (it.toString().isEmpty() && this@SearchActivity.getFragment(RelateFragment::class.java) != null) {
                        // 导航到KeywordFragment
                        navController.navigateUp()
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if(actionId== EditorInfo.IME_ACTION_SEARCH || actionId==0){
                closeKeyboardAndSearch(navController, binding.searchInput.text.toString())
            }
            true
        }

        binding.searchButton.setOnClickListener {
            closeKeyboardAndSearch(navController, binding.searchInput.text.toString())
        }

        binding.searchText.setOnClickListener {
            navController.navigateUp()
        }
    }

    private fun closeKeyboardAndSearch(navController: NavController, keyword: String){
        binding.searchInput.clearFocus()
        val `in` = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        `in`.hideSoftInputFromWindow(binding.searchInput.windowToken, 0)
        navController.navigate(R.id.navigation_result)

        binding.searchText.text = keyword
        if (binding.notEditableBar.visibility != View.VISIBLE) {
            binding.notEditableBar.visibility = View.VISIBLE
            binding.editableBar.visibility = View.INVISIBLE
        }
    }

    private fun showSoftInputFromWindow() {
        binding.searchInput.requestFocus()
        val imm = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
         imm.showSoftInput(binding.searchInput, 0)
    }
}