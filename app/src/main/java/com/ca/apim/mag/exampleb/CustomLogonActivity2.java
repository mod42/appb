/*
 * Created by awitrisna on 2013-11-15.
 * Copyright (c) 2013 CA Technologies. All rights reserved.
 */

package com.ca.apim.mag.exampleb;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.l7tech.msso.auth.QRCode;
import com.l7tech.msso.auth.QRCodeRenderer;
import com.l7tech.msso.gui.AbstractLogonActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.InputStream;
import java.util.List;

public class CustomLogonActivity2 extends AbstractLogonActivity {

    //private Button button;
    private WebView webView;
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.webviewlayout);

        //Get webview
        webView = (WebView) findViewById(R.id.webview1);

        startWebView("http://explore.apim.ca:8080/test/login");

    }

    class MyWebViewClient extends WebViewClient implements JavascriptPostIntercept.JavascriptPostInterceptInterface {

        ProgressDialog progressDialog;
        private String mLastRequestMethod = "";


        //If you will not use this method url links are opeen in new brower not in webview
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        public void onPageFinished(WebView view, String url) {
            //getResources().getAssets().openRawResource()
            InputStream in = getResources().openRawResource(R.raw.post_inter);
            view.loadUrl("javascript: " + JavascriptPostIntercept.getInterceptHeader(in));
            try {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }


        //Show loader on url load
        public void onLoadResource(WebView view, String url) {
            if (progressDialog == null) {
                // in standard case YourActivity.this
                progressDialog = new ProgressDialog(CustomLogonActivity2.this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
            }
        }
        @TargetApi(11)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            //return super.shouldInterceptRequest(view, url);
            Log.d("mywebview", "intercepted");
            if (mLastRequestMethod.equals("POST")) {
                Log.d("mywebview", "POST");
            } else if (mLastRequestMethod.equals("GET")) {
                Log.d("mywebview", "GET");
            }

            return null;
        }

        @Override
        public void nextMessageIsAjaxRequest(JavascriptPostIntercept.AjaxRequestContents contents) {
            mLastRequestMethod = contents.getMethod();
            Log.d("mywebview", "Ajax");
        }

        @Override
        public void nextMessageIsFormRequest(JavascriptPostIntercept.FormRequestContents contents) {
            mLastRequestMethod = contents.getMethod();
            String username = "";
            String password = "";
            Log.d("mywebview", "forms"  + contents.getJson());
            try {
                JSONArray json = new JSONArray(contents.getJson());
                username = json.getJSONObject(0).getString("value");
                password = json.getJSONObject(1).getString("value");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("mywebview", "username "  + username + " password " + password);
            sendCredentialsIntent(username, password);
            setResult(RESULT_OK);
            finish();
        }
    }


    private void startWebView(String url) {

        //Create new webview Client to show progress dialog
        //When opening a url or click on link
        MyWebViewClient webViewClient = new MyWebViewClient();

        webView.setWebViewClient(webViewClient);
        // Javascript enabled on webview
        webView.getSettings().setJavaScriptEnabled(true);

        webView.addJavascriptInterface(new JavascriptPostIntercept(webViewClient), "interception");

        // Other webview options
        /*
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.getSettings().setBuiltInZoomControls(true);
        */

        /*
         String summary = "<html><body>You scored <b>192</b> points.</body></html>";
         webview.loadData(summary, "text/html", null);
         */

        //Load url in webview
        webView.loadUrl(url);


    }

    // Open previous opened link from history on webview when back button pressed

    @Override
    // Detect when the back button is pressed
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            // Let the system handle the back button
            super.onBackPressed();
        }
    }
/*
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customlogon2);
        final Button logonButton = (Button) findViewById(R.id.btnlogin2);
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
    }*/
}