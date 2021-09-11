package com.mitsuki.armory.base.span

import android.graphics.Typeface
import android.text.style.*
import android.view.View
import androidx.annotation.ColorInt

@Suppress("SpellCheckingInspection")
class StylefulText(val text: String, action: StylefulText.() -> Unit) {

    companion object {
        const val FOREGROUND_COLOR = "FOREGROUND_COLOR"
        const val BACKGROUND_COLOR = "BACKGROUND_COLOR"
        const val UNDERLINE = "UNDERLINE"
        const val STRIKETHROUGH = "STRIKETHROUGH"
        const val TEXT_SIZE = "TEXT_SIZE"
        const val TEXT_CLICKABLE = "TEXT_CLICKABLE"
        const val ITALIC = "ITALIC"
        const val BOLD = "BOLD"
        const val CUSTOM = "CUSTOM"
    }

    val length: Int
        get() = text.length

    var start: Int = 0
    val end: Int get() = start + length

    private val mStyleMap: MutableMap<String, CharacterStyle> = mutableMapOf()

    init {
        this.action()
    }

    fun textColor(@ColorInt color: Int) {
        mStyleMap[FOREGROUND_COLOR] = ForegroundColorSpan(color)
    }

    fun backgroundColor(@ColorInt color: Int) {
        mStyleMap[BACKGROUND_COLOR] = BackgroundColorSpan(color)
    }

    @Deprecated("extend to 2 funtion", ReplaceWith("absoluteSize(px)"))
    fun size(px: Int) {
        absoluteSize(px)
    }

    fun absoluteSize(px: Int) {
        mStyleMap[TEXT_SIZE] = AbsoluteSizeSpan(px)
    }

    fun relativeSize(proportion: Float) {
        mStyleMap[TEXT_SIZE] = RelativeSizeSpan(proportion)
    }

    fun click(action: (View) -> Unit) {
        mStyleMap[TEXT_CLICKABLE] = object : ClickableSpan() {
            override fun onClick(widget: View) {
                action(widget)
            }
        }
    }

    fun underline() {
        mStyleMap[UNDERLINE] = UnderlineSpan()
    }

    fun strikethrough() {
        mStyleMap[STRIKETHROUGH] = StrikethroughSpan()
    }

    fun italic() {
        mStyleMap[ITALIC] = StyleSpan(Typeface.ITALIC)
    }

    fun bold() {
        mStyleMap[BOLD] = StyleSpan(Typeface.BOLD)
    }

    fun custom(tag: String, provider: () -> CharacterStyle) {
        mStyleMap["$CUSTOM$tag"] = provider()
    }

    fun forEach(action: (Map.Entry<String, CharacterStyle>) -> Unit) {
        mStyleMap.forEach(action)
    }
}