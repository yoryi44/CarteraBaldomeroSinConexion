package servicio;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;


import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import co.com.celuweb.carterabaldomero.R;

public class MyNotificaciones extends FirebaseMessagingService {
    private static final String TAG = MyNotificaciones.class.getName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // NotificacionController.instance.mostrarNotificaciones(remoteMessage.getNotification().getBody());
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        //EN CASO DE RECIBIR NOTIFICACIONES
        if (remoteMessage.getNotification() != null) {

            String titulo = remoteMessage.getNotification().getTitle();
            String texto = remoteMessage.getNotification().getBody();


            //MOSTRAR NOTIFICACION EN TIEMPO REAL
            showNotification(titulo, texto);
        }
    }

    private void showNotification(String title, String text) {

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder((Context) MyNotificaciones.this, (Notification) null);

        //VALIDAR PARA VERSIONES DE ANDROID MAYOR A 8
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O)
        {
            NotificationChannel mChannel = new NotificationChannel("channel","ejecutor", NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription("Notificaciones");
            mChannel.enableLights(true);
            mNotificationManager.createNotificationChannel(mChannel);

            notificationBuilder = new NotificationCompat.Builder(this,"channel");

            notificationBuilder.setSmallIcon(R.drawable.fondobotonverdeoscuro)
                    .setContentTitle(title)
                    .setContentText(text);
        }
        else
        {
            notificationBuilder.setSmallIcon(R.drawable.fondobotonverdeoscuro)
                    .setContentTitle(title)
                    .setContentText(text);
        }


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
