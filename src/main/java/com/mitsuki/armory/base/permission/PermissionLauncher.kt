package com.mitsuki.armory.base.permission

interface PermissionLauncher {
    fun launch(action: (Boolean) -> Unit)
}