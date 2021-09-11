package com.mitsuki.armory.base.extend

import android.os.Message
import java.lang.ref.Reference
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

fun <T> weak(initializer: () -> T) = Weak(initializer())

fun <T> soft(initializer: () -> T) = Soft(initializer())

class Weak<T>(r: T) {
    private var reference: WeakReference<T?> = WeakReference(r)

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? = reference.get()

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        this.reference = WeakReference(value)
    }
}

class Soft<T>(r: T) {
    private var reference: SoftReference<T?> = SoftReference(r)

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? = reference.get()

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        this.reference = SoftReference(value)
    }
}



