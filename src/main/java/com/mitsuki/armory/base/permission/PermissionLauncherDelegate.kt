package com.mitsuki.armory.base.permission

import androidx.activity.ComponentActivity
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class PermissionLauncherDelegate(private val permission: String) :
    ReadOnlyProperty<ComponentActivity, PermissionLauncher> {
    override fun getValue(thisRef: ComponentActivity, property: KProperty<*>): PermissionLauncher {
        return PermissionLauncherImpl(thisRef, permission)
    }
}