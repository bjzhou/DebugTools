package cn.bjzhou.debugtools.lib;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

import cn.bjzhou.debugtools.lib.ui.DebugToolsActivity;

/**
 * author: zhoubinjia
 * date: 2017/1/1
 */
public class DebugTools {

    public static void init(Context context) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(context, DebugToolsActivity.class), 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_info)
                .setContentTitle("DebugTools")
                .setContentText("DebugTools Running...")
                .setContentIntent(pi)
                .setOngoing(true);
        manager.notify(0, builder.build());
    }
}
