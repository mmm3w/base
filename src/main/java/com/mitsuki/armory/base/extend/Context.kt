package com.mitsuki.armory.base.extend

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.res.use
import androidx.fragment.app.Fragment

/** toast *****************************************************************************************/
fun Context.toast(value: String, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, value, duration).show()

fun Fragment.toast(value: String, duration: Int = Toast.LENGTH_SHORT) =
    context?.toast(value, duration)

/**************************************************************************************************/

@ColorInt
@SuppressLint("Recycle")
fun Context.themeColor(
    @AttrRes themeAttrId: Int
): Int {
    return obtainStyledAttributes(
        intArrayOf(themeAttrId)
    ).use {
        it.getColor(0, Color.MAGENTA)
    }
}

