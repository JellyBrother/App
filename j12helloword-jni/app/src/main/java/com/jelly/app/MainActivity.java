package com.jelly.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.d08a3hqr.chtjikfd.R;
import com.jelly.app.base.load.Start;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
//        initInstallReferrerClient();
    }

    private void initView() {
        setContentView(R.layout.apk_activity_main);
        findViewById(R.id.tvw1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadB();
            }
        });
        findViewById(R.id.tvw2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initInstallReferrerClient();
            }
        });
        findViewById(R.id.tvw3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadA();
            }
        });
    }

    private void initInstallReferrerClient() {
        InstallReferrerClient referrerClient = InstallReferrerClient.newBuilder(this).build();
        referrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                    try {
                        ReferrerDetails response = referrerClient.getInstallReferrer();
                        String referrerUrl = response.getInstallReferrer();
                        if (TextUtils.isEmpty(referrerUrl)) {
                            loadA();
                        } else {
                            if (referrerUrl.contains("utm_medium=organic")) {
                                loadA();
                            } else {
                                //非自然并且是印度卡进b面
                                if (isIndia()) {
                                    loadB();
                                } else {
                                    loadA();
                                }
                            }
                        }
                    } catch (Throwable e) {
                        loadA();
                        e.printStackTrace();
                    }
                } else {
                    loadA();
                }
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                loadA();
            }
        });
    }

    private boolean isIndia() {
        TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (telManager == null) {
            return false;
        }
        String operator = telManager.getSimOperator();
        if (operator != null && !operator.isEmpty()) {
            String mcc = operator.substring(0, 3);
            if (mcc.equals("404") || mcc.equals("405") || mcc.equals("406")) {
                return true;
            }
        }
        return false;
    }

    private void loadA() {
        Start.attachBaseContext(this, "mx123---", "a");
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), "com.cocos.game.AppActivity");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void loadB() {
        Start.attachBaseContext(this, "mx123+++", "b");
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), "com.d08a3hqr.chtjikfd.CkiOFmJI");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
