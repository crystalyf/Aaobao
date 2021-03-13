package org.bubbble.taobao.ui.article

/**
 * @author Andrew
 * @date 2020/11/19 16:04
 */
data class ArticleItem (
        val type: Int,
        val id: Int,
        val shopName: String,
        val pictures: Int,
        val articleName: String,
        val subName: String,
        val tag: String,
        val singlePrice: Float,
        var quantity: Int,
        val maxQuantity: Int,
        val isLast: Boolean
) {
}