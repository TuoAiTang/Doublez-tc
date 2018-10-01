package com.example.doublez.util;

import com.example.doublez.entity.User;

public class GlobalData {

    private static User user;

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        GlobalData.user = user;
    }
}
