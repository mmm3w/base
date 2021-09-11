package com.mitsuki.armory.base.permission

import android.Manifest
import androidx.activity.ComponentActivity

fun ComponentActivity.permissionLauncher(permission: String) =
    PermissionLauncherDelegate(permission)

fun ComponentActivity.multiplePermissionLauncher(permission: Array<String>) =
    MultiplePermissionLauncherDelegate(permission)

fun ComponentActivity.readStorePermissionLauncher() =
    permissionLauncher(Manifest.permission.READ_EXTERNAL_STORAGE)

fun ComponentActivity.writeStorePermissionLauncher() =
    permissionLauncher(Manifest.permission.WRITE_EXTERNAL_STORAGE)

fun ComponentActivity.storePermissionLauncher() = multiplePermissionLauncher(
    arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
)


