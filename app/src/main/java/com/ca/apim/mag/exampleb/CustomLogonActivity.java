/*
 * Created by awitrisna on 2013-11-15.
 * Copyright (c) 2013 CA Technologies. All rights reserved.
 */

package com.ca.apim.mag.exampleb;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.l7tech.msso.auth.QRCode;
import com.l7tech.msso.auth.QRCodeRenderer;
import com.l7tech.msso.gui.AbstractLogonActivity;

import java.util.List;

public class CustomLogonActivity extends AbstractLogonActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customlogon);
        final Button logonButton = (Button) findViewById(R.id.btnlogin);
        if (isEnterpriseLoginEnabled()) {
            logonButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String username = ((EditText) findViewById(R.id.etxtname)).getText().toString();
                    final String password = ((EditText) findViewById(R.id.etxtpass)).getText().toString();

                    sendCredentialsIntent(username, password);

                    setResult(RESULT_OK);
                    finish();
                }
            });
        } else {
            logonButton.setEnabled(false);
        }

        final LinearLayout qr = (LinearLayout) findViewById(R.id.qrcode);

        setAuthRenderer((new QRCodeRenderer() {
            @Override
            public void onError(int code, final String m, Exception e) {
                super.onError(code, m, e);
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, m, Toast.LENGTH_LONG).show();
                        qr.removeAllViews();
                    }
                });
            }

            @Override
            protected long getDelay() {
                return 5;
            }

            @Override
            protected long getPollInterval() {
                //Not recommended to poll the server too often. Recommend to poll the server 5+s
                return 2;
            }

        }));

        addAuthRenderer(new NFCRenderer());

        List<View> providers = getProviders();

        GridLayout gridLayout = (GridLayout) findViewById(R.id.socialLoginGridLayout);

        if (!providers.isEmpty()) {
            for (final View provider : providers) {
                if (provider instanceof ImageButton) {
                    gridLayout.addView(provider);
                } else if (provider instanceof QRCode) {
                    qr.addView(provider);
                }
            }
        }
    }
}