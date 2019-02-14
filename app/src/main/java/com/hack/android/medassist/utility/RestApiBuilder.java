package com.hack.android.medassist.utility;

import android.os.AsyncTask;

import com.hack.android.medassist.interfaces.HttpResponseInterface;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RestApiBuilder extends AsyncTask<String, String, String> {
    private Exception exception;
    private OkHttpClient mClient;;
    private HttpResponseInterface mHttpResponseInterface;
    public RestApiBuilder(OkHttpClient client, HttpResponseInterface  httpResponseInterface) {
        mClient = client;
        mHttpResponseInterface = httpResponseInterface;
    }
    protected String doInBackground(String... urls) {
        try {
            String getResponse = get(urls[0]);
            return getResponse;
        } catch (Exception e) {
            this.exception = e;
            return null;
        }
    }

    protected void onPostExecute(String getResponse) {
        System.out.println(getResponse);
        mHttpResponseInterface.getResponseBody(getResponse);
    }

    public String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }
}