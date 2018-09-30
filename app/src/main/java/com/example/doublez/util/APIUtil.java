package com.example.doublez.util;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;

import com.example.doublez.entity.BaseInfo;
import com.example.doublez.entity.User;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APIUtil {

    public static void invokeLoginAPI(final Handler handler, String email,
                                      String password){
        String api_url = "http://123.207.163.104/Doublez/user.do";
        RequestBody requestBody = new FormBody.Builder()
                .add("method","login")
                .add("email", email)
                .add("password",password)
                .build();
        HttpUtil.sendOkHttpRequest(api_url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Gson gson = new Gson();
                BaseInfo baseInfo = gson.fromJson(responseData, BaseInfo.class);
                Message message = new Message();
                message.obj = baseInfo;
                handler.sendMessage(message);

            }
        },requestBody);
    }

    public static void invokeRegisterAPI(final Handler handler,
                                 String email, String password, String avatar){
        String api_url = "http://123.207.163.104/Doublez/user.do";
        RequestBody requestBody = new FormBody.Builder()
                .add("method","register")
                .add("email", email)
                .add("password",password)
                .add("avatar", avatar)
                .build();
        HttpUtil.sendOkHttpRequest(api_url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Gson gson = new Gson();
                BaseInfo baseInfo = gson.fromJson(responseData, BaseInfo.class);
                Message message = new Message();
                message.obj = baseInfo;
                handler.sendMessage(message);
            }
        }, requestBody);
    }

    public  static void invokeGetUserAPI(final Handler handler, String email){
        String api_url = "http://123.207.163.104/Doublez/user.do";
        RequestBody requestBody = new FormBody.Builder()
                .add("method", "getuser")
                .add("email", email)
                .build();
        HttpUtil.sendOkHttpRequest(api_url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Gson gson = new Gson();
                User user = gson.fromJson(responseData, User.class);
                Message message = new Message();
                message.obj = user;
                handler.sendMessage(message);
            }
        }, requestBody);
    }

}
