package com.devhind.o37i.instant_faeture;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.verify.domain.DomainVerificationManager;
import android.content.pm.verify.domain.DomainVerificationUserState;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.devhind.o37i.instant_faeture.databinding.ActivityOrderBinding;
import com.google.android.gms.instantapps.InstantApps;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.hinddev.pdf.o37i.instantapp.MyService;
import com.hinddev.pdf.o37i.instantapp.adapter.OrderAdapter;
import com.hinddev.pdf.o37i.instantapp.listener.OnOrderReadyListener;
import com.hinddev.pdf.o37i.instantapp.model.Order;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;

public class OrderActivity extends AppCompatActivity implements OnOrderReadyListener {

    RecyclerView recyclerView;
    private MediaPlayer mediaPlayer;

    public String orderUri = "https://wiz-tech.co/?orderId=2";

    ArrayList<Order> orders = new ArrayList<>();
    OrderAdapter orderAdapter;

    ActivityOrderBinding binding;
    private Query mQuery;


    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        Branch.getAutoInstance(this);

        if (InstantApps.getPackageManagerCompat(this).isInstantApp()) {
            Log.d("TAG", "onCreate: yess ");
//            Toast.makeText(this, "this is Instant App", Toast.LENGTH_SHORT).show();

        }

        showInstantsInstall();


        //
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.image1.setVisibility(View.GONE);
                binding.image2.setVisibility(View.GONE);
                binding.smallText.setVisibility(View.GONE);
                binding.orderRv.setVisibility(View.VISIBLE);
                binding.btnAddOrder.setVisibility(View.VISIBLE);
                binding.imageAdd.setVisibility(View.VISIBLE);
                binding.imageHome.setVisibility(View.VISIBLE);
                binding.smallTextHome.setVisibility(View.VISIBLE);
                binding.btnInstall.setVisibility(View.VISIBLE);

            }
        }, 3000);
        orderAdapter = new OrderAdapter(OrderActivity.this, this);


        Intent intent = getIntent();
        if (intent != null) {
            handleDeepLink(intent);
        }
        binding.btnAddOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IntentIntegrator intentIntegrator = new IntentIntegrator(OrderActivity.this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.initiateScan();
            }
        });


        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();

        if (appLinkData != null) {
            Log.d("TAG", "onCreate: " + appLinkData.toString());


            Log.d("TAG", "appLinkData param: " + appLinkData.getQueryParameter("orderId"));
        }
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        if (intent != null) {
//            handleDeepLink(intent);
//        }
//    }

    private void handleDeepLink(Intent intent) {
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnSuccessListener(pendingDynamicLinkData -> {
            if (pendingDynamicLinkData != null) {
                Uri deepLink = pendingDynamicLinkData.getLink();
                if (deepLink != null) {
                    pendingDynamicLinkData.getRedirectUrl();

//                    Log.d("TAG", "getRedirectUrl: " + pendingDynamicLinkData.getRedirectUrl());
                    String id = deepLink.getQueryParameter("orderId");

                    getOrder(id);
//
//                    Log.d("TAG", "handleDeepLink: " + id);
//                    Log.d("TAG", "handleDeepLink: " + deepLink);

                }
//                Log.d("TAG", "onSuccess: " + deepLink);


            }
        });
    }

    private void getOrder(String id) {


        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection("Order").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
//                    Log.w("TAG", "onEvent:error"+ error);
//                    onError(e);
                    return;
                }

                // Dispatch the event
                for (DocumentChange change : value.getDocumentChanges()) {

                    if (change.getDocument().toObject(Order.class).getId().equals(id)) {
                        switch (change.getType()) {
                            case ADDED:
                                orderAdapter.onOrderAdded(change);
                                break;
                            case MODIFIED:
                                orderAdapter.onDocumentModified(change);
                                break;
                            case REMOVED:
                                orderAdapter.onDocumentRemoved(change);
                                break;
                        }

                    }
                }

            }

        });
        initAdapter();


    }


    private void initAdapter() {

        binding.orderRv.setAdapter(orderAdapter);
        binding.orderRv.setLayoutManager(new LinearLayoutManager(OrderActivity.this));


    }

    private void statsChange(Order order) {

        if (order.isStats()) {
            startService(order.getName() + "stats :" + order.isStats());
        }
    }


    public void startService(String input) {

        Intent serviceIntent = new Intent(this, MyService.class);

        serviceIntent.putExtra("orderDetails", input);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, MyService.class);
        stopService(serviceIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);
        if (result != null) {
            Uri uri = Uri.parse(result.getContents());
            String ss = uri.getQueryParameter("orderId");
//            Log.d("TAG", "onCreate paaa: " + ss);

            getOrder(ss);

        }
    }

    @Override
    public void onOrderReady(Order order) {
        if (order != null) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), com.hinddev.pdf.o37i.instantapp.R.raw.notification);
            mediaPlayer.start();
            startService(order.getName() + " Order is Ready");
        } else {
            stopService();
            mediaPlayer.stop();
        }

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        adapter.startListening();
//
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        adapter.startListening();
//
//    }

    //        db.collection("Order")
//
//                .document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                        if (error != null) {
//                            Log.w("TAG", "Listen failed.", error);
//                            return;
//                        }
//
//                        if (value != null && value.exists()) {
//                            //Do what you need to do
//
//                            Order order = value.toObject(Order.class);
//                            //
//
//                            initAdapter();
//                            statsChange(order);
//
//
//                            Log.d("TAG6", "onEvent: "+order.isStats());
//                        }
//
//                    }
//                });

    //    private  void  addOrder(String id)
//    {
//        Query  query  = FirebaseFirestore.getInstance().collection("Order");
//
//
//        adapter = new OrdersAdapter(query ,id);
//        adapter.startListening();
//
//        binding.orderRv.setAdapter(adapter);
//        binding.orderRv.setLayoutManager(new LinearLayoutManager(OrderActivity.this));
//
//    }
    private void showInstantsInstall() {
        if (InstantApps.getPackageManagerCompat(this).isInstantApp()) {


            binding.btnInstall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent postInstall = new Intent();
                    InstantApps.showInstallPrompt(OrderActivity.this, postInstall, 100, "");
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // Branch init
        Branch.sessionBuilder(this).withCallback(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    Log.i("BRANCH SDK", referringParams.toString());
                    try {
                       String deepLink = referringParams.getString("$android_deeplink_path");
                        Uri uri = Uri.parse(deepLink);
                        String paramValue = uri.getQueryParameter("orderId");
                        if (paramValue!=null){
                            getOrder(paramValue);

                        }
//                        Log.i("BRANCH SDK $android_deeplink_path",  referringParams.getString("$android_deeplink_path"));
//                        Log.i("BRANCH SDK paramValue",  paramValue);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Retrieve deeplink keys from 'referringParams' and evaluate the values to determine where to route the user
                    // Check '+clicked_branch_link' before deciding whether to use your Branch routing logic
                } else {
                    Toast.makeText(OrderActivity.this, "error :" +error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.i("BRANCH SDK", error.getMessage());
                }
            }
        }).init();
//        Branch.getInstance().initSession(new Branch.BranchReferralInitListener() {
//            @Override
//            public void onInitFinished(JSONObject referringParams, BranchError error) {
//                if (error == null) {
//                    Log.i("BRANCH SDK", referringParams.toString());
//                    // Retrieve deeplink keys from 'referringParams' and evaluate the values to determine where to route the user
//                    // Check '+clicked_branch_link' before deciding whether to use your Branch routing logic
//                } else {
//                    Log.i("BRANCH SDK", error.getMessage());
//                }
//            }
//        }, this.getIntent().getData(), this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        this.setIntent(intent);

    }
}
