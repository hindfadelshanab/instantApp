package com.hinddev.pdf.o37i.instantapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.instantapps.InstantApps;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.hinddev.pdf.o37i.instantapp.databinding.ActivitySignInBinding;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth ;

    ActivitySignInBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


//        Intent intent = getIntent();
//        if (intent != null) {
//            handleDeepLink(intent);
//        }
        if(!InstantApps.getPackageManagerCompat(this).isInstantApp()) {
            Log.d("TAG", "onCreate: yrssssd ");
//            tvNoon.text = formatTimeString(this, R.string.noon_format, sunTimetable?.noon)
//            tvDayLength.text = formatTimeString(this, R.string.day_length, sunTimetable?.dayLength)
        }



    }

    private void signIn() {
        mAuth.signInWithEmailAndPassword(binding.inputEmail.getText().toString() , binding.inputPassword.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(SignInActivity.this, "Done", Toast.LENGTH_SHORT).show();

//                        startActivity(new Intent(SignInActivity.this , OrderActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        if (intent != null) {
//            handleDeepLink(intent);
//        }
//    }
//    private void handleDeepLink(Intent intent) {
//        FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnSuccessListener(pendingDynamicLinkData -> {
//            if (pendingDynamicLinkData != null) {
//                Uri deepLink = pendingDynamicLinkData.getLink();
//                if (deepLink != null) {
//                    Log.d("TAG", "onSuccess: "+ deepLink);
//                    deepLink.getQueryParameter("email");
//                    Log.d("TAG", "handleDeepLink: "+ deepLink.getQueryParameter("email"));
//
//
//                }
//                Log.d("TAG", "onSuccess: "+ deepLink);
//
//
//            }
//        });
//    }
}