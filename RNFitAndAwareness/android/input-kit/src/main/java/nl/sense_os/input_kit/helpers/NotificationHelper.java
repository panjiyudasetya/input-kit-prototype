package nl.sense_os.input_kit.helpers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import nl.sense_os.input_kit.R;

/**
 * Created by panjiyudasetya on 5/12/17.
 */

public class NotificationHelper {

   public static void createNotification(@NonNull Context context,
                                         @NonNull String title,
                                         @NonNull String content,
                                         int notificationId) {
       
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(content);
        NotificationManagerCompat.from(context).notify(notificationId, notificationBuilder.build());
    }
}
