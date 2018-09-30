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
    private byte[] avatar;

    public User(String email,String password,byte[] avatar)
    {
        this.email=email;
        this.password=password;
        this.avatar=avatar;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public String getEmail()
    {
        return this.email;
    }

    public String getPassword()
    {
        return this.password;
    }

    public byte[] getAvatar()
    {
        return this.avatar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
