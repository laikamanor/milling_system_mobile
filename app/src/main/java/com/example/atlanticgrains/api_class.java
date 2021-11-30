package com.example.atlanticgrains;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;

import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class api_class {
    String gURL = "",
    gMethod = "",
    gBody = "",
    gIpAddress = "";
    boolean gIsNeedBearerToken = false;
    Context mCOntext;
    public api_class(String url, String method, String body, String ipAddress,boolean isNeedBearerToken,Context context){
        gURL = url;
        gMethod = method;
        gBody = body;
        gIpAddress = ipAddress;
        gIsNeedBearerToken = isNeedBearerToken;
        mCOntext = context;
    }

    public String getResponse(){
        try{
            if (android.os.Build.VERSION.SDK_INT > 9)
            {
                StrictMode.ThreadPolicy policy = new
                        StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            String bearerToken = "";
            if(gIsNeedBearerToken){
                SharedPreferences sharedPreferences = mCOntext.getSharedPreferences("TOKEN", MODE_PRIVATE);
                bearerToken = Objects.requireNonNull(sharedPreferences.getString("token", ""));
            }
            OkHttpClient client;
            client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, gBody);
            okhttp3.Request request = null;
            if(gIsNeedBearerToken) {
                request = new okhttp3.Request.Builder()
                        .url(gIpAddress + gURL)
                        .method(gMethod, gMethod.equals("GET") ? null : body)
                        .addHeader("Authorization", "Bearer " + bearerToken)
                        .build();
            }else {
                request = new okhttp3.Request.Builder()
                        .url(gIpAddress + gURL)
                        .method(gMethod, gMethod.equals("GET") ? null : body)
                        .build();
            }

            Response response;
            response = client.newCall(request).execute();
            return response.body().string();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return ex.toString();
        }
    }
}
