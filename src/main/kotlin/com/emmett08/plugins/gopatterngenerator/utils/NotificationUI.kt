package com.emmett08.plugins.gopatterngenerator.utils

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

object NotificationUtil {
    private const val GROUP_ID = "Go DesignPattern Generator"

    private fun getNotificationGroup() = NotificationGroupManager.getInstance().getNotificationGroup(GROUP_ID)

    fun notifyError(project: Project?, content: String) {
        getNotificationGroup().createNotification(content, NotificationType.ERROR).notify(project)
    }

    fun notifyInfo(project: Project?, content: String) {
        getNotificationGroup().createNotification(content, NotificationType.INFORMATION).notify(project)
    }

    fun notifyWarn(project: Project?, content: String) {
        getNotificationGroup().createNotification(content, NotificationType.WARNING).notify(project)
    }
}
