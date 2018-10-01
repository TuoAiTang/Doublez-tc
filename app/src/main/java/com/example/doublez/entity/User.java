package com.example.doublez.entity;

import android.graphics.Bitmap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class User extends DataSupport implements Serializable
{
    //实现Serializable接口是为了能在Login中直接传递查询出来的user对象给MainActivity
    private int id;
    private String email;
    private String password;
    private String avatar;

    public User(String email,String password,String avatar)
    {
        this.email=email;
        this.password=password;
        this.avatar=avatar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
