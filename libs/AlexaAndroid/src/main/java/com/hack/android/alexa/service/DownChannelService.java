package com.hack.android.alexa.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hack.android.alexa.AlexaManager;
import com.hack.android.alexa.TokenManager;
import com.hack.android.alexa.callbacks.ImplAsyncCallback;
import com.hack.android.alexa.connection.ClientUtil;
import com.hack.android.alexa.data.Directive;
import com.hack.android.alexa.data.Event;
import com.hack.android.alexa.interfaces.AvsItem;
import com.hack.android.alexa.interfaces.AvsResponse;
import com.hack.android.alexa.interfaces.response.ResponseParser;
import com.hack.android.alexa.system.AndroidSystemHandler;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSource;

/**
 * @author will on 4/27/2016.
 */
public class DownChannelService extends Service {

    private static final String TAG = "DownChannelService";

    private AlexaManager alexaManager;
    private Call currentCall;
    private AndroidSystemHandler handler;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "Launched");
        alexaManager = AlexaManager.getInstance(this);
        handler = AndroidSystemHandler.getInstance(this);

        openDownChannel();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(currentCall != null){
            currentCall.cancel();
        }
    }


    private void openDownChannel(){
        Log.d(TAG, "openDownChannel ");
        TokenManager.getAccessToken(alexaManager.getAuthorizationManager().getAmazonAuthorizationManager(), DownChannelService.this, new TokenManager.TokenCallback() {
            @Override
            public void onSuccess(String token) {
                Log.d(TAG, "openDownChannel onSuccess: ");
                Log.d(TAG, "openDownChannel onSuccess: ="+alexaManager.getDirectivesUrl());
                OkHttpClient downChannelClient = ClientUtil.getTLS12OkHttpClient();

                final Request request = new Request.Builder()
                        .url(alexaManager.getDirectivesUrl())
                        .get()
                        .addHeader("Authorization", "Bearer " + token)
                        .build();

                currentCall = downChannelClient.newCall(request);
                currentCall.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d(TAG, "openDownChannel onResponse: ");
                        alexaManager.sendEvent(Event.getSynchronizeStateEvent(), new ImplAsyncCallback<AvsResponse, Exception>() {
                            @Override
                            public void success(AvsResponse result) {
                                Log.d(TAG, "openDownChannel sendEvent success: ");
                                handler.handleItems(result);
                                sendHeartbeat();
                            }
                        });

                        BufferedSource bufferedSource = response.body().source();
                        Log.d(TAG, "openDownChannel Before bufferedSource "+bufferedSource.toString());
                        while (!bufferedSource.exhausted()) {
                            Log.d(TAG, "openDownChannel bufferedSource exhausted: ");
                            String line = bufferedSource.readUtf8Line();
                            Log.d(TAG, "openDownChannel readUtf8Line: "+line);
                            try {
                                Directive directive = ResponseParser.getDirective(line);
                                handler.handleDirective(directive);

                                //surface to our UI if it's up
                                try {
                                    AvsItem item = ResponseParser.parseDirective(directive);
                                    Log.d(TAG, "openDownChannel ResponseParser: " +
                                            ""+item);
//                                    EventBus.getDefault().post(item);
                                } catch (IOException e) {
                                    Log.e(TAG, "IOException");
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Bad line");
                            }
                        }
                        Log.d(TAG, "After bufferedSource ");

                    }
                });

            }

            @Override
            public void onFailure(Throwable e) {
                e.printStackTrace();
            }
        });
    }

    private void sendHeartbeat(){
        TokenManager.getAccessToken(alexaManager.getAuthorizationManager().getAmazonAuthorizationManager(), DownChannelService.this, new TokenManager.TokenCallback() {
            @Override
            public void onSuccess(String token) {

                Log.i(TAG, "Sending heartbeat");
                Log.d(TAG, "openDownChannel onSuccess: ="+alexaManager.getPingUrl());
                final Request request = new Request.Builder()
                        .url(alexaManager.getPingUrl())
                        .get()
                        .addHeader("Authorization", "Bearer " + token)
                        .build();

                ClientUtil.getTLS12OkHttpClient()
                        .newCall(request)
                        .enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {

                            }
                        });

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendHeartbeat();
                    }
                }, 4 * 60 * 1000);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.i(TAG, "Sending heartbeat onFailure");
            }
        });
    }
}