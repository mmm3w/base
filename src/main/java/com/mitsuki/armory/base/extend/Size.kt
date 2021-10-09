package com.mitsuki.armory.base.extend

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics

fun dp2px(dpValue: Float): Float {
    return 0.5f + dpValue * Resources.getSystem().displayMetrics.density
}

fun px2dp(pxValue: Float): Float {
    return pxValue / Resources.getSystem().displayMetrics.density
}

fun Context.statusBarHeight(): Int {
    val id = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (id > 0)
        return resources.getDimensionPixelSize(id)
    return 0
}

fun Context.navigationBarHeight(): Int {
    val id = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    if (id > 0)
        return resources.getDimensionPixelSize(id)
    return 0
}


val Activity.rotation: Int
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) display!!.rotation
    else windowManager.defaultDisplay.rotation


@Suppress("DEPRECATION")
val Activity.screenWidth: Int
    get() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            DisplayMetrics().apply { display?.getRealMetrics(this) }.widthPixels
        else
            DisplayMetrics().apply { windowManager.defaultDisplay.getRealMetrics(this) }.widthPixels

@Suppress("DEPRECATION")
val Activity.screenHeight: Int
    get() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            DisplayMetrics().apply { display?.getRealMetrics(this) }.heightPixels
        else
            DisplayMetrics().apply { windowManager.defaultDisplay.getRealMetrics(this) }.heightPixels

@Suppress("DEPRECATION")
val Activity.displayWidth: Int
    get() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            windowManager.currentWindowMetrics.bounds.width()
        else
            DisplayMetrics().apply { windowManager.defaultDisplay.getMetrics(this) }.widthPixels

@Suppress("DEPRECATION")
val Activity.displayHeight: Int
    get() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            windowManager.currentWindowMetrics.bounds.height()
        else
            DisplayMetrics().apply { windowManager.defaultDisplay.getMetrics(this) }.heightPixels
