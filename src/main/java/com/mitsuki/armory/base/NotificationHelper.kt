package com.mitsuki.armory.base

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
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
object NotificationHelper {
    private val channelSet: ChannelSet by lazy { ChannelSet() }

    private lateinit var mContext: Context
    private lateinit var mNotificationManager: NotificationManagerCompat

    /**
     * 请在application提前初始化渠道和渠道组
     */
    fun initChannel(context: Context, func: ChannelSet.() -> Unit) {
        this.mContext = context
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
        channelID: String,
        notifyID: Int = -1,
        func: (Context, NotificationCompat.Builder) -> Unit
    ): Int {
        return builder(channelID)?.run {
            val nid = if (notifyID < 0) System.currentTimeMillis().toInt() else notifyID
            func(this@NotificationHelper.mContext, this)
            notificationManagerInstance().notify(nid, build())
            nid
        } ?: -1
    }

    /**
     * 通知权限判断，在缺少权限的时候会获得一个跳往通知设置的intent
     * 部分手机默认关闭通知权限，需要该方法辅助
     */
    fun permission(func: Intent.() -> Unit): NotificationHelper? {
        return if (notificationManagerInstance().areNotificationsEnabled()) {
            this
        } else {
            permissionIntent().apply(func)
            null
        }
    }

    fun deleteChannel(channelID: String) {
        channelSet.deleteChannel(channelID)
        notificationManagerInstance().deleteNotificationChannel(channelID)
    }


    fun deleteGroup(groupID: String) {
        channelSet.deleteGroup(groupID)
        notificationManagerInstance().deleteNotificationChannelGroup(groupID)
    }

    /**
     * 这里已经包含渠道importance和通知的priority的处理，所以其他地方尽量不要再动
     * 否则可能会造成在不同Android版本下通知优先级的不同步
     */
    private fun builder(channelID: String): NotificationCompat.Builder? {
        return channelSet.channel(channelID)?.run {
            return NotificationCompat.Builder(mContext, id).setPriority(priority)
        }
    }

    private fun notificationManagerInstance(): NotificationManagerCompat {
        if (this::mNotificationManager.isInitialized) {
            return mNotificationManager
        }
        mNotificationManager = NotificationManagerCompat.from(mContext)
        return mNotificationManager
    }

    private fun permissionIntent(): Intent {
        return Intent().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(Settings.EXTRA_APP_PACKAGE, mContext.packageName)
                putExtra(Settings.EXTRA_CHANNEL_ID, mContext.applicationInfo.uid)
            } else {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", mContext.packageName, null)
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
