package com.mitsuki.armory.base

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.lang.RuntimeException

/**
 * 通知渠道便捷管理
 */
class NotificationHelper(context: Context, func: ChannelSet.() -> Unit) {

    private val channelSet: ChannelSet by lazy { ChannelSet() }

    private val mNotificationManager: NotificationManagerCompat =
        NotificationManagerCompat.from(context.applicationContext)

    init {
        this.channelSet.apply(func)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.apply {
                createNotificationChannelGroups(channelSet.group())
                createNotificationChannels(channelSet.channel())
            }
        }
    }

    /**
     * 最好使用该方法发出通知
     */
    fun notify(
        context:Context,
        channelID: String,
        notifyID: Int = -1,
        func: (NotificationCompat.Builder) -> Unit
    ): Int {
        return builder(context, channelID)?.run {
            val nid = if (notifyID < 0) System.currentTimeMillis().toInt() else notifyID
            func(this)
            mNotificationManager.notify(nid, build())
            nid
        } ?: -1
    }

    fun startForeground(
        service: Service,
        channelID: String,
        id: Int,
        func: (NotificationCompat.Builder) -> Unit
    ): Boolean {
        return builder(service as Context, channelID)?.run {
            func(this)
            service.startForeground(id, build())
            true
        } ?: false
    }

    /**
     * 通知权限判断，在缺少权限的时候会获得一个跳往通知设置的intent
     * 部分手机默认关闭通知权限，需要该方法辅助
     */
    fun permission(context: Context, func: Intent.() -> Unit): NotificationHelper? {
        return if (mNotificationManager.areNotificationsEnabled()) {
            this
        } else {
            permissionIntent(context).apply(func)
            null
        }
    }

    fun deleteChannel(channelID: String) {
        channelSet.deleteChannel(channelID)
        mNotificationManager.deleteNotificationChannel(channelID)
    }


    fun deleteGroup(groupID: String) {
        channelSet.deleteGroup(groupID)
        mNotificationManager.deleteNotificationChannelGroup(groupID)
    }

    /**
     * 这里已经包含渠道importance和通知的priority的处理，所以其他地方尽量不要再动
     * 否则可能会造成在不同Android版本下通知优先级的不同步
     */
    private fun builder(context: Context, channelID: String): NotificationCompat.Builder? {
        return channelSet.channel(channelID)?.run {
            return NotificationCompat.Builder(context, id).setPriority(priority)
        }
    }

    private fun permissionIntent(context: Context): Intent {
        return Intent().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                putExtra(Settings.EXTRA_CHANNEL_ID, context.applicationInfo.uid)
            } else {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", context.packageName, null)
            }
        }
    }

    data class ChannelSet(
        private val groupMap: MutableMap<String, ChannelGroupInfo> = hashMapOf(),
        private val channelMap: MutableMap<String, ChannelInfo> = hashMapOf()
    ) {
        @RequiresApi(Build.VERSION_CODES.O)
        internal fun group(): List<NotificationChannelGroup> {
            val list = groupMap.values.toList()
            return List(list.size) {
                list[it].run { notificationChannelGroup }
            }
        }

        internal fun deleteGroup(id: String) = groupMap.remove(id)

        @RequiresApi(Build.VERSION_CODES.O)
        internal fun channel(): List<NotificationChannel> {
            val list = channelMap.values.toList()
            return List(list.size) {
                list[it].run { notificationChannel }
            }
        }

        internal fun channel(id: String): ChannelInfo? = channelMap[id]

        internal fun deleteChannel(id: String) = channelMap.remove(id)

        /**
         * 注意个人需要管理好渠道ID和渠道组ID
         */
        fun channel(
            channelID: String,
            channelName: String,
            channelDes: String? = null,
            channelPriority: Int = NotificationCompat.PRIORITY_DEFAULT,
            groupID: String? = null,
            groupName: String? = null
        ) {
            if (groupID == null) {
                channelMap[channelID] =
                    ChannelInfo(channelID, channelName, channelDes, channelPriority)
            } else {
                if (groupName.isNullOrEmpty()) throw RuntimeException("Need channel group name")
                groupMap[groupID] = ChannelGroupInfo(groupID, groupName)
                channelMap[channelID] =
                    ChannelInfo(channelID, channelName, channelDes, channelPriority, groupID)
            }
        }
    }

    data class ChannelInfo(
        val id: String,
        val name: String,
        val des: String? = null,
        val priority: Int = NotificationCompat.PRIORITY_DEFAULT,
        val groupID: String = ChannelGroupInfo.NO_GROUP
    ) {
        @Suppress("MemberVisibilityCanBePrivate")
        val importance: Int
            @RequiresApi(Build.VERSION_CODES.N)
            get() {
                return when (priority) {
                    NotificationCompat.PRIORITY_MAX, NotificationCompat.PRIORITY_HIGH -> NotificationManager.IMPORTANCE_HIGH
                    NotificationCompat.PRIORITY_LOW -> NotificationManager.IMPORTANCE_LOW
                    NotificationCompat.PRIORITY_MIN -> NotificationManager.IMPORTANCE_MIN
                    else -> NotificationManager.IMPORTANCE_DEFAULT
                }
            }

        val notificationChannel: NotificationChannel
            @RequiresApi(Build.VERSION_CODES.O)
            get() = NotificationChannel(id, name, importance).apply {
                description = des
                if (groupID != ChannelGroupInfo.NO_GROUP)
                    group = groupID
            }
    }

    data class ChannelGroupInfo(
        val id: String,
        val name: String
    ) {

        val notificationChannelGroup: NotificationChannelGroup
            @RequiresApi(Build.VERSION_CODES.O)
            get() = NotificationChannelGroup(id, name)

        companion object {
            const val NO_GROUP = "GROUP_ID_NONE"
        }
    }
}
