package com.mitsuki.armory.base.extend

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes
import androidx.core.view.*
import androidx.recyclerview.widget.RecyclerView


fun View.paddingVertical() = paddingTop + paddingBottom

fun View.paddingHorizontal() = paddingStart + paddingEnd

fun View.marginVertical() = marginTop + marginBottom

fun View.marginHorizontal() = marginStart + marginEnd

fun RecyclerView.addOnScrollListenerBy(
    onScrollStateChanged: ((recyclerView: RecyclerView, newState: Int) -> Unit)? = null,
    onScrolled: ((recyclerView: RecyclerView, dx: Int, dy: Int) -> Unit)? = null
) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            onScrollStateChanged?.invoke(recyclerView, newState)
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            onScrolled?.invoke(recyclerView, dx, dy)
        }
    })
}

fun <V : View> RecyclerView.ViewHolder.view(@IdRes id: Int): V? {
    return itemView.findViewById(id)
}

fun Activity.paddingStatusBarHeight(@IdRes id: Int) {
    findViewById<View>(id)?.apply { paddingStatusBarHeight(this) }
}

fun Activity.paddingStatusBarHeight(view: View) {
    with(view) {
        setPadding(paddingLeft, paddingTop + statusBarHeight(), paddingRight, paddingBottom)
    }
}

fun Activity.marginStatusBarHeight(@IdRes id: Int) {
    findViewById<View>(id)?.apply { marginStatusBarHeight(this) }
}

fun Activity.marginStatusBarHeight(view: View) {
    with(view) {
        (layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(
            marginLeft, marginTop + statusBarHeight(), marginRight, marginBottom
        )
    }
}

fun View.showSoftKeyboard() {
    if (requestFocus()) {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
            ?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun View.hideSoftKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}