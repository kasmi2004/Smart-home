package com.iota.mohammad.myapplication;



import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class AlarmService extends Service
{
//    private MqttAndroidClient client;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // your code
        final MqttAndroidClient client;

        client = new MqttAndroidClient(getApplicationContext(), "tcp://thingtalk.ir:1883", "smarties");
        try {
            Log.d("Connedted ex", "trying to connect");
            client.connect(null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("Connedted ex", "ok");
                    try {
                        Log.d("Connedted", "ok");
                        client.subscribe("smarties",1);
                        client.setCallback(new MqttCallback() {
                            @Override
                            public void connectionLost(Throwable cause) {
                            }

                            @Override
                            public void messageArrived(String topic, MqttMessage message) throws Exception {
                                if ((new String(message.getPayload())).equals("smoke detected")) {
                                    String s = "خطر آتش‌سوزی!";
                                    displayNotification(s);
                                }
                                if ((new String(message.getPayload())).equals("door is opened")) {
                                    String s = "یکی وارد شد";
                                    displayNotification(s);
                                }
                            }
                            @Override
                            public void deliveryComplete(IMqttDeliveryToken token) {

                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                        Log.d("Connedted ex", String.valueOf(e.getMessage()));
                    }
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            Log.d("Connedted", String.valueOf(e.getMessage()));
        }
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    protected void displayNotification(String s)
    {

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(this)
                .setContentTitle(s)
                .setSmallIcon(R.drawable.fire)
                .setTicker("اعلان جدید!")
                .setAutoCancel(true) // hide the notification after its selected
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(pIntent)
                .build();

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent1 = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
//        notification.setLatestEventInfo(getApplicationContext(), "یکی پشته در می‌باشد.", "باز کن براش", intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);

    }
}

