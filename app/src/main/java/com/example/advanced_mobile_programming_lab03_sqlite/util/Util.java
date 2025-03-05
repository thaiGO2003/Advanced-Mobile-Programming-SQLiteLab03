package com.example.advanced_mobile_programming_lab03_sqlite.util;

import android.content.Context;
import android.widget.Toast;

public class Util {

    public static void showToast(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast.show();
    }
}