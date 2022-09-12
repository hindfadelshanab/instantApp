package com.hinddev.pdf.o37i.instantapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
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
                    Log.d("TAG", "onSuccess: "+ deepLink);
                    deepLink.getQueryParameter("email");
                    Log.d("TAG", "handleDeepLink: "+ deepLink.getQueryParameter("email"));


                }
                Log.d("TAG", "onSuccess: "+ deepLink);


            }
        });
    }
}