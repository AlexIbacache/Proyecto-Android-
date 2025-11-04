package com.example.proyectoandroid.util;

import android.util.Patterns;

public class Validators {
    public static boolean isEmailValid(CharSequence email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPasswordValid(CharSequence password) {
        return password != null && password.length() > 5;
    }
}
