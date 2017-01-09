package cn.bjzhou.debugtools.lib;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import cn.bjzhou.debugtools.lib.ui.DebugToolsActivity;

/**
 * author: zhoubinjia
 * date: 2017/1/1
 */
public class DebugTools {

    public static void init(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(context, DebugToolsActivity.class), 0);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.ic_info)
                .setContentTitle("DebugTools")
                .setContentText("DebugTools Running...")
                .setContentIntent(pi)
                .setOngoing(true);
        manager.notify(0, builder.getNotification());
    }
}
