package com.mitsuki.armory.base.permission

import androidx.activity.ComponentActivity
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class MultiplePermissionLauncherDelegate(private val permission: Array<String>) :
    ReadOnlyProperty<ComponentActivity, PermissionLauncher> {

    private val launcherImpl = MultiplePermissionLauncherImpl(thisRef, permission)

    override fun getValue(thisRef: ComponentActivity, property: KProperty<*>): PermissionLauncher {
        return launcherImpl
    }
}