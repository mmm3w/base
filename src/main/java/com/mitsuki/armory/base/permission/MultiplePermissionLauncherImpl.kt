package com.mitsuki.armory.base.permission

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import java.lang.ref.WeakReference

class MultiplePermissionLauncherImpl(
    val activity: ComponentActivity,
    val permission: Array<String>
) :
    PermissionLauncher {

    private val launcher =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {

        }

    private var cbReference: WeakReference<(Boolean) -> Unit>? = null

    override fun launch(action: (Boolean) -> Unit) {
        if (Tool.checkSelfPermission(activity, permission)) {
            action.invoke(true)
            return
        }
        cbReference = WeakReference(action)
        launcher.launch(permission)
    }
}