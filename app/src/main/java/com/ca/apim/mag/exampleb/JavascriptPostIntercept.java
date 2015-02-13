package com.ca.apim.mag.exampleb;

import android.content.res.Resources;
import android.webkit.JavascriptInterface;

import com.google.zxing.common.StringUtils;
import com.l7tech.msso.io.IoUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by milst08 on 11.02.2015.
 */
    public class JavascriptPostIntercept {

        private static String sInterceptHeader;
        private JavascriptPostInterceptInterface mClient;

        public JavascriptPostIntercept(JavascriptPostInterceptInterface client) {
            mClient = client;
        }

        public static String getInterceptHeader(InputStream in) {
            if (sInterceptHeader == null) {
                // Assuming you have your own stream to string implementation
                sInterceptHeader =  convertStreamToString(in);


            }
              return sInterceptHeader;
        }

        @JavascriptInterface
        public void customAjax(final String method, final String body) {
            mClient.nextMessageIsAjaxRequest(new AjaxRequestContents(method, body));
        }

        @JavascriptInterface
        public void customSubmit(String json, String method, String enctype) {
            mClient.nextMessageIsFormRequest(new FormRequestContents(json, method, enctype));
        }

        public interface JavascriptPostInterceptInterface {
            public void nextMessageIsAjaxRequest(AjaxRequestContents contents);
            public void nextMessageIsFormRequest(FormRequestContents contents);
        }

        public static class AjaxRequestContents {
            private String mMethod;
            private String mBody;

            public AjaxRequestContents(String method, String body) {
                mMethod = method;
                mBody = body;
            }

            public String getMethod() {
                return mMethod;
            }

            public String getBody() {
                return mBody;
            }
        }

        public static class FormRequestContents {
            private String mJson;
            private String mMethod;
            private String mEnctype;

            public FormRequestContents(String json, String method, String enctype) {
                mJson = json;
                mMethod = method;
                mEnctype = enctype;
            }

            public String getJson() {
                return mJson;
            }

            public String getMethod() {
                return mMethod;
            }

            public String getEnctype() {
                return mEnctype;
            }
        }
    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }





    }
