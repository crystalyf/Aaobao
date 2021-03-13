package org.bubbble.taobao.ui.popular

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.bubbble.taobao.R

/**
 * @author Andrew
 * @date 2020/09/13 9:44
 */
class BannerViewAdapter(private val dataList: ArrayList<Int>) : RecyclerView.Adapter<BannerViewAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_popular_banner_image, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide.with(holder.imageView).load(dataList[position]).into(holder.imageView)
    }

    override fun getItemCount(): Int = dataList.size
}