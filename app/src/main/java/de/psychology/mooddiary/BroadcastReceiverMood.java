package de.psychology.mooddiary;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;

public class BroadcastReceiverMood extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


            Toast.makeText(context, "Time to register your Mood", Toast.LENGTH_LONG).show();

            PendingIntent pi = PendingIntent.getActivity(
                    context,
                    0,
                    new Intent(context, Mood.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyMood")
                    .setSmallIcon(R.drawable.example_picture)
                    .setContentTitle("Mood Diary")
                    .setContentText("Time to to register your Mood")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setFullScreenIntent(pi, true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            notificationManager.notify(200, builder.build());


        }

}
