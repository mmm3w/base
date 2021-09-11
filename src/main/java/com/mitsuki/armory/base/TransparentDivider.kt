package com.mitsuki.armory.base

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class TransparentDivider(private val px: Int) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        if (parent.layoutManager is LinearLayoutManager) {
            val childPosition = parent.getChildAdapterPosition(view)
            val itemCount = parent.adapter!!.itemCount
            when ((parent.layoutManager as LinearLayoutManager).orientation) {
                LinearLayoutManager.VERTICAL -> {
                    if (childPosition == itemCount - 1)
                        outRect.set(0, 0, 0, 0)
                    else
                        outRect.set(0, 0, 0, px)
                }
                LinearLayoutManager.HORIZONTAL -> {
                    if (childPosition == itemCount - 1)
                        outRect.set(0, 0, 0, 0)
                    else
                        outRect.set(0, 0, px, 0)
                }
            }
        }
    }
}