package com.hinddev.pdf.o37i.instantapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editText = findViewById(R.id.editTextTextPersonName);

        Intent intent = getIntent();
        if (intent != null) {
            handleDeepLink(intent);
        }

        String link=null;
        if (getIntent().getData()!=null){
            link=getIntent().getData().toString();
            editText.setText(link.toString());
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
                    Log.d("TAG", "onSuccess: "+ deepLink);


                }
                Log.d("TAG", "onSuccess: "+ deepLink);


            }
        });
    }
}