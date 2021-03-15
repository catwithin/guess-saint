package com.gamesofni.neko.guesswhichsaint.utils;

import com.gamesofni.neko.guesswhichsaint.R;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;

public class Utils {
    public static void checkSupportedAction (Context context, View view, Intent intent, String genericAppppName) {
        ComponentName app = intent.resolveActivity(context.getPackageManager());
        ComponentName unsupportedAction = ComponentName.unflattenFromString("com.android.fallback/.Fallback");
        if (app == null || app.equals(unsupportedAction)) {
            showNoAppClientsMsg(context, view, genericAppppName);
        }
    }

    public static void showNoAppClientsMsg(Context context, View view, String genericAppName) {
        showClientsMsg(
            context,
            view,
            context.getString(R.string.unsupported_action_begining) +
                genericAppName +
                context.getString(R.string.unsupported_action_ending)
        );
    }

    public static void showClientsMsg(Context context, View view, String msg) {
        if (msg == null) {
            msg = context.getString(R.string.unsupported_action_default_text);
        }
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG * 2)
                .show();
    }
}
