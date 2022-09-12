package com.hinddev.pdf.o37i.instantapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class OrderActivity extends AppCompatActivity {
    Timer timer = new Timer(); // changed

    ImageView imageView ;
    TextView textView ;
    private MediaPlayer mediaPlayer;
    private NotificationManager mNotificationManager1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        imageView = findViewById(R.id.imageViewAlert);
        textView = findViewById(R.id.txtOrderName);

        Intent intent = getIntent();
        if (intent != null) {
            handleDeepLink(intent);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            handleDeepLink(intent);
        }
    }

    private void handleDeepLink(Intent intent) {
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnSuccessListener(pendingDynamicLinkData -> {
            if (pendingDynamicLinkData != null) {
                Uri deepLink = pendingDynamicLinkData.getLink();
                if (deepLink != null) {

                    String id = deepLink.getQueryParameter("orderId");
                    getOrder(id);

                    Log.d("TAG", "handleDeepLink: " +id);
                    Log.d("TAG", "handleDeepLink: " +deepLink);

                }
                Log.d("TAG", "onSuccess: " + deepLink);


            }
        });
    }

    private void getOrder(String id) {


        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Order").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d("TAG", "onSuccess:  firebase" );
                        boolean stats = documentSnapshot.getBoolean("stats");
                        String name = documentSnapshot.getString("name");
                        Log.d("TAG", "onSuccess: "+stats);
                        if (stats) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    imageView.setVisibility(View.VISIBLE);
                                    textView.setVisibility(View.VISIBLE);
                                    textView.setText(name);
//
                                    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.notification);
                                    mediaPlayer.start();
                                    startService();
//                                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                                    v.vibrate(1000);
                                }
                            });

                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    imageView.setVisibility(View.GONE);
                                    textView.setVisibility(View.GONE);
                                    stopService();

                                }
                            });
                        }
                    }
                });


//                if (i < mImageUrl.size()) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            // TODO Auto-generated method stub
//                            mImageLoader.DisplayImage(mImageUrl.get(i), img);
//                            i++;
//                        }
//                    });
//                } else {
//                    i = 0;
//                }
            }
        }, 0, 3000);

    }


    public void startService() {

        Intent serviceIntent = new Intent(this, MyService.class);


        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, MyService.class);
        stopService(serviceIntent);
    }



}