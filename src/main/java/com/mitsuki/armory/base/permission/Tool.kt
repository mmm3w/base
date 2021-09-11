package com.mitsuki.armory.base.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat


object Tool {

    fun checkSelfPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun checkSelfPermission(context: Context, permission: Array<String>): Boolean {
        for (item in permission) {
            if (ContextCompat.checkSelfPermission(context, item)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }
}
