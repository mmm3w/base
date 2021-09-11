package com.mitsuki.armory.base.span

import android.text.Spannable
import android.text.SpannableString

class SpannableBuilder {
    private val mTextStyle: MutableList<StylefulText> = arrayListOf()
    private val mText = StringBuilder()

    fun append(text: String, action: (StylefulText.() -> Unit)? = null): SpannableBuilder {
        if (action != null) {
            StylefulText(text, action).apply {
                start = mText.length
                mText.append(text)
                mTextStyle.add(this)
            }
        } else {
            mText.append(text)
        }
        return this
    }

    fun build(): SpannableString {
        return SpannableString(mText).also { spannable ->
            mTextStyle.forEach { text ->
                text.forEach {
                    spannable.setSpan(it.value, text.start, text.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }
    }


}