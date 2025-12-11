// NotificationHelper.kt
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

/**
 * Notification helper, contains the logic of creating and sending notifications.
 * used in RouteScreenViewModel
 */
class NotificationHelper(private val application: Application) {

    companion object {
        private const val CHANNEL_ID = "my_app_default_channel"
        private const val CHANNEL_NAME = "default notification channel"
        private const val NOTIFICATION_ID = 1001 // Notification ID

    }

    /**
     * send a simple notification
     * @param title notification title
     * @param message notification message
     * @param targetActivityClass click notification to open this activity
     */
    fun sendSimpleNotification(
        title: String,
        message: String,
        targetActivityClass: Class<*>? = null
    ) {
        // 1. creat the notification channel（Android 8.0+）
        createNotificationChannel()

        // 2. creat the notification body
        val notification = buildNotification(title, message, targetActivityClass)

        // 3. get NotificationManager and send the notification
        val notificationManager = application.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        Log.d("NotificationHelper", "Sending notification: $title, $message")

        // 4. send the notification
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    /**
     * creat the notification channel（Android 8.0+）
     */
    private fun createNotificationChannel() {
        // only in Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT // Default importance level
            ).apply {
                description = "basic notification channel"
                enableLights(true) // Turn on the indicator light (if supports).
                lightColor = ContextCompat.getColor(application, android.R.color.holo_blue_light)
            }

            val notificationManager = application.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Build the notification
     */
    private fun buildNotification(
        title: String,
        message: String,
        targetActivityClass: Class<*>?
    ): android.app.Notification {
        // Create intent for the target activity
        val targetClass = targetActivityClass ?: try {
            // get the main activity class
            val packageManager = application.packageManager
            val intent = packageManager.getLaunchIntentForPackage(application.packageName)
            intent?.component?.let { componentName ->
                Class.forName(componentName.className)
            } ?: throw ClassNotFoundException("Cannot find main Activity")
        } catch (e: Exception) {
            // if failed, return null
            e.printStackTrace()
            null
        }

        val pendingIntent = targetClass?.let {
            val intent = Intent(application, it).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            PendingIntent.getActivity(
                application,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        // Use NotificationCompat.Builder to build the notification
        return NotificationCompat.Builder(application, CHANNEL_ID)
            .setSmallIcon(getNotificationIcon()) // Icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Set the priority
            .apply {
                pendingIntent?.let { setContentIntent(it) }
            }
            .setAutoCancel(true) // Click to cancel the notification
            .build()
    }

    /**
     * Get a notification icon from the mipmap resources
     */
    private fun getNotificationIcon(): Int {
        // Get the application's package name from the context
        val resourceId = application.resources.getIdentifier(
            "ic_notification",
            "mipmap",
            application.packageName
        )

        return if (resourceId != 0) {
            resourceId
        } else {
            // If the icon is not found, return a default icon
            android.R.drawable.ic_dialog_info
        }
    }

    /**
     * Cancel the notification
     */
    fun cancelNotification() {
        val notificationManager = application.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }
}