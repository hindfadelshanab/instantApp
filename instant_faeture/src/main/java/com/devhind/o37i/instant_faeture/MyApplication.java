package com.devhind.o37i.instant_faeture;

import android.app.Application;

import io.branch.referral.Branch;

public class MyApplication  extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // This tells the Branch initialization how long to wait for the Google Play Referrer before proceeding. (Default: 1.5 second)
        Branch.setPlayStoreReferrerCheckTimeout(1000L);

        // Branch logging for debugging
        Branch.enableLogging();

        // Initialize the Branch SDK
        Branch.getAutoInstance(this);
    }

}