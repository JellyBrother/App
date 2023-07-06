package com.example.myapplication;

public class Hello {
    static {
        System.loadLibrary("myapplication");
    }

    public static String getNative(int num) {
        Hello hello = new Hello();
        return "bbbb+" + hello.stringFromJNI() + num;
    }

    public native String stringFromJNI();
}
