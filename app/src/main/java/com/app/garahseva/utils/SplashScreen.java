package com.app.garahseva.utils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.app.garahseva.BuildConfig;
import com.app.garahseva.R;
import com.app.garahseva.activities.MainActivity;
import com.app.garahseva.activities.myAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.jetbrains.annotations.NotNull;

public class SplashScreen extends AppCompatActivity {

    FirebaseRemoteConfig firebaseRemoteConfig;
    AlertDialog updateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setNavigationBarColor(Color.parseColor("#ff5733"));

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings.Builder configBuilder = new FirebaseRemoteConfigSettings.Builder();


        if (BuildConfig.DEBUG) {
            long cacheInterval = 0;
            configBuilder.setMinimumFetchIntervalInSeconds(cacheInterval);
        }

        firebaseRemoteConfig.setConfigSettingsAsync(configBuilder.build());

        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        firebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult())
                                checkForUpdate();
                            else
                                moveToMainAct();
                        } else
                            moveToMainAct();
                    }
                });


    }

    private void checkForUpdate() {
        String appVersion;
        try {
            appVersion = SplashScreen.this.getPackageManager().getPackageInfo(SplashScreen.this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            appVersion = "1.0";
            e.printStackTrace();
        }
        if (appVersion.compareTo(firebaseRemoteConfig.getString("min_version_of_app")) < 0) {
            //mandatory update
            AlertDialog.Builder updateAlert = new AlertDialog.Builder(SplashScreen.this);
            View view = getLayoutInflater().inflate(R.layout.update_app_dialog, null);

            Button updBtn = view.findViewById(R.id.update);
            Button skipBtn = view.findViewById(R.id.skip);
            skipBtn.setVisibility(View.GONE);
            updBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent rateIntent = IntentForUrl("market://details");
                        startActivity(rateIntent);
                    } catch (ActivityNotFoundException e) {
                        Intent rateIntent = IntentForUrl("https://play.google.com/store/apps/details");
                        startActivity(rateIntent);

                    }
                }
            });
            updateAlert.setView(view);
            updateDialog = updateAlert.create();
            updateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            updateDialog.setCanceledOnTouchOutside(false);
            updateDialog.setCancelable(false);
            updateDialog.show();

        } else if (appVersion.compareTo(firebaseRemoteConfig.getString("latest_version_of_app")) < 0) {
            //not mandatory
            AlertDialog.Builder updateAlert = new AlertDialog.Builder(SplashScreen.this);
            View view = getLayoutInflater().inflate(R.layout.update_app_dialog, null);

            Button updBtn = view.findViewById(R.id.update);
            Button skipBtn = view.findViewById(R.id.skip);
            skipBtn.setVisibility(View.VISIBLE);
            updBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent rateIntent = IntentForUrl("market://details");
                        startActivity(rateIntent);
                    } catch (ActivityNotFoundException e) {
                        Intent rateIntent = IntentForUrl("https://play.google.com/store/apps/details");
                        startActivity(rateIntent);
                    }
                    updateDialog.cancel();
                }
            });
            skipBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateDialog.cancel();
                    moveToMainAct();
                }
            });
            updateAlert.setView(view);
            updateDialog = updateAlert.create();
            updateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            updateDialog.setCanceledOnTouchOutside(false);
            updateDialog.setCancelable(false);
            updateDialog.show();
        } else
            moveToMainAct();

    }

    private Intent IntentForUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        intent.addFlags(flags);
        return intent;
    }

    void moveToMainAct() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
            }
        }, 500);
    }

}